"""Async Smallville simulation orchestrator.

Drives a compressed multi-day simulation via the HTTP API with:
  - Parallel per-tick artifact fan-out (aiohttp + asyncio.gather)
  - Jittered exponential backoff with circuit breaker
  - Crash-safe checkpoint/resume via manifest.json
  - Inline policy pass for auto-approve/reject/escalate of proposals
  - Frame index in manifest.json for O(1) replay metadata lookups
  - Structured per-tick metrics

Requires: aiohttp>=3.9 (bootstrap with scripts/bootstrap-python-sidecar.ps1)
Optional: pyyaml>=6.0 for YAML policy files (included in requirements-sidecar.txt)

Bootstrap the repo-local sidecar venv:
    powershell -ExecutionPolicy Bypass -File .\\scripts\\bootstrap-python-sidecar.ps1

Usage (near-live interactive mode):
    .\\.venv\\Scripts\\python.exe .\\scripts\\run_three_day_sim.py --mode interactive

Usage (overnight soak with auto-governance):
    .\\.venv\\Scripts\\python.exe .\\scripts\\run_three_day_sim.py --mode overnight --allow-auto-govern --policy .\\scripts\\proposal_policy.yaml

Usage (resume after pause/crash):
    .\\.venv\\Scripts\\python.exe .\\scripts\\run_three_day_sim.py --resume --output-dir runs/<run-id>
"""
from __future__ import annotations

import argparse
import asyncio
import gzip
import hashlib
import json
import math
import random
import sys
import time
from collections import defaultdict
from dataclasses import asdict, dataclass
from datetime import datetime, timezone
from pathlib import Path
from typing import Any
from urllib.parse import quote, urlencode

try:
    import aiohttp
except ImportError:
    print(
        'aiohttp is required for the Python sidecar path.\n'
        'Bootstrap the repo venv with:\n'
        '    powershell -ExecutionPolicy Bypass -File .\\scripts\\bootstrap-python-sidecar.ps1\n'
        'Then run:\n'
        '    .\\.venv\\Scripts\\python.exe .\\scripts\\run_three_day_sim.py --help',
        file=sys.stderr,
    )
    sys.exit(1)

# ---------------------------------------------------------------------------
# Constants
# ---------------------------------------------------------------------------

_MAX_RETRIES = 3
_BACKOFF_BASE_SECONDS = 2.0
_BACKOFF_JITTER = 0.2          # ±20% of base delay
_CIRCUIT_BREAKER_THRESHOLD = 5  # consecutive failures before open
_POLICY_TRACE_SCHEMA_VERSION = 1
_POLICY_TRACE_FILENAME = 'policy_trace.json'
_POLICY_LOG_FILENAME = 'policy_log.json'
_DEFAULT_POLICY: dict[str, Any] = {
    'review_window_hours': 24,
    'labor_days_per_agent_per_day': 1.0,
    'max_autoapproved_child_additions_per_parent_per_day': 1,
    'max_autoapproved_additions_per_agent_per_day': 1,
    'auto_approve': ['add_location', 'add_object', 'change_state'],
    'auto_reject': ['delete', 'destroy'],
    'forbidden_names': ['destroy', 'kill', 'nuke', 'demolish', 'burn down'],
    'effort_costs': {
        'add_location': 0.5,
        'add_object': 0.25,
        'change_state': 0.25,
        'room_scale_location': 1.0,
        'building_scale_location': 3.0,
    },
    'keywords': {
        'building_scale': ['house', 'cottage', 'cabin', 'barn', 'garage', 'studio', 'workshop'],
        'room_scale': ['room', 'kitchen', 'study', 'bedroom', 'hall', 'office', 'library', 'lounge'],
    },
    'alternative_suggestions': {
        'garden': ['Raised Planter', 'Potting Bench', 'Tool Shed'],
        'green house': ['Seed Rack', 'Potting Bench', 'Water Shelf'],
        'house': ['Reading Nook', 'Tea Shelf', 'Storage Nook'],
        'default': ['Storage Nook', 'Bench Alcove', 'Tool Shelf'],
    },
}

_MODE_PRESETS: dict[str, dict[str, Any]] = {
    'interactive': {
        'days': 1.0,
        'timestep_minutes': 60,
        'pause_seconds': 0.0,
    },
    'overnight': {
        'days': 3.0,
        'timestep_minutes': 120,
        'pause_seconds': 0.5,
    },
}

# ---------------------------------------------------------------------------
# Utility helpers
# ---------------------------------------------------------------------------


def utc_now() -> str:
    return datetime.now(timezone.utc).isoformat()


def get_tick_count(days: float, timestep_minutes: int) -> int:
    return int(math.ceil(days * 24 * 60 / timestep_minutes))


def _jitter(base: float) -> float:
    return base * _BACKOFF_JITTER * (random.random() * 2 - 1)


def backoff_delay(attempt: int) -> float:
    base = _BACKOFF_BASE_SECONDS * (2 ** attempt)
    return max(0.1, base + _jitter(base))


def write_json_sync(path: Path, payload: Any) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(payload, indent=2), encoding='utf-8')


def write_text_sync(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding='utf-8')


def write_json_gzip_sync(path: Path, payload: Any) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with gzip.open(path, 'wt', encoding='utf-8') as handle:
        json.dump(payload, handle, indent=2)


async def write_json(path: Path, payload: Any) -> None:
    loop = asyncio.get_event_loop()
    await loop.run_in_executor(None, write_json_sync, path, payload)


async def write_text(path: Path, content: str) -> None:
    loop = asyncio.get_event_loop()
    await loop.run_in_executor(None, write_text_sync, path, content)


async def write_json_gzip(path: Path, payload: Any) -> None:
    loop = asyncio.get_event_loop()
    await loop.run_in_executor(None, write_json_gzip_sync, path, payload)


# ---------------------------------------------------------------------------
# Async HTTP client with retry + circuit breaker
# ---------------------------------------------------------------------------


