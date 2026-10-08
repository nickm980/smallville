# Canonical Fixture Consumer Rules v1

## Purpose

This note defines what future tools, tests, and bounded agents may and may not do when consuming the canonical fixture set.

## Consumers May

Consumers may:
- read fixture files
- validate canonical IDs
- validate presence of schema-aligned sections
- normalize fixture content into transient in-memory representations
- compare fixtures against expected fixture contracts
- export derived diagnostics to separate non-canonical outputs

## Consumers May Not

Consumers may not:
- mutate fixture files
- infer away missing evidence
- promote inferred content to observed
- treat a packet as runtime world state
- treat Water Mill design assets as real-world authority
- couple fixtures to runtime

## Read Rules

Consumers may read:
- file contents
- canonical IDs
- provenance notes
- boundary language
- schema-aligned field labels preserved in the frozen specimen notes

Consumers must read these as immutable references, not editable working copies.

## Validate Rules

Consumers may validate:
- file existence
- canonical ID identity
- artifact type expectations
- presence of required boundary language
- single-dossier continuity
- fact-status discipline
- packet/world-state separation
- Water Mill design-asset boundary

Validation must remain descriptive and must not rewrite or auto-correct source.

## Normalize Rules

Consumers may normalize fixture content only into transient representations for:
- comparison
- indexing
- test fixture loading
- diagnostics generation

Normalization must not:
- write back to fixture files
- erase explicit uncertainty
- collapse `open-question` or `deferred` into stronger states

## Compare Rules

Consumers may compare:
- canonical IDs
- fixed paths
- field presence
- provenance wording
- boundary wording
- fact-status wording

Comparison results must remain downstream diagnostics only.

## Derived Diagnostics Export Rules

Consumers may export derived diagnostics that report:
- missing fields
- drift in canonical ID
- weakened provenance language
- weakened packet/world-state separation
- weakened Water Mill design-asset boundary
- weakened single-dossier continuity

Derived diagnostics must be:
- separate from the canonical fixture files
- clearly labeled as derived outputs
- non-authoritative relative to the canonical source text

## Explicit Non-Authority Rules

No consumer may:
- gain mutation authority
- gain runtime authority
- gain scene/world mutation authority
- promote Water Mill context into real-world built authority without accepted supporting evidence
