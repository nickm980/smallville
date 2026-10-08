"""Build the deterministic memory packet."""

from __future__ import annotations

from typing import Any


BOUNDARY = {
    "hostAuthority": "java",
    "sidecarRole": "deterministic-offline-packet-assembly",
    "mutationAuthority": "none",
}


def _ordered_memory_config(simulation: dict[str, Any]) -> dict[str, Any]:
    memory = simulation.get("memory") or {}
    return {
        "dreamIntervalTicks": memory.get("dreamIntervalTicks"),
        "dreamWindowStart": memory.get("dreamWindowStart"),
        "dreamWindowEnd": memory.get("dreamWindowEnd"),
        "hotMemoryLimit": memory.get("hotMemoryLimit"),
        "recallTopK": memory.get("recallTopK"),
        "archiveCompression": memory.get("archiveCompression"),
        "ponderEnabled": memory.get("ponderEnabled"),
        "ponderBlendAlpha": memory.get("ponderBlendAlpha"),
        "ponderCooldownTicks": memory.get("ponderCooldownTicks"),
        "eveningSocialBoost": memory.get("eveningSocialBoost"),
        "eveningActivationDamp": memory.get("eveningActivationDamp"),
    }


def build_memory_packet(
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
                "seedTraits": seed.get("traits"),
                "seedGoals": list(seed.get("goals") or []),
                "seedRituals": list(seed.get("rituals") or []),
                "seedPersona": list(seed.get("persona") or []),
                "seedMemories": list(seed.get("memories") or []),
                "seedWorkingMemories": list(seed.get("workingMemories") or []),
                "seedLocation": seed.get("location"),
                "seedActivity": seed.get("activity"),
                "socialPreference": seed.get("socialPreference"),
                "canProposeWorldChanges": bool(seed.get("canProposeWorldChanges", False)),
                "memoryIndex": {
                    "agentName": index.get("agentName", agent_name),
                    "totalEvents": int(index.get("totalEvents", 0) or 0),
                    "hotCount": int(index.get("hotCount", 0) or 0),
                    "archivedCount": int(index.get("archivedCount", 0) or 0),
                    "dreamPackCount": int(index.get("dreamPackCount", 0) or 0),
                    "lastDreamTick": int(index.get("lastDreamTick", 0) or 0),
                    "lastRecallTick": int(index.get("lastRecallTick", 0) or 0),
                    "ponderCount": int(index.get("ponderCount", 0) or 0),
                    "lastPonderTick": int(index.get("lastPonderTick", 0) or 0),
                    "lastAffectPresent": index.get("lastAffect") is not None,
                },
            }
        )

    return {
        "packetType": "memory",
        "packetVersion": 1,
        "scenarioId": scenario_id,
        "source": source_metadata,
        "boundary": BOUNDARY,
        "memoryConfig": _ordered_memory_config(simulation),
        "agents": agents,
    }
