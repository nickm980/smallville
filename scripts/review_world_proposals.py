"""Smallville governance review CLI with optional policy-driven pending decisions.

Usage (inspect only):
    python review_world_proposals.py

Usage (manual pending-queue decisions):
    python review_world_proposals.py --approve-id <id> --reject-id <id>

Usage (governance review actions):
    python review_world_proposals.py --window-hours 24 --remove-id <id> --block-id <id> --guidance-rule "Prefer sibling beds over nested planters."

Usage (policy auto-decide all pending proposals):
    python review_world_proposals.py --auto-decide --policy proposal_policy.yaml

Policy tiers still apply only to the pending queue:
    auto_approve / auto_reject / forbidden_names / escalate (default)
"""
from __future__ import annotations

import argparse
import json
import sys
from datetime import datetime, timezone
from pathlib import Path
from typing import Any
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen


def utc_now() -> str:
    return datetime.now(timezone.utc).isoformat()


def request_json(
    base_url: str,
    path: str,
    method: str = 'GET',
    payload: dict[str, Any] | None = None,
    timeout: float = 60.0,
) -> Any:
    body = None
    headers: dict[str, str] = {'Accept': 'application/json'}
    if payload is not None:
        body = json.dumps(payload).encode('utf-8')
        headers['Content-Type'] = 'application/json'

    request = Request(f"{base_url.rstrip('/')}{path}", data=body, headers=headers, method=method)
    with urlopen(request, timeout=timeout) as response:
        return json.loads(response.read().decode('utf-8'))


def write_json(path: Path, payload: Any) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(payload, indent=2), encoding='utf-8')


# ---------------------------------------------------------------------------
# Policy engine
# ---------------------------------------------------------------------------


def load_policy(policy_path: str) -> dict[str, Any]:
    p = Path(policy_path)
    if not p.exists():
        print(f'[policy] Policy file not found: {policy_path!r}', file=sys.stderr)
        return {}
    raw = p.read_text(encoding='utf-8')
    try:
        import yaml  # type: ignore[import-untyped]
        return yaml.safe_load(raw) or {}
    except ImportError:
        pass
    try:
        return json.loads(raw)
    except Exception as exc:
        print(f'[policy] Failed to parse policy file: {exc}', file=sys.stderr)
        return {}


def classify_proposal(proposal: dict[str, Any], policy: dict[str, Any]) -> str:
    """Return 'approve', 'reject', or 'escalate'."""
    if not policy:
        return 'escalate'

    ptype = (proposal.get('type') or '').lower()
    pname = (proposal.get('name') or '').lower()

    forbidden = [n.lower() for n in policy.get('forbidden_names', [])]
    if any(fn in pname for fn in forbidden):
        return 'reject'

    if ptype in [t.lower() for t in policy.get('auto_reject', [])]:
        return 'reject'

    if ptype in [t.lower() for t in policy.get('auto_approve', [])]:
        return 'approve'

    return 'escalate'


def apply_auto_decisions(
    base_url: str,
    proposals: list[dict[str, Any]],
    policy: dict[str, Any],
    timeout: float,
) -> tuple[list[dict[str, str]], list[dict[str, Any]]]:
    """
    Classify and execute policy decisions for all proposals.
    Returns (decisions_log, escalated_proposals).
    """
    decisions: list[dict[str, str]] = []
    escalated: list[dict[str, Any]] = []

    for proposal in proposals:
        decision = classify_proposal(proposal, policy)
        pid = proposal.get('id', '')
        ptype = proposal.get('type', 'unknown')
        pname = proposal.get('name', 'n/a')

        try:
            if decision == 'approve':
                request_json(base_url, f'/world/proposals/{pid}/approve', method='POST', payload={}, timeout=timeout)
                decisions.append({'action': 'approve', 'id': pid})
                print(f'  [policy] AUTO-APPROVE: {pid} ({ptype}: {pname})')
            elif decision == 'reject':
                request_json(base_url, f'/world/proposals/{pid}/reject', method='POST', payload={}, timeout=timeout)
                decisions.append({'action': 'reject', 'id': pid})
                print(f'  [policy] AUTO-REJECT:  {pid} ({ptype}: {pname})')
            else:
                escalated.append(proposal)
                decisions.append({'action': 'escalate', 'id': pid})
                print(f'  [policy] ESCALATE:     {pid} ({ptype}: {pname})')
        except (HTTPError, URLError) as exc:
            print(f'  [policy] ERROR on {pid}: {exc}', file=sys.stderr)
            escalated.append(proposal)

    return decisions, escalated


# ---------------------------------------------------------------------------
# Report rendering
# ---------------------------------------------------------------------------


