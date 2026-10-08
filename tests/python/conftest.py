"""Shared fixtures for sidecar packet tests."""

from __future__ import annotations

from pathlib import Path

import pytest

from sidecar.assembler import assemble_packets_from_files
from sidecar.validators.schema_validator import SchemaValidator

REPO_ROOT = Path(__file__).resolve().parents[2]
FIXTURE_DIR = REPO_ROOT / "tests" / "python" / "fixtures" / "sidecar" / "two-house-garden-v1"


@pytest.fixture(scope="session")
def sidecar_fixture_paths() -> dict[str, Path]:
    return {
        "ledger_export": FIXTURE_DIR / "ledger_export_cold.json",
        "simulation": FIXTURE_DIR / "simulation.yaml",
    }


@pytest.fixture(scope="session")
def sidecar_packets(sidecar_fixture_paths: dict[str, Path]) -> dict[str, dict]:
    return assemble_packets_from_files(
        ledger_export_path=sidecar_fixture_paths["ledger_export"],
        simulation_path=sidecar_fixture_paths["simulation"],
    )


@pytest.fixture(scope="session")
def sidecar_validator() -> SchemaValidator:
    return SchemaValidator(REPO_ROOT)
