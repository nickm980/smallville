"""Build the deterministic affect packet."""

from __future__ import annotations

from typing import Any


BOUNDARY = {
    "hostAuthority": "java",
    "sidecarRole": "deterministic-offline-packet-assembly",
    "mutationAuthority": "none",
}

FORMULA_MIRRORS = {
    "dreamValence": "clamp((avgImportance / 5.0) - 1.0, -1.0, 1.0)",
    "dreamActivation": "clamp(avgImportance / 10.0, 0.0, 1.0)",
    "dreamSocialDrive": "ratio of hot memories matching social-word heuristic",
    "ponderValence": "clamp((avgImportance / 5.0) - 0.5, -1.0, 1.0)",
    "ponderBlend": "alpha * currentValence + (1.0 - alpha) * ponderValence",
    "eveningWindModulation": "socialDrive + eveningSocialBoost and activation - eveningActivationDamp",
}


def _affect_config(simulation: dict[str, Any]) -> dict[str, Any]:
    memory = simulation.get("memory") or {}
    daily_rhythm = simulation.get("dailyRhythm") or {}
    return {
        "dreamWindowStart": memory.get("dreamWindowStart"),
        "dreamWindowEnd": memory.get("dreamWindowEnd"),
        "ponderEnabled": memory.get("ponderEnabled"),
        "ponderBlendAlpha": memory.get("ponderBlendAlpha"),
        "ponderCooldownTicks": memory.get("ponderCooldownTicks"),
        "eveningSocialBoost": memory.get("eveningSocialBoost"),
        "eveningActivationDamp": memory.get("eveningActivationDamp"),
        "breakWindows": {
            "morningTea": daily_rhythm.get("morningTea"),
            "lunch": daily_rhythm.get("lunch"),
            "afternoonTea": daily_rhythm.get("afternoonTea"),
            "eveningWind": daily_rhythm.get("eveningWind"),
        },
    }


def build_affect_packet(
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

    agents_by_name = {
        agent.get("name"): agent
        for agent in simulation.get("agents", [])
        if isinstance(agent, dict) and agent.get("name")
    }
    memory_index = ledger_export.get("memoryIndex") or {}
    agent_names = sorted(set(agents_by_name) | set(memory_index))

    agents = []
    for agent_name in agent_names:
        seed = agents_by_name.get(agent_name) or {}
        index = memory_index.get(agent_name) or {}
        agents.append(
            {
                "agentName": agent_name,
                "socialPreference": seed.get("socialPreference"),
                "lastDreamTick": int(index.get("lastDreamTick", 0) or 0),
                "lastPonderTick": int(index.get("lastPonderTick", 0) or 0),
                "ponderCount": int(index.get("ponderCount", 0) or 0),
                "affectAvailable": index.get("lastAffect") is not None,
                "affectState": index.get("lastAffect"),
                "affectSource": "ledger.memoryIndex.lastAffect"
                if index.get("lastAffect") is not None
                else "missing",
            }
        )

    return {
        "packetType": "affect",
        "packetVersion": 1,
        "scenarioId": scenario_id,
        "source": source_metadata,
        "boundary": BOUNDARY,
        "affectConfig": _affect_config(simulation),
        "formulaMirrors": FORMULA_MIRRORS,
        "agents": agents,
    }