class SmallvilleClient:
    """Async Smallville HTTP client with connection pooling, retry/backoff, and circuit breaker."""

    def __init__(self, session: aiohttp.ClientSession, base_url: str) -> None:
        self._session = session
        self._base_url = base_url.rstrip('/')
        self._consecutive_failures = 0

    def _check_circuit(self) -> None:
        if self._consecutive_failures >= _CIRCUIT_BREAKER_THRESHOLD:
            raise RuntimeError(
                f'Circuit breaker open after {self._consecutive_failures} consecutive failures. '
                'Check the backend and resume with --resume.'
            )

    def _on_success(self) -> None:
        self._consecutive_failures = 0

    def _on_failure(self) -> None:
        self._consecutive_failures += 1

    async def _request_json(
        self, method: str, path: str, payload: dict[str, Any] | None = None
    ) -> Any:
        self._check_circuit()
        url = f'{self._base_url}{path}'
        last_exc: BaseException = RuntimeError('No attempts made')

        for attempt in range(_MAX_RETRIES + 1):
            try:
                if method == 'GET':
                    async with self._session.get(url) as resp:
                        resp.raise_for_status()
                        data = await resp.json(content_type=None)
                else:
                    async with self._session.post(url, json=payload or {}) as resp:
                        resp.raise_for_status()
                        data = await resp.json(content_type=None)
                self._on_success()
                return data
            except Exception as exc:
                last_exc = exc
                self._on_failure()
                if attempt < _MAX_RETRIES:
                    delay = backoff_delay(attempt)
                    print(
                        f'  [retry {attempt + 1}/{_MAX_RETRIES}] {method} {path} failed: {exc!r}. '
                        f'Waiting {delay:.1f}s…'
                    )
                    await asyncio.sleep(delay)

        raise last_exc

    async def _request_text(self, path: str, params: dict[str, str] | None = None) -> str:
        self._check_circuit()
        url = f'{self._base_url}{path}'
        if params:
            url = f'{url}?{urlencode(params)}'
        last_exc: BaseException = RuntimeError('No attempts made')

        for attempt in range(_MAX_RETRIES + 1):
            try:
                async with self._session.get(url) as resp:
                    resp.raise_for_status()
                    text = await resp.text()
                    self._on_success()
                    return text
            except Exception as exc:
                last_exc = exc
                self._on_failure()
                if attempt < _MAX_RETRIES:
                    delay = backoff_delay(attempt)
                    await asyncio.sleep(delay)

        raise last_exc

    async def get_json(self, path: str) -> Any:
        return await self._request_json('GET', path)

    async def post_json(self, path: str, payload: dict[str, Any] | None = None) -> Any:
        return await self._request_json('POST', path, payload)

    async def get_text(self, path: str, params: dict[str, str] | None = None) -> str:
        return await self._request_text(path, params)

    async def approve_proposal(self, proposal_id: str) -> Any:
        return await self._request_json('POST', f'/world/proposals/{proposal_id}/approve', {})

    async def reject_proposal(self, proposal_id: str) -> Any:
        return await self._request_json('POST', f'/world/proposals/{proposal_id}/reject', {})


# ---------------------------------------------------------------------------
# Parallel artifact capture
# ---------------------------------------------------------------------------


def coerce_snapshot(snapshot: dict[str, Any]) -> dict[str, Any]:
    snapshot.setdefault('pendingProposals', [])
    snapshot.setdefault('agents', [])
    snapshot.setdefault('locations', [])
    snapshot.setdefault('time', 'Unavailable')
    snapshot.setdefault('step', 0)
    return snapshot


def sanitize_filename(value: str) -> str:
    normalized = ''.join(ch if ch.isalnum() or ch in '._-' else '_' for ch in (value or '').strip())
    return normalized or 'unknown-agent'


async def save_world_artifacts(
    client: SmallvilleClient,
    output_dir: Path,
    name: str,
    title: str,
    tick_metrics: dict[str, Any],
) -> dict[str, Any]:
    """Fetch world, proposals, models, state, and SVG in parallel; write all to disk."""
    t_fetch = time.perf_counter()

    results = await asyncio.gather(
        client.get_json('/world'),
        client.get_json('/world/proposals'),
        client.get_json('/models'),
        client.get_json('/state'),
        client.get_text('/world/visual-map', {'title': title}),
        return_exceptions=True,
    )

    tick_metrics['artifact_fetch_ms'] = int((time.perf_counter() - t_fetch) * 1000)

    world_raw, proposals_raw, models_raw, state_raw, svg = results

    snapshot = coerce_snapshot(
        world_raw if not isinstance(world_raw, BaseException) else {}
    )
    if isinstance(proposals_raw, BaseException):
        proposals_raw = {}
    if isinstance(models_raw, BaseException):
        models_raw = []
    if isinstance(state_raw, BaseException):
        state_raw = {}
    if isinstance(svg, BaseException):
        svg = '<svg xmlns="http://www.w3.org/2000/svg"><text y="20">Map unavailable</text></svg>'

    t_write = time.perf_counter()
    await asyncio.gather(
        write_json(output_dir / f'{name}_world.json', snapshot),
        write_json(output_dir / f'{name}_proposals.json', proposals_raw),
        write_json(output_dir / f'{name}_models.json', models_raw),
        write_json(output_dir / f'{name}_state.json', state_raw),
        write_text(output_dir / f'{name}_world_map.svg', svg),
    )
    tick_metrics['artifact_write_ms'] = int((time.perf_counter() - t_write) * 1000)

    return snapshot