def render_markdown_report(
    world: dict[str, Any],
    proposals: list[dict[str, Any]],
    decisions: list[dict[str, str]],
    governance: dict[str, Any],
    review: dict[str, Any],
) -> str:
    review_entries = review.get('history', [])
    guidance_rules = governance.get('guidanceRules', [])
    block_rules = governance.get('blockRules', [])
    lines: list[str] = [
        '# Smallville Governance Review Packet',
        '',
        f'Generated at: {utc_now()}',
        f"World tick: {world.get('tick', 0)}",
        f"World time: {world.get('time', 'Unavailable')}",
        f"Governance proposal mode: {governance.get('proposalMode', 'unknown')}",
        f"Governance review window (hours): {review.get('windowHours', governance.get('reviewWindowHours', 24))}",
        f'Pending proposals (after decisions): {len(proposals)}',
        f'Governance history entries in window: {len(review_entries)}',
        '',
    ]

    lines.append('## Governance State')
    if guidance_rules:
        lines.append(f'- Guidance rules: {len(guidance_rules)}')
        for rule in guidance_rules:
            lines.append(f'  - {rule}')
    else:
        lines.append('- Guidance rules: none')

    if block_rules:
        lines.append(f'- Block rules: {len(block_rules)}')
        for rule in block_rules:
            target = rule.get('targetLocation') or rule.get('name') or 'unknown'
            lines.append(f"  - {rule.get('id', 'unknown')}: {target}")
    else:
        lines.append('- Block rules: none')
    lines.append('')

    if decisions:
        lines.append('## Applied Decisions')
        approve_count = sum(1 for d in decisions if d['action'] == 'approve')
        reject_count = sum(1 for d in decisions if d['action'] == 'reject')
        remove_count = sum(1 for d in decisions if d['action'] == 'remove')
        block_count = sum(1 for d in decisions if d['action'] == 'block')
        guidance_count = sum(1 for d in decisions if d['action'] == 'guidance')
        escalate_count = sum(1 for d in decisions if d['action'] == 'escalate')
        lines.append(f'- Approved: {approve_count}')
        lines.append(f'- Rejected: {reject_count}')
        lines.append(f'- Removed: {remove_count}')
        lines.append(f'- Blocked: {block_count}')
        lines.append(f'- Guidance rules added: {guidance_count}')
        lines.append(f'- Escalated: {escalate_count}')
        lines.append('')
        for item in decisions:
            value = item.get('id') or item.get('value', '')
            lines.append(f"  - {item['action'].upper()}: `{value}`")
        lines.append('')

    lines.append('## 24-Hour Review')
    if not review_entries:
        lines.append('- No governance history entries.')
    else:
        for entry in review_entries:
            lines.append(f"### {entry.get('id', 'unknown-id')}")
            lines.append(f"- Status: {entry.get('status', 'unknown')}")
            lines.append(f"- Agent: {entry.get('agent', 'unknown')}")
            lines.append(f"- Type: {entry.get('type', 'unknown')}")
            lines.append(f"- Parent: {entry.get('parentLocation') or 'n/a'}")
            lines.append(f"- Name: {entry.get('name') or 'n/a'}")
            lines.append(f"- State: {entry.get('state') or 'n/a'}")
            lines.append(f"- Tick: {entry.get('tick', 'n/a')}")
            lines.append(f"- Time: {entry.get('time') or 'n/a'}")
            lines.append(f"- Reason: {entry.get('reason', '').strip() or 'n/a'}")
            lines.append('')

    lines.append('## Pending Queue')
    if not proposals:
        lines.append('- No pending proposals.')
        return '\n'.join(lines)

    for proposal in proposals:
        lines.append(f"### {proposal.get('id', 'unknown-id')}")
        lines.append(f"- Agent: {proposal.get('agent', 'unknown')}")
        lines.append(f"- Type: {proposal.get('type', 'unknown')}")
        lines.append(f"- Parent: {proposal.get('parentLocation') or 'n/a'}")
        lines.append(f"- Name: {proposal.get('name', 'n/a')}")
        lines.append(f"- Proposed state: {proposal.get('proposedState') or 'n/a'}")
        lines.append(f"- Created at tick: {proposal.get('createdAtTick', 0)}")
        lines.append(f"- Reason: {proposal.get('reason', '').strip() or 'n/a'}")
        lines.append('')

    return '\n'.join(lines)


