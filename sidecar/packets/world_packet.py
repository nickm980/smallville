"""Build the deterministic world packet."""

from __future__ import annotations

from typing import Any


BOUNDARY = {
    "hostAuthority": "java",
    "sidecarRole": "deterministic-offline-packet-assembly",
    "mutationAuthority": "none",
}

DAILY_RHYTHM_FIELDS = (
    "breakfast",
    "lunch",
    "dinner",
    "morningTea",
    "afternoonTea",
    "eveningWind",
    "snack",
)


def _ordered_daily_rhythm(daily_rhythm: dict[str, Any]) -> dict[str, Any]:
    daily_rhythm = daily_rhythm or {}
    return {field: daily_rhythm.get(field) for field in DAILY_RHYTHM_FIELDS}


def _merge_daily_rhythm(
    runtime_daily_rhythm: dict[str, Any],
    scenario_daily_rhythm: dict[str, Any],
) -> tuple[dict[str, Any], list[str]]:
    merged = {}
    supplemented_fields: list[str] = []
    for field in DAILY_RHYTHM_FIELDS:
        runtime_value = runtime_daily_rhythm.get(field)
        scenario_value = scenario_daily_rhythm.get(field)
        if runtime_value in (None, "") and scenario_value not in (None, ""):
            merged[field] = scenario_value
            supplemented_fields.append(field)
        else:
            merged[field] = runtime_value if runtime_value not in (None, "") else scenario_value
    return merged, supplemented_fields


def _sorted_locations(world: dict[str, Any]) -> list[dict[str, Any]]:
    locations = world.get("locations") or []
    return sorted(
        [
            {
                "name": location.get("name"),
                "parent": location.get("parent"),
                "state": location.get("state"),
                "depth": int(location.get("depth", 0) or 0),
                "agents": sorted(list(location.get("agents") or [])),
            }
            for location in locations
            if isinstance(location, dict)
        ],
        key=lambda item: item["name"] or "",
    )


def _sorted_agents(world: dict[str, Any]) -> list[dict[str, Any]]:
    agents = world.get("agents") or []
    return sorted(
        [
            {
                "name": agent.get("name"),
                "location": agent.get("location"),
                "action": agent.get("action"),
                "traits": agent.get("traits"),
                "socialPreference": agent.get("socialPreference"),
                "canProposeWorldChanges": bool(agent.get("canProposeWorldChanges", False)),
            }
            for agent in agents
            if isinstance(agent, dict)
        ],
        key=lambda item: item["name"] or "",
    )


def build_world_packet(
    ledger_export: dict[str, Any],
    simulation: dict[str, Any],
    scenario_id: str = "",
    source_metadata: dict[str, Any] | None = None,
    *,
    ledger_sha256: str = "",
    simulation_sha256: str = "",
) -> dict[str, Any]:
    if source_metadata is None:
        source_metadata = {
            "ledgerGeneratedAtUtc": ledger_export.get("generatedAtUtc"),
            "ledgerScenarioField": ledger_export.get("scenario"),
            "ledgerExportSha256": ledger_sha256,
            "simulationSha256": simulation_sha256,
        }

    world = ledger_export.get("world") or {}
    world_building = world.get("worldBuilding") or {}
    runtime_daily_rhythm = _ordered_daily_rhythm(world.get("dailyRhythm") or {})
    scenario_daily_rhythm = _ordered_daily_rhythm(simulation.get("dailyRhythm") or {})
    merged_daily_rhythm, supplemented_fields = _merge_daily_rhythm(
        runtime_daily_rhythm=runtime_daily_rhythm,
        scenario_daily_rhythm=scenario_daily_rhythm,
    )

    return {
        "packetType": "world",
        "packetVersion": 1,
        "scenarioId": scenario_id,
        "source": source_metadata,
        "boundary": BOUNDARY,
        "worldSnapshot": {
            "step": int(world.get("step", 0) or 0),
            "tick": int(world.get("tick", 0) or 0),
            "time": world.get("time"),
            "worldSummary": world_building.get("summary"),
            "worldRules": list(world_building.get("rules") or []),
            "allowedLocationStates": list(world_building.get("allowedLocationStates") or []),
            "locations": _sorted_locations(world),
            "agents": _sorted_agents(world),
            "conversationCount": len(world.get("conversations") or []),
            "pendingProposalCount": len(world.get("pendingProposals") or []),
        },
        "dailyRhythmRuntime": runtime_daily_rhythm,
        "dailyRhythmScenario": scenario_daily_rhythm,
        "dailyRhythmMerged": merged_daily_rhythm,
        "dailyRhythmSupplementedFields": supplemented_fields,
        "offlinePolicy": {
            "offlineMode": bool((ledger_export.get("offlinePolicy") or {}).get("offlineMode", False)),
            "loopbackOnly": bool((ledger_export.get("offlinePolicy") or {}).get("loopbackOnly", False)),
        },
        "governancePolicy": {
            "proposalTypes": list((ledger_export.get("governancePolicy") or {}).get("proposalTypes") or []),
            "pendingStatuses": list((ledger_export.get("governancePolicy") or {}).get("pendingStatuses") or []),
        },
        "governanceLedgerCount": len(ledger_export.get("governanceLedger") or []),
        "proposalHistoryCount": len(ledger_export.get("proposalHistoryFull") or []),
    }