async def save_ledger_export_artifacts(
    client: SmallvilleClient,
    output_dir: Path,
    snapshot_label: str | None = None,
) -> dict[str, Any]:
    export_payload = await client.get_json('/world/ledger/export')

    writes = [
        write_json(output_dir / 'ledger_export.json', export_payload),
        write_json(output_dir / 'governance_ledger.json', export_payload.get('governanceLedger', {})),
        write_json(output_dir / 'memory_index.json', export_payload.get('memoryIndex', {})),
    ]

    snapshot_root: Path | None = None
    if snapshot_label:
        snapshot_root = output_dir / 'ledger_snapshots' / snapshot_label
        writes.extend([
            write_json(snapshot_root / 'ledger_export.json', export_payload),
            write_json(snapshot_root / 'governance_ledger.json', export_payload.get('governanceLedger', {})),
            write_json(snapshot_root / 'memory_index.json', export_payload.get('memoryIndex', {})),
        ])

    agent_names = [
        str(agent.get('name') or '')
        for agent in export_payload.get('world', {}).get('agents', [])
        if str(agent.get('name') or '').strip()
    ]

    agent_ledgers = await asyncio.gather(
        *[
            client.get_json(f"/agents/{quote(agent_name, safe='')}/memory-ledger")
            for agent_name in agent_names
        ],
        return_exceptions=True,
    )

    for agent_name, ledger in zip(agent_names, agent_ledgers):
        if isinstance(ledger, BaseException):
            continue

        slug = sanitize_filename(agent_name)
        writes.append(write_json(output_dir / 'memory_ledgers' / f'{slug}.json', ledger))
        if snapshot_root is not None:
            writes.append(write_json(snapshot_root / 'memory_ledgers' / f'{slug}.json', ledger))

        for pack in ledger.get('dreamPacks', []):
            pack_id = sanitize_filename(str(pack.get('id') or 'dream-pack'))
            writes.append(write_json_gzip(output_dir / 'dream_packs' / slug / f'{pack_id}.json.gz', pack))
            if snapshot_root is not None:
                writes.append(write_json_gzip(snapshot_root / 'dream_packs' / slug / f'{pack_id}.json.gz', pack))

    await asyncio.gather(*writes)
    return export_payload


# ---------------------------------------------------------------------------
# Policy engine + replay/export traces
# ---------------------------------------------------------------------------


def merge_policy_config(raw_policy: dict[str, Any] | None) -> dict[str, Any]:
    merged = json.loads(json.dumps(_DEFAULT_POLICY))
    raw_policy = raw_policy or {}

    for key in (
        'review_window_hours',
        'labor_days_per_agent_per_day',
        'max_autoapproved_child_additions_per_parent_per_day',
        'max_autoapproved_additions_per_agent_per_day',
    ):
        if key in raw_policy:
            merged[key] = raw_policy[key]

    for key in ('auto_approve', 'auto_reject', 'forbidden_names'):
        if key in raw_policy:
            merged[key] = list(raw_policy.get(key) or [])

    for key in ('effort_costs', 'keywords', 'alternative_suggestions'):
        if key in raw_policy and isinstance(raw_policy[key], dict):
            merged[key].update(raw_policy[key])

    return merged


def load_policy(policy_path: str) -> dict[str, Any]:
    if not policy_path:
        return merge_policy_config({})

    path = Path(policy_path)
    if not path.exists():
        print(f'[policy] Policy file not found: {policy_path!r}', file=sys.stderr)
        return merge_policy_config({})

    raw = path.read_text(encoding='utf-8')
    parsed: dict[str, Any] = {}
    try:
        import yaml
        parsed = yaml.safe_load(raw) or {}
    except ImportError:
        try:
            parsed = json.loads(raw)
        except Exception as exc:
            print(f'[policy] Failed to parse policy file: {exc}', file=sys.stderr)
            parsed = {}
    except Exception as exc:
        print(f'[policy] Failed to parse policy file: {exc}', file=sys.stderr)
        parsed = {}

    return merge_policy_config(parsed)


def normalize_lower(value: Any) -> str:
    return str(value or '').strip().lower()


def get_simulated_day_index(tick_index: int, timestep_minutes: int) -> int:
    if tick_index <= 0:
        return 0
    minutes_elapsed_before_tick = max(0, (tick_index - 1) * timestep_minutes)
    return minutes_elapsed_before_tick // (24 * 60)


def is_addition_type(proposal_type: str) -> bool:
    return proposal_type in {'add_location', 'add_object'}


def estimate_proposal_effort(proposal: dict[str, Any], policy: dict[str, Any]) -> tuple[float, str]:
    proposal_type = normalize_lower(proposal.get('type'))
    effort_costs = policy.get('effort_costs', {})
    keywords = policy.get('keywords', {})
    name_blob = ' '.join(
        normalize_lower(value)
        for value in (
            proposal.get('name'),
            proposal.get('targetLocation'),
            proposal.get('parentLocation'),
        )
    )

    if proposal_type == 'add_location':
        if any(keyword in name_blob for keyword in keywords.get('building_scale', [])):
            return float(effort_costs.get('building_scale_location', 3.0)), 'building_scale_location'
        if any(keyword in name_blob for keyword in keywords.get('room_scale', [])):
            return float(effort_costs.get('room_scale_location', 1.0)), 'room_scale_location'
        return float(effort_costs.get('add_location', 0.5)), 'add_location'

    if proposal_type == 'add_object':
        return float(effort_costs.get('add_object', 0.25)), 'add_object'

    if proposal_type == 'change_state':
        return float(effort_costs.get('change_state', 0.25)), 'change_state'

    return float(effort_costs.get('change_state', 0.25)), 'fallback'


def suggest_alternative(proposal: dict[str, Any], policy: dict[str, Any]) -> str | None:
    suggestions = policy.get('alternative_suggestions', {})
    parent = str(proposal.get('parentLocation') or '').strip()
    parent_key = normalize_lower(parent)

    candidate_group = suggestions.get('default', [])
    if 'garden' in parent_key:
        candidate_group = suggestions.get('garden', candidate_group)
    elif 'green house' in parent_key:
        candidate_group = suggestions.get('green house', candidate_group)
    elif 'house' in parent_key:
        candidate_group = suggestions.get('house', candidate_group)

    current_name = normalize_lower(proposal.get('name'))
    for candidate in candidate_group:
        if normalize_lower(candidate) != current_name:
            if parent:
                return f'Consider "{candidate}" under {parent} instead.'
            return f'Consider "{candidate}" instead.'
    return None


