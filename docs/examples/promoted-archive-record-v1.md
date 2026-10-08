# Promoted Archive Record v1

## Selected Canonical Specimen

- `record_id`: `rec-water-mill-design-asset-pack`
- Source family: [`archive-record-examples-dossier-v1.md`](C:/SmallVille/docs/examples/archive-record-examples-dossier-v1.md)

## Why This Is The Best First Canonical Target

This record is the strongest first canonical archive specimen because it has the clearest provenance chain in the frozen set:
- direct pack inventory
- manifest-backed component listing
- note-backed interpretation boundaries
- explicit design-asset handling rules

It also carries the richest non-executing Water Mill context while still preserving the rule that designed structures are not observed real-world authority.

## Promotion Rationale

- Provenance clarity: strongest in the set because it points directly to `README.md`, `design_observations.md`, `asset_manifest.json`, and the image files.
- Schema alignment quality: already cleanly populated across `record_id`, `source_type`, `source_path_or_uri`, `snapshot_type`, `source_hash`, `provenance_status`, `fact_items`, and `notes`.
- Interpretive richness without overclaiming: rich enough to support later packet language while still keeping hidden mechanics, dimensions, and authority deferred.
- Tooling/test usefulness: ideal first fixture for provenance parsing, design-asset boundary assertions, and fact-status checks.
- Single-dossier discipline: supports the same dossier without introducing extra operational sources.
- Non-executing boundary strength: explicitly states static design-asset context only.

## Canonical Fields

- `record_id`: `rec-water-mill-design-asset-pack`
- `dossier_id`: `dossier-bootstrap-v1`
- `source_type`: `design-asset-pack`
- `source_title`: `Water Mill context pack`
- `source_path_or_uri`: `C:\Users\callu\Downloads\water_mill_codex_context_pack.zip`
- `snapshot_type`: `design asset pack`
- `source_hash`: `example-water-mill-zip-hash-not-captured-in-this-spec`
- `provenance_status`: `approved static design-asset reference`
- `fact_status`: `observed`
- `owner_layer`: `Water Mill context pack`
- `summary`: `Approved static design-asset pack containing nine images, manifests, and note files for descriptive dossier vocabulary only.`

## Canonical Use Boundary

This promoted record may serve as:
- the provenance anchor for future fixture design
- the design-asset intake specimen for schema-shape tests
- the source record anchor for the promoted packet

It must not be treated as:
- runtime authority
- real-world structure proof
- implementation instruction
