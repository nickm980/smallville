"""Integration tests for the offline packet assembler."""

from __future__ import annotations

import json

from sidecar.assembler import assemble_to_directory

EXPECTED_OUTPUT_FILES = {
    "affect.packet.json",
    "biome-extension-scaffold.json",
    "memory.packet.json",
    "world.packet.json",
}


def test_packet_assembler_writes_expected_files(tmp_path, sidecar_fixture_paths):
    output_dir = tmp_path / "packets"
    written = assemble_to_directory(
        ledger_export_path=sidecar_fixture_paths["ledger_export"],
        simulation_path=sidecar_fixture_paths["simulation"],
        output_dir=output_dir,
    )

    assert {path.name for path in written.values()} == EXPECTED_OUTPUT_FILES
    assert {path.name for path in output_dir.iterdir()} == EXPECTED_OUTPUT_FILES

    for path in written.values():
        payload = json.loads(path.read_text(encoding="utf-8"))
        assert payload["packetVersion"] == 1


def test_packet_assembler_is_repeatable(tmp_path, sidecar_fixture_paths):
    first_dir = tmp_path / "first"
    second_dir = tmp_path / "second"

    assemble_to_directory(
        ledger_export_path=sidecar_fixture_paths["ledger_export"],
        simulation_path=sidecar_fixture_paths["simulation"],
        output_dir=first_dir,
    )
    assemble_to_directory(
        ledger_export_path=sidecar_fixture_paths["ledger_export"],
        simulation_path=sidecar_fixture_paths["simulation"],
        output_dir=second_dir,
    )

    for file_name in sorted(EXPECTED_OUTPUT_FILES):
        assert (first_dir / file_name).read_bytes() == (second_dir / file_name).read_bytes()