def init_policy_trace_root(policy: dict[str, Any]) -> dict[str, Any]:
    return {
        'schema_version': _POLICY_TRACE_SCHEMA_VERSION,
        'generated_at_utc': utc_now(),
        'policy_config': {
            'reviewWindowHours': int(policy.get('review_window_hours', 24)),
            'laborDaysPerAgentPerDay': float(policy.get('labor_days_per_agent_per_day', 1.0)),
            'maxAutoapprovedChildAdditionsPerParentPerDay': int(
                policy.get('max_autoapproved_child_additions_per_parent_per_day', 1)
            ),
            'maxAutoapprovedAdditionsPerAgentPerDay': int(
                policy.get('max_autoapproved_additions_per_agent_per_day', 1)
            ),
            'effortCosts': policy.get('effort_costs', {}),
        },
        'ticks': [],
    }


def load_policy_trace_root(path: Path, policy: dict[str, Any]) -> dict[str, Any]:
    if not path.exists():
        return init_policy_trace_root(policy)
    try:
        parsed = json.loads(path.read_text(encoding='utf-8'))
        if isinstance(parsed, dict) and isinstance(parsed.get('ticks'), list):
            parsed.setdefault('schema_version', _POLICY_TRACE_SCHEMA_VERSION)
            parsed.setdefault('generated_at_utc', utc_now())
            parsed.setdefault('policy_config', init_policy_trace_root(policy)['policy_config'])
            return parsed
    except Exception:
        pass
    return init_policy_trace_root(policy)


def save_policy_trace_root(path: Path, payload: dict[str, Any]) -> None:
    payload['generated_at_utc'] = utc_now()
    payload['ticks'] = sorted(payload.get('ticks', []), key=lambda item: item.get('tick_index', 0))
    write_json_sync(path, payload)


def get_current_day_budget_state(
    trace_root: dict[str, Any],
    simulated_day_index: int,
) -> tuple[dict[str, float], dict[str, int], dict[str, int]]:
    labor_used: dict[str, float] = defaultdict(float)
    additions_by_agent: dict[str, int] = defaultdict(int)
    additions_by_parent: dict[str, int] = defaultdict(int)

    for tick_trace in trace_root.get('ticks', []):
        for decision in tick_trace.get('proposal_decisions', []):
            if not decision.get('executed'):
                continue
            if decision.get('action') != 'approve':
                continue
            if int(decision.get('approval_day_index', -1)) != simulated_day_index:
                continue

            agent = str(decision.get('agent') or '')
            parent_location = str(decision.get('parent_location') or '')
            labor_used[agent] += float(decision.get('effort_labor_days', 0.0))
            if is_addition_type(normalize_lower(decision.get('type'))):
                additions_by_agent[agent] += 1
                if parent_location:
                    additions_by_parent[parent_location] += 1

    return dict(labor_used), dict(additions_by_agent), dict(additions_by_parent)


def build_agent_tick_outcomes(snapshot: dict[str, Any], tick_index: int) -> list[dict[str, Any]]:
    proposals_this_tick = [
        proposal
        for proposal in snapshot.get('proposalHistory', [])
        if int(proposal.get('createdAtTick') or -1) == tick_index
    ]
    proposals_by_agent: dict[str, list[dict[str, Any]]] = defaultdict(list)
    proposals_by_target: dict[str, list[dict[str, Any]]] = defaultdict(list)
    for proposal in proposals_this_tick:
        agent_name = str(proposal.get('agent') or '')
        proposals_by_agent[agent_name].append(proposal)
        proposals_by_target[str(proposal.get('targetLocation') or '')].append(proposal)

    outcomes: list[dict[str, Any]] = []
    for agent in sorted(
        (entry for entry in snapshot.get('agents', []) if entry.get('canProposeWorldChanges')),
        key=lambda item: str(item.get('name') or ''),
    ):
        name = str(agent.get('name') or '')
        submitted = proposals_by_agent.get(name, [])
        if submitted:
            rejected_duplicate = any(
                normalize_lower(proposal.get('status')) == 'rejected'
                and len(proposals_by_target.get(str(proposal.get('targetLocation') or ''), [])) > 1
                for proposal in submitted
            )
            outcome = 'rejected_duplicate' if rejected_duplicate else 'submitted'
            rationale = (
                'The host rejected a same-target duplicate proposal created during this tick.'
                if rejected_duplicate
                else 'The host recorded a world proposal from this agent during this tick.'
            )
        else:
            outcome = 'no_proposal'
            rationale = (
                'No world proposal was emitted by the host for this agent during this tick; '
                'the current activity stayed within the existing environment.'
            )

        outcomes.append({
            'agent': name,
            'outcome': outcome,
            'rationale': rationale,
            'current_action': str(agent.get('action') or ''),
            'location': agent.get('location'),
            'next_short_plan': (agent.get('shortPlans') or [None])[0],
            'working_memory': (agent.get('workingMemories') or [None])[0],
            'proposal_ids': [proposal.get('id') for proposal in submitted],
            'proposal_targets': [proposal.get('targetLocation') for proposal in submitted],
        })

    return outcomes


