# Canonical Fixture Validation Rules v1

## Purpose

This note defines the validation expectations for the canonical fixture set.

Validation is descriptive only and does not authorize mutation, repair, or replacement.

## Validation Expectations

### 1. File Exists

Each canonical fixture path must exist at the expected frozen location.

### 2. Canonical ID Matches

Each fixture must preserve the promoted canonical ID exactly:
- archive record: `rec-water-mill-design-asset-pack`
- derived packet: `pkt-dossier-bootstrap-water-mill-v1`
- agent brief: `brief-review-analyst-bootstrap-v1`
- review decision: `rev-brief-review-analyst-v1`

### 3. Required Sections Or Schema-Aligned Content Present

Validation should confirm that each fixture preserves its expected schema-aligned or contract-aligned sections.

Examples:
- archive record fixture preserves canonical fields and use boundary
- packet fixture preserves canonical fields and packet boundary
- brief fixture preserves canonical fields and use boundary
- review decision fixture preserves canonical fields and approval-boundary wording

### 4. Provenance Language Preserved

Validation should confirm that provenance language remains explicit and anchored to the frozen promoted chain.

### 5. Fact-Status Discipline Preserved

Validation should confirm that fixture wording does not:
- erase `open-question`
- erase `deferred`
- silently promote inferred content to observed

### 6. Packet/World-State Separation Preserved

Validation should confirm that the promoted packet fixture and all downstream fixtures preserve the rule that the packet is not runtime world state.

### 7. Water Mill Design-Asset Boundary Preserved

Validation should confirm that Water Mill content remains:
- design-asset-derived context only
- non-authoritative for observed real-world status
- non-executing

### 8. Single-Dossier Boundary Preserved

Validation should confirm that no second dossier is introduced and that `tiny-world` remains control context only.

## Validation Output Discipline

Validation outputs may report:
- pass
- fail
- warning

Validation outputs must not:
- rewrite fixtures
- repair fixtures
- replace missing sections automatically

## Explicit Stop Conditions

Stop validation-driven promotion if any result indicates:
- canonical ID drift
- missing provenance note
- packet/world-state language weakened
- Water Mill treated as observed built fact
- second dossier silently merged
