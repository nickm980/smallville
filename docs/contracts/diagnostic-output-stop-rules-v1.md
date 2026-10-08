# Diagnostic Output Stop Rules v1

## Purpose

This note defines stop-and-report behavior for the read-only consumption of already-derived diagnostic outputs.

## Core Stop Rule

Stopping on outputs means halting further downstream consumption when a `stop-report` is present or when a consumed output reveals a triggered stop-rule condition.

## Output-Family Stop Rules

### `summary`

- Stop if consumed `summary` wording is treated as substitute source text.
- Stop if consumed `summary` wording implies runtime authority, mutation authority, execution authority, or canonical replacement.
- Stop if consumed `summary` wording weakens provenance, Water Mill boundaries, packet/world-state separation, or single-dossier continuity.

### `drift-note`

- Stop if consumed `drift-note` is treated as correction authority or approval authority.
- Stop if consumed `drift-note` is used to rewrite, normalize, or replace canonical fixture text.
- Stop if consumed `drift-note` weakens a reported boundary instead of preserving it.

### `stop-report`

- Stop immediately when a consumed `stop-report` flags an output or subject.
- Stop if any downstream reading attempts to continue as though the stop condition were advisory only.
- Stop if any downstream wording recasts a review decision as execution approval.

### `diagnostics-bundle`

- Stop if consumed `diagnostics-bundle` is treated as a package, archive, manifest, file format, or transport artifact.
- Stop if grouping is used to imply canonical replacement, canonical consolidation, or authoritative mirrors.
- Stop if grouped reading weakens the distinction between individual output families.

### `export-summary`

- Stop if consumed `export-summary` is treated as canonical replacement, runtime-facing input, or execution guidance.
- Stop if consumed `export-summary` strips provenance, weakens uncertainty, or inflates Water Mill or packet authority.
- Stop if consumed `export-summary` is used to introduce a second dossier.

## Cross-Family Stop Overlays

Stop and report if any consumed output triggers:

- canonical ID drift
- provenance weakening
- packet/world-state confusion
- Water Mill authority inflation
- inferred-to-observed promotion
- second-dossier introduction
- derived-output replacement
- rewrite-disguised-as-normalization
- runtime coupling
- review decision treated as execution approval

## Human Review Rule

When a stop condition is triggered, downstream consumption may not continue on the flagged output or flagged subject until human review resolves the stop condition.

## Boundary Preservation Rules

All stop behavior must preserve:

- canonical paths as source of truth
- derived outputs as non-authoritative and non-replacing
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, never runtime world state
- single-dossier continuity

## Explicit Stop Conditions

Stop if this note or a later pass:

- weakens upstream stop overlays
- treats stop behavior as optional
- implies runtime authority, mutation authority, or execution authority
- implies canonical replacement
- drifts into tooling, validators, scripts, schemas, packaging, mirrors, or runtime-coupled behavior