def plan_policy_decisions(
    proposals: list[dict[str, Any]],
    snapshot: dict[str, Any],
    policy: dict[str, Any],
    trace_root: dict[str, Any],
    tick_index: int,
    timestep_minutes: int,
    auto_govern_enabled: bool,
) -> dict[str, Any]:
    simulated_day_index = get_simulated_day_index(tick_index, timestep_minutes)
    review_window_hours = int(
        (snapshot.get('governance') or {}).get('reviewWindowHours')
        or policy.get('review_window_hours', 24)
    )
    labor_budget = float(policy.get('labor_days_per_agent_per_day', 1.0))
    max_parent_additions = int(policy.get('max_autoapproved_child_additions_per_parent_per_day', 1))
    max_agent_additions = int(policy.get('max_autoapproved_additions_per_agent_per_day', 1))
    forbidden_names = [normalize_lower(item) for item in policy.get('forbidden_names', [])]
    auto_reject = [normalize_lower(item) for item in policy.get('auto_reject', [])]
    auto_approve = [normalize_lower(item) for item in policy.get('auto_approve', [])]

    labor_used, additions_by_agent, additions_by_parent = get_current_day_budget_state(
        trace_root, simulated_day_index
    )
    decisions: list[dict[str, Any]] = []

    for proposal in sorted(
        proposals,
        key=lambda item: (
            int(item.get('createdAtTick') or 0),
            str(item.get('createdAtUtc') or ''),
            str(item.get('id') or ''),
        ),
    ):
        proposal_id = str(proposal.get('id') or '')
        proposal_type = normalize_lower(proposal.get('type'))
        proposal_name = normalize_lower(proposal.get('name'))
        parent_location = str(proposal.get('parentLocation') or '')
        agent = str(proposal.get('agent') or '')
        effort_labor_days, effort_class = estimate_proposal_effort(proposal, policy)
        age_hours = max(
            0.0,
            (tick_index - int(proposal.get('createdAtTick') or tick_index)) * timestep_minutes / 60.0,
        )
        action = 'escalate'
        rationale = 'Auto-govern disabled; proposal remains in manual review.'
        suggestion = None

        if not auto_govern_enabled:
            action = 'escalate'
            rationale = 'Auto-govern is disabled for this run, so the proposal remains in manual review.'
        elif age_hours < review_window_hours:
            action = 'deferred_time_window'
            rationale = (
                f'Proposal age is {age_hours:.1f}h; it must reach the {review_window_hours}h review window first.'
            )
        elif any(fragment and fragment in proposal_name for fragment in forbidden_names):
            action = 'reject_scope'
            rationale = 'Proposal name matched a forbidden substring in policy.'
        elif proposal_type in auto_reject:
            action = 'reject_scope'
            rationale = 'Proposal type matched the policy auto-reject set.'
        elif proposal_type not in auto_approve:
            action = 'escalate'
            rationale = 'Proposal type is not in the policy auto-govern allowlist.'
        elif effort_labor_days > labor_budget:
            action = 'reject_scope'
            suggestion = suggest_alternative(proposal, policy)
            rationale = (
                f'Estimated effort {effort_labor_days:.2f} labor-days exceeds the per-agent daily budget '
                f'of {labor_budget:.2f}.'
            )
        else:
            agent_labor_used = labor_used.get(agent, 0.0)
            agent_additions = additions_by_agent.get(agent, 0)
            parent_additions = additions_by_parent.get(parent_location, 0)
            if agent_labor_used + effort_labor_days > labor_budget:
                action = 'deferred_budget'
                rationale = (
                    f'Approving now would raise {agent} to {agent_labor_used + effort_labor_days:.2f} labor-days '
                    f'on simulated day {simulated_day_index}, above the {labor_budget:.2f} limit.'
                )
            elif is_addition_type(proposal_type) and agent_additions >= max_agent_additions:
                action = 'deferred_budget'
                rationale = (
                    f'{agent} already has {agent_additions} auto-approved addition(s) on simulated day '
                    f'{simulated_day_index}; the per-agent addition cap is {max_agent_additions}.'
                )
            elif is_addition_type(proposal_type) and parent_location and parent_additions >= max_parent_additions:
                action = 'deferred_budget'
                rationale = (
                    f'{parent_location} already has {parent_additions} auto-approved child addition(s) on '
                    f'simulated day {simulated_day_index}; the per-parent cap is {max_parent_additions}.'
                )
            else:
                action = 'approve'
                rationale = (
                    'Proposal is within the review window, effort budget, and daily addition limits '
                    f'for simulated day {simulated_day_index}.'
                )
                labor_used[agent] = agent_labor_used + effort_labor_days
                if is_addition_type(proposal_type):
                    additions_by_agent[agent] = agent_additions + 1
                    if parent_location:
                        additions_by_parent[parent_location] = parent_additions + 1

        decisions.append({
            'proposal_id': proposal_id,
            'agent': agent,
            'type': proposal.get('type'),
            'parent_location': parent_location,
            'target_location': proposal.get('targetLocation'),
            'name': proposal.get('name'),
            'status_before': proposal.get('status'),
            'created_at_tick': int(proposal.get('createdAtTick') or 0),
            'created_at_simulation_time': proposal.get('createdAtSimulationTime'),
            'action': action,
            'rationale': rationale,
            'suggestion': suggestion,
            'age_hours': round(age_hours, 2),
            'effort_labor_days': effort_labor_days,
            'effort_class': effort_class,
            'simulated_day_index': simulated_day_index,
            'approval_day_index': simulated_day_index if action == 'approve' else None,
            'requires_human_review': action == 'escalate',
            'executed': False,
        })

    return {
        'decisions': decisions,
        'review_window_hours': review_window_hours,
        'simulated_day_index': simulated_day_index,
        'blocking_pending_ids': [
            decision['proposal_id']
            for decision in decisions
            if decision.get('requires_human_review')
        ],
        'budget_snapshot': {
            'agent_labor_days_used': labor_used,
            'agent_autoapproved_additions': additions_by_agent,
            'parent_autoapproved_additions': additions_by_parent,
        },
    }


async def apply_policy_decisions(client: SmallvilleClient, planned: dict[str, Any]) -> dict[str, Any]:
    approved = 0
    rejected = 0
    mutated = False
    blocking_pending_ids = list(planned.get('blocking_pending_ids', []))

    for decision in planned.get('decisions', []):
        proposal_id = str(decision.get('proposal_id') or '')
        try:
            if decision.get('action') == 'approve':
                await client.approve_proposal(proposal_id)
                decision['executed'] = True
                approved += 1
                mutated = True
                print(f'  [policy] AUTO-APPROVE: {proposal_id} ({decision.get("type")}: {decision.get("name")})')
            elif decision.get('action') == 'reject_scope':
                await client.reject_proposal(proposal_id)
                decision['executed'] = True
                rejected += 1
                mutated = True
                print(f'  [policy] AUTO-REJECT:  {proposal_id} ({decision.get("type")}: {decision.get("name")})')
            elif decision.get('action') == 'deferred_time_window':
                print(f'  [policy] DEFER-TIME:   {proposal_id} ({decision.get("type")}: {decision.get("name")})')
            elif decision.get('action') == 'deferred_budget':
                print(f'  [policy] DEFER-BUDGET: {proposal_id} ({decision.get("type")}: {decision.get("name")})')
            else:
                print(f'  [policy] ESCALATE:     {proposal_id} ({decision.get("type")}: {decision.get("name")})')
        except Exception as exc:
            decision['action'] = 'escalate'
            decision['executed'] = False
            decision['requires_human_review'] = True
            decision['rationale'] = f'Policy action failed with {exc!r}; proposal now requires manual review.'
            if proposal_id not in blocking_pending_ids:
                blocking_pending_ids.append(proposal_id)
            print(f'  [policy] ERROR on {proposal_id}: {exc}', file=sys.stderr)

    planned['approved_count'] = approved
    planned['rejected_count'] = rejected
    planned['mutated'] = mutated
    planned['blocking_pending_ids'] = sorted(set(blocking_pending_ids))
    return planned


