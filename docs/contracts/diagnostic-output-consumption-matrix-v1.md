# Diagnostic Output Consumption Matrix v1

## Purpose

This matrix defines which approved consumer profile may consume which approved diagnostic output family under the frozen read-only governance ladder.

All cells remain subordinate to canonical source-of-truth rules, Water Mill boundary discipline, packet/world-state separation, single-dossier continuity, and upstream stop overlays.

## Matrix

| Consumer profile | `summary` | `drift-note` | `stop-report` | `diagnostics-bundle` | `export-summary` |
| --- | --- | --- | --- | --- | --- |
| `Fixture Reader` | `allowed-with-stop-rules` - May read as a review aid only, but may not treat it as substitute source text. | `allowed-with-stop-rules` - May read for drift awareness, but may not treat it as correction or approval authority. | `allowed-with-stop-rules` - May read to recognize a halt condition, and consumption must stop on the flagged output or subject. | `allowed-with-stop-rules` - May read as grouped descriptive material only, but may not infer package or manifest semantics. | `allowed-with-stop-rules` - May read as downstream handoff text only, but it remains non-canonical and non-authoritative. |
| `Fixture Comparator` | `allowed-with-stop-rules` - May consume to compare downstream wording against canonical source, but may not replace source text. | `allowed-with-stop-rules` - May consume to compare reported drift against canonical wording, but may not turn drift into correction authority. | `allowed-with-stop-rules` - May consume to verify that a stop condition has been triggered, and may not downgrade it into advisory-only text. | `allowed-with-stop-rules` - May consume grouped outputs for comparison context only, but may not normalize them into substitute source sets. | `allowed-with-stop-rules` - May compare handoff wording against canonical source and upstream outputs, but may not treat exported text as a new authority. |
| `Fixture Diagnostics Consumer` | `allowed-with-stop-rules` - May consume as review context for boundary integrity, but must preserve that `summary` is review-only. | `allowed-with-stop-rules` - May consume as drift context, but may not upgrade it into approval or mutation authority. | `allowed-with-stop-rules` - May consume as a hard-stop signal, and downstream consumption must halt until human review resolves the stop condition. | `allowed-with-stop-rules` - May consume grouped diagnostics for audit context only, but may not interpret grouping as package semantics. | `allowed-with-stop-rules` - May consume exported review text for downstream diagnostics context only, but it remains non-replacing and non-authoritative. |
| `Fixture Export Consumer` | `allowed-with-stop-rules` - May consume as read-only review input, but may not treat it as a substitute source for export authority. | `allowed-with-stop-rules` - May consume as drift context for bounded handoff, but may not convert drift into correction authority. | `allowed-with-stop-rules` - May consume to preserve halt conditions in downstream handoff, and may not continue past a triggered stop condition. | `allowed-with-stop-rules` - May consume as grouped descriptive context only, but may not acquire packaging, archive, manifest, file-format, or transport semantics. | `allowed-with-stop-rules` - May consume as downstream review aid only and may not become canonical replacement, runtime-facing input, or execution guidance. |

## Cell Interpretation Notes

- `allowed-with-stop-rules` means the interaction is conformant only while all upstream and local stop rules remain untriggered.
- No matrix cell grants blanket downstream authority beyond the bounded interaction described by the approved consumer profile, approved output family, and applicable stop-rule context.
- No matrix cell authorizes runtime coupling, mutation, execution, canonical replacement, or second-dossier expansion.

## Cross-Cell Preservation Rules

All matrix cells must preserve:

- canonical paths as source of truth
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, not runtime world state
- single-dossier continuity
- derived outputs as non-authoritative and non-replacing

## Explicit Stop Overlay References

Consumption remains subject to the frozen stop overlays for:

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
