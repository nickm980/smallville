"""Build the deterministic biome scaffold packet."""

from __future__ import annotations

from typing import Any


BOUNDARY = {
    "hostAuthority": "java",
    "sidecarRole": "deterministic-offline-packet-assembly",
    "mutationAuthority": "none",
}


def _source_metadata(
    ledger_export: dict[str, Any],
    ledger_sha256: str,
    simulation_sha256: str,
) -> dict[str, Any]:
    return {
        "ledgerGeneratedAtUtc": ledger_export.get("generatedAtUtc"),
        "ledgerScenarioField": ledger_export.get("scenario"),
        "ledgerExportSha256": ledger_sha256,
        "simulationSha256": simulation_sha256,
    }


def build_biome_scaffold(
    ledger_export: dict[str, Any],
    simulation: dict[str, Any],
    scenario_id: str = "",
    source_metadata: dict[str, Any] | None = None,
    *,
    ledger_sha256: str = "",
    simulation_sha256: str = "",
) -> dict[str, Any]:
    del simulation
    if source_metadata is None:
        source_metadata = _source_metadata(
            ledger_export,
            ledger_sha256,
            simulation_sha256,
        )

    world = ledger_export.get("world") or {}
    root_locations = sorted(
        location.get("name")
        for location in (world.get("locations") or [])
        if isinstance(location, dict) and not location.get("parent") and location.get("name")
    )

    return {
        "packetType": "biome-scaffold",
        "packetVersion": 1,
        "scenarioId": scenario_id,
        "source": source_metadata,
        "boundary": BOUNDARY,
        "status": "scaffold-only",
        "implemented": False,
        "worldLocationRoots": root_locations,
        "openQuestions": [
            "No biome-specific inputs are exposed in the ledger export surface.",
            "Simulation seed does not define biome identifiers, climate bands, or resource maps.",
            "Any biome simulation semantics would be speculative until a dedicated host-backed seam exists.",
        ],
        "extensionFields": [
            "biomeId",
            "displayName",
            "sourceLocations",
            "climate",
            "seasonality",
            "resources",
            "constraints",
            "notes",
        ],
        "scaffold": {
            "biomes": [],
        },
    }


def build_biome_scaffold_packet(
    ledger_export: dict[str, Any],
    simulation: dict[str, Any],
    *,
    ledger_sha256: str = "",
    simulation_sha256: str = "",
) -> dict[str, Any]:
    return build_biome_scaffold(
        ledger_export,
        simulation,
        ledger_sha256=ledger_sha256,
        simulation_sha256=simulation_sha256,
    )