def build_policy_tick_trace(
    tick_index: int,
    simulated_time: str,
    snapshot: dict[str, Any],
    pending_before_count: int,
    pending_after_count: int,
    planned: dict[str, Any],
) -> dict[str, Any]:
    return {
        'tick_index': tick_index,
        'simulated_time': simulated_time,
        'simulated_day_index': planned.get('simulated_day_index', 0),
        'review_window_hours': planned.get('review_window_hours', 24),
        'pending_before_count': pending_before_count,
        'pending_after_count': pending_after_count,
        'blocking_pending_count': len(planned.get('blocking_pending_ids', [])),
        'blocking_pending_ids': planned.get('blocking_pending_ids', []),
        'agent_outcomes': build_agent_tick_outcomes(snapshot, tick_index),
        'proposal_decisions': planned.get('decisions', []),
        'budget_snapshot': planned.get('budget_snapshot', {}),
    }


def write_policy_trace_files(
    output_dir: Path,
    trace_root: dict[str, Any],
    tick_trace: dict[str, Any],
) -> dict[str, Any]:
    trace_root['ticks'] = [
        entry for entry in trace_root.get('ticks', [])
        if int(entry.get('tick_index', -1)) != int(tick_trace.get('tick_index', -1))
    ]
    trace_root['ticks'].append(tick_trace)
    save_policy_trace_root(output_dir / _POLICY_TRACE_FILENAME, trace_root)
    write_json_sync(output_dir / f'tick_{int(tick_trace["tick_index"]):03d}_policy_trace.json', tick_trace)

    flat_log: list[dict[str, Any]] = []
    for entry in sorted(trace_root.get('ticks', []), key=lambda item: item.get('tick_index', 0)):
        for decision in entry.get('proposal_decisions', []):
            flat_log.append({
                'tick_index': entry.get('tick_index'),
                'simulated_day_index': entry.get('simulated_day_index'),
                **decision,
            })
    write_json_sync(output_dir / _POLICY_LOG_FILENAME, flat_log)
    return trace_root


# ---------------------------------------------------------------------------
# Data structures
# ---------------------------------------------------------------------------


@dataclass
class TickRecord:
    tick_index: int
    simulated_time: str
    step: int
    proposal_count: int
    agent_count: int
    location_count: int
    blocking_proposal_count: int = 0


# ---------------------------------------------------------------------------
# CLI argument parser
# ---------------------------------------------------------------------------


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description='Async multi-day Smallville simulation orchestrator with parallel artifacts, '
                    'retry/backoff, checkpoint/resume, and inline proposal governance.',
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument('--base-url', default='http://127.0.0.1:8090', help='Backend base URL.')
    parser.add_argument('--days', type=float, default=None, help='Simulated days to run.')
    parser.add_argument('--timestep-minutes', type=int, default=None, help='Simulated minutes per tick.')
    parser.add_argument('--pause-seconds', type=float, default=None, help='Real-time pause between ticks.')
    parser.add_argument('--output-dir', default='', help='Artifact output directory.')
    parser.add_argument(
        '--mode',
        choices=['interactive', 'overnight', 'custom'],
        default='custom',
        help='Preset: interactive=(1d/60m/0s), overnight=(3d/120m/0.5s), custom=use explicit flags.',
    )
    parser.add_argument(
        '--allow-pending-proposals',
        action='store_true',
        help='Continue when pending proposals remain after policy pass.',
    )
    parser.add_argument(
        '--allow-auto-govern',
        action='store_true',
        help='Apply inline policy pass on proposals before pausing.',
    )
    parser.add_argument(
        '--policy',
        default='',
        help='Path to proposal_policy.yaml (or .json). Enables auto-approve/reject/escalate.',
    )
    parser.add_argument(
        '--resume',
        action='store_true',
        help='Resume from last checkpoint in --output-dir.',
    )
    parser.add_argument('--timeout-seconds', type=float, default=1800.0, help='HTTP timeout per request.')
    return parser.parse_args()


# ---------------------------------------------------------------------------
# Orchestrator
# ---------------------------------------------------------------------------


