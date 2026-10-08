# Fixture Consumer Capability Catalog v1

## Purpose

This note defines the approved capability vocabulary for read-only canonical fixture consumption.

The catalog is descriptive only. It does not define implementations, validators, scripts, or runtime-facing ingestion.

## Scope Note

This v1 catalog is intentionally closed to the capability tokens listed here.

Broader validation-oriented language from the frozen consumer-profile package remains preserved, but it is not subdivided into additional capability tokens in this version.

## Approved Capability Vocabulary

### Read Capabilities

#### `read-canonical-id`

- Category: `read`
- Meaning: read the canonical ID preserved in frozen fixture text.
- Boundary note: may confirm identity but may not mutate, rewrite, or replace the canonical source.

#### `read-provenance-language`

- Category: `read`
- Meaning: read the preserved provenance wording and lineage references in frozen fixture text.
- Boundary note: must preserve Water Mill as design-asset-derived context only and must not inflate provenance into new authority.

#### `read-fact-status-language`

- Category: `read`
- Meaning: read preserved wording about observed, inferred, open-question, deferred, or related uncertainty states where present.
- Boundary note: must not collapse uncertainty or promote inferred content to observed.

#### `read-boundary-language`

- Category: `read`
- Meaning: read preserved boundary wording about packet/world-state separation, approval boundaries, Water Mill handling, and single-dossier continuity.
- Boundary note: must preserve non-executing interpretation and source-of-truth discipline.

### Compare Capabilities

#### `compare-canonical-id`

- Category: `compare`
- Meaning: compare a fixture's preserved canonical ID against the expected frozen canonical identity.
- Boundary note: may report drift only and may not rewrite source.

#### `compare-section-presence`

- Category: `compare`
- Meaning: compare the presence of expected schema-aligned or contract-aligned sections in frozen fixture text.
- Boundary note: missing sections may be reported but not auto-repaired.

#### `compare-provenance-language`

- Category: `compare`
- Meaning: compare preserved provenance wording against approved expectations from the frozen chain.
- Boundary note: may not weaken or replace provenance wording through comparison output.

#### `compare-boundary-language`

- Category: `compare`
- Meaning: compare preserved boundary wording such as packet/world-state separation, Water Mill handling, approval-boundary wording, and single-dossier continuity.
- Boundary note: may not reinterpret packet text as runtime authority or Water Mill text as observed built authority.

### Derive Capabilities

#### `derive-diagnostics`

- Category: `derive`
- Meaning: derive external, non-authoritative diagnostics about fixture preservation and boundary integrity.
- Boundary note: derived diagnostics must remain outside canonical fixture files and may not replace canonical source text.

#### `derive-drift-notes`

- Category: `derive`
- Meaning: derive external notes describing canonical ID drift, provenance drift, or boundary-language drift.
- Boundary note: drift notes may report weakening only and may not become substitute canonical truth.

### Export Capabilities

#### `export-summary`

- Category: `export`
- Meaning: export non-authoritative summary text describing frozen fixture content or preserved boundary conditions.
- Boundary note: summaries may not become canonical replacements and may not imply runtime or execution authority.

#### `export-diagnostics`

- Category: `export`
- Meaning: export non-authoritative diagnostics previously derived from approved read-only fixture consumption.
- Boundary note: exported diagnostics must remain downstream only and may not overwrite, mirror, or replace canonical fixtures.

## Forbidden Capability Vocabulary

#### `mutate-fixture`

- Category: `forbidden`
- Meaning: alter the canonical fixture file or its frozen text.
- Why forbidden: canonical fixtures are immutable.

#### `rewrite-fixture`

- Category: `forbidden`
- Meaning: rewrite wording, repair sections, or normalize the canonical fixture in place.
- Why forbidden: rewrite behavior becomes hidden mutation.

#### `replace-canonical-source`

- Category: `forbidden`
- Meaning: treat a derived note, summary, export, or mirror as a new canonical source.
- Why forbidden: canonical paths remain the sole source of truth.

#### `bind-to-runtime`

- Category: `forbidden`
- Meaning: bind a canonical fixture or derived output to live runtime state or runtime-facing ingestion.
- Why forbidden: the capability layer is non-executing and runtime-decoupled.

#### `infer-away-uncertainty`

- Category: `forbidden`
- Meaning: erase open-question, deferred, or otherwise uncertain content during reading, comparison, derivation, or export.
- Why forbidden: evidence discipline must remain explicit.

#### `promote-inferred-to-observed`

- Category: `forbidden`
- Meaning: recast inferred content as observed built authority without accepted evidence.
- Why forbidden: this breaks fact-status discipline, Water Mill boundaries, and single-dossier integrity.

## Explicit Boundary Notes

All approved capability tokens must preserve:
- canonical fixture immutability
- canonical paths as source of truth
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, not runtime world state
- single-dossier continuity
- derived outputs as non-authoritative and non-replacing