# ---------------------------------------------------------------------------
# CLI
# ---------------------------------------------------------------------------


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description='Review and optionally decide pending Smallville world proposals.',
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument('--base-url', default='http://127.0.0.1:8090', help='Smallville server base URL.')
    parser.add_argument('--output-dir', default='', help='Directory for review packet artifacts.')
    parser.add_argument('--timeout-seconds', type=float, default=60.0, help='HTTP timeout in seconds.')
    parser.add_argument('--window-hours', type=int, default=24, help='Governance review window in hours.')
    parser.add_argument('--approve-id', action='append', default=[], help='Proposal ID to approve. Repeatable.')
    parser.add_argument('--reject-id', action='append', default=[], help='Proposal ID to reject. Repeatable.')
    parser.add_argument('--remove-id', action='append', default=[], help='Governance proposal ID to remove/revert. Repeatable.')
    parser.add_argument('--block-id', action='append', default=[], help='Governance proposal ID to block for future builds. Repeatable.')
    parser.add_argument('--guidance-rule', action='append', default=[], help='Governance guidance rule to add. Repeatable.')
    parser.add_argument(
        '--auto-decide',
        action='store_true',
        help='Apply policy rules to all pending proposals automatically.',
    )
    parser.add_argument(
        '--policy',
        default='',
        help='Path to proposal_policy.yaml (or .json). Required for --auto-decide.',
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()

    if args.auto_decide and not args.policy:
        print('[error] --auto-decide requires --policy <path>', file=sys.stderr)
        return 2

    run_id = datetime.now().strftime('%Y%m%d-%H%M%S')
    output_dir = Path(args.output_dir) if args.output_dir else Path('runs') / f'{run_id}-proposal-review'
    output_dir.mkdir(parents=True, exist_ok=True)

    decisions: list[dict[str, str]] = []

    try:
        ping = request_json(args.base_url, '/ping', timeout=args.timeout_seconds)
        write_json(output_dir / 'ping.json', ping)

        # Manual explicit decisions
        for proposal_id in args.approve_id:
            request_json(
                args.base_url,
                f'/world/proposals/{proposal_id}/approve',
                method='POST',
                payload={},
                timeout=args.timeout_seconds,
            )
            decisions.append({'action': 'approve', 'id': proposal_id})
            print(f'APPROVED: {proposal_id}')

        for proposal_id in args.reject_id:
            request_json(
                args.base_url,
                f'/world/proposals/{proposal_id}/reject',
                method='POST',
                payload={},
                timeout=args.timeout_seconds,
            )
            decisions.append({'action': 'reject', 'id': proposal_id})
            print(f'REJECTED: {proposal_id}')

        for proposal_id in args.remove_id:
            request_json(
                args.base_url,
                f'/world/governance/proposals/{proposal_id}/remove',
                method='POST',
                payload={},
                timeout=args.timeout_seconds,
            )
            decisions.append({'action': 'remove', 'id': proposal_id})
            print(f'REMOVED: {proposal_id}')

        for proposal_id in args.block_id:
            request_json(
                args.base_url,
                f'/world/governance/proposals/{proposal_id}/block',
                method='POST',
                payload={},
                timeout=args.timeout_seconds,
            )
            decisions.append({'action': 'block', 'id': proposal_id})
            print(f'BLOCKED: {proposal_id}')

        for rule in args.guidance_rule:
            request_json(
                args.base_url,
                '/world/governance/guidance',
                method='POST',
                payload={'rule': rule},
                timeout=args.timeout_seconds,
            )
            decisions.append({'action': 'guidance', 'value': rule})
            print(f'GUIDANCE ADDED: {rule}')

        # Fetch current world state and proposals
        world = request_json(args.base_url, '/world', timeout=args.timeout_seconds)
        proposal_payload = request_json(args.base_url, '/world/proposals', timeout=args.timeout_seconds)
        governance_payload = request_json(args.base_url, '/world/governance', timeout=args.timeout_seconds)
        review_payload = request_json(
            args.base_url,
            f'/world/governance/review?windowHours={max(args.window_hours, 1)}',
            timeout=args.timeout_seconds,
        )
        proposals: list[dict[str, Any]] = proposal_payload.get('proposals', [])

        # Policy auto-decide pass
        if args.auto_decide and proposals:
            policy = load_policy(args.policy)
            if policy:
                auto_decisions, proposals = apply_auto_decisions(
                    args.base_url, proposals, policy, args.timeout_seconds
                )
                decisions.extend(auto_decisions)
                # Refresh proposal list after decisions
                proposal_payload = request_json(args.base_url, '/world/proposals', timeout=args.timeout_seconds)
                proposals = proposal_payload.get('proposals', [])
            else:
                print('[policy] No valid policy loaded; skipping auto-decide.', file=sys.stderr)

        write_json(output_dir / 'world.json', world)
        write_json(output_dir / 'proposals.json', proposal_payload)
        write_json(output_dir / 'governance.json', governance_payload)
        write_json(output_dir / 'governance_review.json', review_payload)
        write_json(output_dir / 'decisions.json', {'decisions': decisions})

        report = render_markdown_report(
            world,
            proposals,
            decisions,
            governance_payload.get('governance', {}),
            review_payload,
        )
        (output_dir / 'review_packet.md').write_text(report, encoding='utf-8')

        print(f'Pending proposals (remaining): {len(proposals)}')
        print(f"Governance history entries: {len(review_payload.get('history', []))}")
        if decisions:
            print(f'Total decisions applied: {len(decisions)}')
        print(f'Review packet written to {output_dir}')
        return 0

    except HTTPError as error:
        print(f'HTTP error {error.code}: {error.reason}', file=sys.stderr)
        return 1
    except URLError as error:
        print(f'Connection error: {error.reason}', file=sys.stderr)
        return 1


if __name__ == '__main__':
    raise SystemExit(main())
