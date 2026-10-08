"""Assemble deterministic sidecar packets from ledger export and simulation YAML."""



from __future__ import annotations



import argparse

import hashlib

import json

from pathlib import Path

from typing import Any



import yaml



from sidecar.packets.affect_packet import build_affect_packet

from sidecar.packets.biome_scaffold import build_biome_scaffold

from sidecar.packets.memory_packet import build_memory_packet

from sidecar.packets.world_packet import build_world_packet

from sidecar.validators.schema_validator import SchemaValidator



OUTPUT_FILE_NAMES = {

    "memory": "memory.packet.json",

    "affect": "affect.packet.json",

    "world": "world.packet.json",

    "biome_scaffold": "biome-extension-scaffold.json",

}



SCHEMA_FILE_NAMES = {

    "memory": "memory-packet.schema.yaml",

    "affect": "affect-packet.schema.yaml",

    "world": "world-packet.schema.yaml",

    "biome_scaffold": "biome-extension-scaffold.schema.yaml",

}





def _load_json_document(path: Path) -> dict[str, Any]:

    with path.open("r", encoding="utf-8-sig") as handle:

        raw = json.load(handle)

    if not isinstance(raw, dict):

        raise ValueError(f"{path} does not contain a JSON object")

    body = raw.get("body")

    if isinstance(body, dict) and "generatedAtUtc" in body and "world" in body:

        return body

    return raw





def _load_yaml_document(path: Path) -> dict[str, Any]:

    with path.open("r", encoding="utf-8") as handle:

        raw = yaml.safe_load(handle)

    if not isinstance(raw, dict):

        raise ValueError(f"{path} does not contain a YAML object")

    return raw





def _sha256(path: Path) -> str:

    digest = hashlib.sha256()

    with path.open("rb") as handle:

        for chunk in iter(lambda: handle.read(65536), b""):

            digest.update(chunk)

    return digest.hexdigest()





def _build_source_metadata(

    ledger_export: dict[str, Any],

    ledger_export_path: Path,

    simulation_path: Path,

) -> dict[str, Any]:

    return {

        "ledgerGeneratedAtUtc": ledger_export.get("generatedAtUtc"),

        "ledgerScenarioField": ledger_export.get("scenario"),

        "ledgerExportSha256": _sha256(ledger_export_path),

        "simulationSha256": _sha256(simulation_path),

    }





def assemble_packets_from_files(

    ledger_export_path: str | Path,

    simulation_path: str | Path,

) -> dict[str, dict[str, Any]]:

    ledger_path = Path(ledger_export_path)

    simulation_file_path = Path(simulation_path)

    ledger_export = _load_json_document(ledger_path)

    simulation = _load_yaml_document(simulation_file_path)

    scenario_id = simulation_file_path.resolve().parent.name

    source_metadata = _build_source_metadata(

        ledger_export=ledger_export,

        ledger_export_path=ledger_path,

        simulation_path=simulation_file_path,

    )



    packets = {

        "memory": build_memory_packet(

            ledger_export=ledger_export,

            simulation=simulation,

            scenario_id=scenario_id,

            source_metadata=source_metadata,

        ),

        "affect": build_affect_packet(

            ledger_export=ledger_export,

            simulation=simulation,

            scenario_id=scenario_id,

            source_metadata=source_metadata,

        ),

        "world": build_world_packet(

            ledger_export=ledger_export,

            simulation=simulation,

            scenario_id=scenario_id,

            source_metadata=source_metadata,

        ),

        "biome_scaffold": build_biome_scaffold(

            ledger_export=ledger_export,

            simulation=simulation,

            scenario_id=scenario_id,

            source_metadata=source_metadata,

        ),

    }



    validator = SchemaValidator()

    for packet_key, packet_body in packets.items():

        validator.validate(packet_body, SCHEMA_FILE_NAMES[packet_key])



    return packets





def write_packets(

    packets: dict[str, dict[str, Any]],

    output_dir: str | Path,

) -> dict[str, Path]:

    target_dir = Path(output_dir)

    target_dir.mkdir(parents=True, exist_ok=True)

    written_paths: dict[str, Path] = {}



    for packet_key, packet_body in packets.items():

        output_path = target_dir / OUTPUT_FILE_NAMES[packet_key]

        with output_path.open("w", encoding="utf-8", newline="\n") as handle:
            json.dump(packet_body, handle, indent=2, ensure_ascii=False, sort_keys=True)
            handle.write("\n")

        written_paths[packet_key] = output_path



    return written_paths





def assemble_to_directory(

    ledger_export_path: str | Path,

    simulation_path: str | Path,

    output_dir: str | Path,

) -> dict[str, Path]:

    packets = assemble_packets_from_files(

        ledger_export_path=ledger_export_path,

        simulation_path=simulation_path,

    )

    return write_packets(packets=packets, output_dir=output_dir)





def _parse_args(argv: list[str] | None = None) -> argparse.Namespace:

    parser = argparse.ArgumentParser(

        description="Assemble deterministic SmallVille sidecar packets."

    )

    parser.add_argument(

        "--ledger-export",

        required=True,

        help="Path to a ledger export JSON file or endpoint-capture wrapper.",

    )

    parser.add_argument(

        "--simulation",

        required=True,

        help="Path to the scenario simulation.yaml file.",

    )

    parser.add_argument(

        "--output-dir",

        required=True,

        help="Directory that will receive the packet JSON artifacts.",

    )

    return parser.parse_args(argv)





def main(argv: list[str] | None = None) -> int:

    args = _parse_args(argv)

    written_paths = assemble_to_directory(

        ledger_export_path=args.ledger_export,

        simulation_path=args.simulation,

        output_dir=args.output_dir,

    )

    for packet_key in sorted(written_paths):

        print(f"{packet_key}: {written_paths[packet_key]}")

    return 0





if __name__ == "__main__":

    raise SystemExit(main())

