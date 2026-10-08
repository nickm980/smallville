# Diagnostic Output Interpretation Rules v1

## Purpose

This note defines how approved consumer profiles may assign bounded review meaning to already-derived diagnostic outputs without creating new authority.

## Core Distinctions

Producing outputs means deriving an approved diagnostic output family from canonical fixtures under an approved read-only capability.

Consuming outputs means read-only downstream use of an already-derived diagnostic output without changing its authority level.

Interpreting outputs means assigning bounded review meaning to a diagnostic output without elevating it into source truth, runtime state, or execution approval.

Stopping on outputs means halting further downstream consumption when a `stop-report` is present or when a consumed output reveals a triggered stop-rule condition.

## Interpretation Rules By Output Family

### `summary`

`summary` may be consumed as a review aid only and may not be treated as substitute source text.

Interpretation of `summary` is limited to bounded review understanding, provenance-aware reading, and boundary-preserving downstream orientation.

### `drift-note`

`drift-note` may trigger review attention only and may not create approval, correction, replacement, or execution authority.

Interpretation of `drift-note` is limited to recognizing reported weakening, drift, or boundary risk relative to canonical source text and frozen governance language.

### `stop-report`

`stop-report` must halt downstream consumption of the flagged output or flagged subject until human review resolves the stop condition.

Interpretation of `stop-report` is limited to recognizing a triggered hard boundary and preserving the halt requirement. It may not be reinterpreted as discretionary guidance.

### `diagnostics-bundle`

`diagnostics-bundle` may be read as a grouped descriptive output only and may not acquire packaging, archive, manifest, file-format, or transport semantics.

Interpretation of `diagnostics-bundle` is limited to grouped review context and audit context. Grouping does not change authority level.

### `export-summary`

`export-summary` may be handed off as a downstream review aid only and may not become canonical replacement, runtime-facing input, or execution guidance.

Interpretation of `export-summary` is limited to bounded downstream review or handoff context while preserving canonical source-of-truth rules.

## Capability Naming Boundary

`export-diagnostics` remains a capability name from the frozen capability layer and is not a sixth diagnostic output family.

Interpretation rules may refer to capability context, but they may not redefine capability names as output-family names.

## Cross-Family Preservation Rules

All output interpretation must preserve:

- canonical paths as source of truth
- derived outputs as non-authoritative and non-replacing
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, never runtime world state
- single-dossier continuity
- no runtime authority
- no mutation authority
- no execution authority

## Explicit Stop Conditions

Stop if interpretation wording:

- upgrades a consumed output into source truth
- downgrades a `stop-report` into advisory-only text
- treats a grouped output as package semantics
- treats packet content as runtime world state
- treats Water Mill design-asset-derived context as observed built authority without accepted evidence
- weakens single-dossier continuity
- implies runtime authority, mutation authority, execution authority, or canonical replacement