async def run(args: argparse.Namespace) -> int:
    # Apply mode preset for any unset values
    if args.mode != 'custom':
        preset = _MODE_PRESETS[args.mode]
        if args.days is None:
            args.days = preset['days']
        if args.timestep_minutes is None:
            args.timestep_minutes = preset['timestep_minutes']
        if args.pause_seconds is None:
            args.pause_seconds = preset['pause_seconds']

    # Custom defaults
    if args.days is None:
        args.days = 3.0
    if args.timestep_minutes is None:
        args.timestep_minutes = 60
    if args.pause_seconds is None:
        args.pause_seconds = 0.0

    if args.timestep_minutes <= 0:
        print('timestep-minutes must be positive', file=sys.stderr)
        return 2

    run_id = datetime.now().strftime('%Y%m%d-%H%M%S')
    output_dir = Path(args.output_dir) if args.output_dir else Path('runs') / f'{run_id}-three-day-python'
    output_dir.mkdir(parents=True, exist_ok=True)

    tick_count = get_tick_count(args.days, args.timestep_minutes)
    policy = load_policy(args.policy)
    manifest_path = output_dir / 'manifest.json'
    policy_trace_path = output_dir / _POLICY_TRACE_FILENAME
    policy_trace_root = load_policy_trace_root(policy_trace_path, policy)

    # --- Resume checkpoint ---
    start_tick = 1
    old_records: list[dict[str, Any]] = []
    old_metrics: list[dict[str, Any]] = []
    frame_index: list[dict[str, Any]] = []
    manifest: dict[str, Any] = {}

    if args.resume and manifest_path.exists():
        try:
            manifest = json.loads(manifest_path.read_text(encoding='utf-8'))
            old_records = manifest.get('records', [])
            old_metrics = manifest.get('metrics', [])
            frame_index = manifest.get('frame_index', [])
            if old_records:
                start_tick = old_records[-1]['tick_index'] + 1
                print(f'[resume] Continuing from tick {start_tick} (last completed: {start_tick - 1})')
        except Exception as exc:
            print(f'[resume] Could not read checkpoint, starting fresh: {exc}', file=sys.stderr)

    if not manifest or not args.resume:
        manifest = {
            'run_started_at_utc': utc_now(),
            'base_url': args.base_url,
            'days': args.days,
            'timestep_minutes': args.timestep_minutes,
            'tick_count': tick_count,
            'pause_seconds': args.pause_seconds,
            'mode': args.mode,
            'allow_pending_proposals': args.allow_pending_proposals,
            'allow_auto_govern': args.allow_auto_govern,
            'status': 'starting',
            'stopped_reason': '',
            'policy_trace_file': _POLICY_TRACE_FILENAME,
            'policy_trace_schema_version': _POLICY_TRACE_SCHEMA_VERSION,
            'records': [],
            'metrics': [],
            'frame_index': [],
        }
        write_json_sync(manifest_path, manifest)

    connector = aiohttp.TCPConnector(limit=10, ttl_dns_cache=300)
    timeout = aiohttp.ClientTimeout(total=args.timeout_seconds, connect=10, sock_read=args.timeout_seconds)

    async with aiohttp.ClientSession(connector=connector, timeout=timeout) as session:
        client = SmallvilleClient(session, args.base_url)
        try:
            ping = await client.get_json('/ping')
            await write_json(output_dir / 'ping.json', ping)
            print(f'[init] Backend alive: {ping}')

            timestep_response = await client.post_json('/timestep', {'numOfMinutes': str(args.timestep_minutes)})
            await write_json(output_dir / 'timestep.json', timestep_response)

            # Cold snapshot (tick_000) — only on fresh start
            if start_tick == 1:
                cold_metrics: dict[str, Any] = {}
                cold_snapshot = await save_world_artifacts(
                    client, output_dir, 'tick_000', 'Smallville Tick 000', cold_metrics
                )
                await save_ledger_export_artifacts(client, output_dir, snapshot_label='cold_start')
                pending = cold_snapshot.get('pendingProposals', [])
                pending_before_count = len(pending)
                cold_plan = plan_policy_decisions(
                    pending,
                    cold_snapshot,
                    policy,
                    policy_trace_root,
                    0,
                    args.timestep_minutes,
                    args.allow_auto_govern,
                )
                if pending:
                    cold_plan = await apply_policy_decisions(client, cold_plan)
                    if cold_plan.get('mutated'):
                        refresh_metrics = {'artifact_fetch_ms': 0, 'artifact_write_ms': 0}
                        cold_snapshot = await save_world_artifacts(
                            client, output_dir, 'tick_000', 'Smallville Tick 000', refresh_metrics
                        )
                final_cold_pending = cold_snapshot.get('pendingProposals', [])
                final_cold_pending_ids = {
                    str(proposal.get('id') or '')
                    for proposal in final_cold_pending
                }
                cold_plan['blocking_pending_ids'] = [
                    proposal_id
                    for proposal_id in cold_plan.get('blocking_pending_ids', [])
                    if proposal_id in final_cold_pending_ids
                ]
                cold_trace = {
                    'tick_index': 0,
                    'simulated_time': str(cold_snapshot.get('time', 'Unavailable')),
                    'simulated_day_index': 0,
                    'review_window_hours': cold_plan.get('review_window_hours', policy.get('review_window_hours', 24)),
                    'pending_before_count': pending_before_count,
                    'pending_after_count': len(final_cold_pending),
                    'blocking_pending_count': len(cold_plan.get('blocking_pending_ids', [])),
                    'blocking_pending_ids': cold_plan.get('blocking_pending_ids', []),
                    'agent_outcomes': [],
                    'proposal_decisions': cold_plan.get('decisions', []),
                    'budget_snapshot': cold_plan.get('budget_snapshot', {}),
                }
                policy_trace_root = write_policy_trace_files(output_dir, policy_trace_root, cold_trace)

                if cold_trace['blocking_pending_count'] and not args.allow_pending_proposals:
                    manifest['status'] = 'paused'
                    manifest['stopped_reason'] = 'pending proposals detected before the first tick'
                    manifest['policy_trace_file'] = _POLICY_TRACE_FILENAME
                    write_json_sync(manifest_path, manifest)
                    print('Paused before tick 1 — pending proposals remain after policy pass.')
                    return 1

            records: list[TickRecord] = [TickRecord(**r) for r in old_records]
            all_metrics: list[dict[str, Any]] = list(old_metrics)

            for tick_index in range(start_tick, tick_count + 1):
                tick_start = time.perf_counter()
                tick_metrics: dict[str, Any] = {
                    'tick_index': tick_index,
                    'wall_clock_ms': 0,
                    'advance_ms': 0,
                    'artifact_fetch_ms': 0,
                    'artifact_write_ms': 0,
                    'policy_ms': 0,
                }

                # Advance the simulation
                t_adv = time.perf_counter()
                await client.post_json('/state', {})
                tick_metrics['advance_ms'] = int((time.perf_counter() - t_adv) * 1000)

                # Capture all artifacts in parallel
                snapshot = await save_world_artifacts(
                    client,
                    output_dir,
                    f'tick_{tick_index:03d}',
                    f'Smallville Tick {tick_index:03d}',
                    tick_metrics,
                )

                # Inline policy pass
                t_policy = time.perf_counter()
                pending_proposals = snapshot.get('pendingProposals', [])
                pending_before_count = len(pending_proposals)
                planned = plan_policy_decisions(
                    pending_proposals,
                    snapshot,
                    policy,
                    policy_trace_root,
                    tick_index,
                    args.timestep_minutes,
                    args.allow_auto_govern,
                )
                if pending_proposals:
                    planned = await apply_policy_decisions(client, planned)
                    if planned.get('mutated'):
                        refresh_metrics = {'artifact_fetch_ms': 0, 'artifact_write_ms': 0}
                        snapshot = await save_world_artifacts(
                            client,
                            output_dir,
                            f'tick_{tick_index:03d}',
                            f'Smallville Tick {tick_index:03d}',
                            refresh_metrics,
                        )
                        tick_metrics['artifact_fetch_ms'] += refresh_metrics['artifact_fetch_ms']
                        tick_metrics['artifact_write_ms'] += refresh_metrics['artifact_write_ms']

                final_pending = snapshot.get('pendingProposals', [])
                final_pending_ids = {
                    str(proposal.get('id') or '')
                    for proposal in final_pending
                }
                planned['blocking_pending_ids'] = [
                    proposal_id
                    for proposal_id in planned.get('blocking_pending_ids', [])
                    if proposal_id in final_pending_ids
                ]
                remaining_count = len(final_pending)
                tick_trace = build_policy_tick_trace(
                    tick_index,
                    str(snapshot.get('time', 'Unavailable')),
                    snapshot,
                    pending_before_count,
                    remaining_count,
                    planned,
                )
                policy_trace_root = write_policy_trace_files(output_dir, policy_trace_root, tick_trace)
                tick_metrics['policy_ms'] = int((time.perf_counter() - t_policy) * 1000)
                tick_metrics['wall_clock_ms'] = int((time.perf_counter() - tick_start) * 1000)

                record = TickRecord(
                    tick_index=tick_index,
                    simulated_time=str(snapshot.get('time', 'Unavailable')),
                    step=int(snapshot.get('step', 0)),
                    proposal_count=remaining_count,
                    agent_count=len(snapshot.get('agents', [])),
                    location_count=len(snapshot.get('locations', [])),
                    blocking_proposal_count=len(planned.get('blocking_pending_ids', [])),
                )
                records.append(record)
                all_metrics.append(tick_metrics)

                # Compute SVG hash for frame_index
                svg_file = f'tick_{tick_index:03d}_world_map.svg'
                svg_path = output_dir / svg_file
                svg_hash = ''
                if svg_path.exists():
                    svg_hash = hashlib.sha256(svg_path.read_bytes()).hexdigest()[:16]

                frame_index.append({
                    'tick': tick_index,
                    'time': str(snapshot.get('time', 'Unavailable')),
                    'step': int(snapshot.get('step', 0)),
                    'agentCount': len(snapshot.get('agents', [])),
                    'locationCount': len(snapshot.get('locations', [])),
                    'proposalCount': remaining_count,
                    'blockingProposalCount': record.blocking_proposal_count,
                    'worldFile': f'tick_{tick_index:03d}_world.json',
                    'svgFile': svg_file,
                    'svgHash': svg_hash,
                    'policyTraceFile': f'tick_{tick_index:03d}_policy_trace.json',
                })

                # Checkpoint manifest after every tick
                manifest['records'] = [asdict(r) for r in records]
                manifest['metrics'] = all_metrics
                manifest['frame_index'] = frame_index
                manifest['policy_trace_file'] = _POLICY_TRACE_FILENAME
                manifest['status'] = 'running'
                manifest['stopped_reason'] = ''
                write_json_sync(manifest_path, manifest)

                print(
                    f'tick={tick_index:03d}  time="{record.simulated_time}"  '
                    f'agents={record.agent_count}  locs={record.location_count}  '
                    f'proposals={record.proposal_count}  '
                    f'blocking={record.blocking_proposal_count}  '
                    f'wall={tick_metrics["wall_clock_ms"]}ms  '
                    f'advance={tick_metrics["advance_ms"]}ms  '
                    f'artifacts={tick_metrics["artifact_fetch_ms"]}ms'
                )

                if record.blocking_proposal_count and not args.allow_pending_proposals:
                    manifest['status'] = 'paused'
                    manifest['stopped_reason'] = (
                        f'{record.blocking_proposal_count} proposals require review after tick {tick_index}'
                    )
                    write_json_sync(manifest_path, manifest)
                    print(
                        f'Paused after tick {tick_index}. '
                        f'{record.blocking_proposal_count} proposal(s) need human review.'
                    )
                    print(f'Resume:  python run_three_day_sim.py --resume --output-dir {output_dir}')
                    return 1

                if args.pause_seconds > 0:
                    await asyncio.sleep(args.pause_seconds)

            manifest['status'] = 'completed'
            manifest['stopped_reason'] = ''
            manifest['run_finished_at_utc'] = utc_now()
            write_json_sync(manifest_path, manifest)
            await save_ledger_export_artifacts(client, output_dir, snapshot_label='final')
            print(f'\nCompleted {tick_count} ticks. Artifacts in {output_dir}')
            return 0

        except RuntimeError as exc:
            # Circuit breaker open
            manifest.update({
                'status': 'failed',
                'stopped_reason': str(exc),
                'run_finished_at_utc': utc_now(),
            })
            write_json_sync(manifest_path, manifest)
            print(f'[circuit-breaker] {exc}', file=sys.stderr)
            return 1
        except Exception as exc:
            manifest.update({
                'status': 'failed',
                'stopped_reason': str(exc),
                'run_finished_at_utc': utc_now(),
            })
            write_json_sync(manifest_path, manifest)
            print(f'Fatal error: {exc}', file=sys.stderr)
            return 1


def main() -> int:
    args = parse_args()
    return asyncio.run(run(args))


if __name__ == '__main__':
    raise SystemExit(main())
