# Fixture Consumer Conformance Matrix v1

## Purpose

This matrix defines which approved consumer profile may perform approved behavior against which canonical fixture type.

All cells remain subordinate to immutable fixture rules, single-dossier continuity, packet/world-state separation, and Water Mill design-asset boundary discipline.

## Matrix

| Consumer profile | Promoted archive record | Promoted derived packet | Promoted agent brief | Promoted review decision |
| --- | --- | --- | --- | --- |
| Fixture Reader | `allowed-with-stop-rules` - Read-only provenance review is allowed, but Water Mill content must remain design-asset-derived context only. | `allowed-with-stop-rules` - Read-only packet review is allowed, but packet text must never be treated as runtime world state. | `allowed` - Read-only brief review is allowed because the brief already preserves non-executing downstream use. | `allowed-with-stop-rules` - Read-only decision review is allowed, but approval language must not drift into execution authority. |
| Fixture Comparator | `allowed-with-stop-rules` - Comparison is allowed for canonical ID, provenance, and fact-status wording, but must stop if Water Mill is elevated to observed built authority. | `allowed-with-stop-rules` - Comparison is allowed for packet boundary and lineage wording, but must stop if packet/world-state separation weakens. | `allowed` - Comparison is allowed for brief linkage, approved-fact wording, and anti-automation guardrails. | `allowed-with-stop-rules` - Comparison is allowed for gate and approval-boundary wording, but must stop if `approved_for_next_gate` is read as execution approval. |
| Fixture Diagnostics Consumer | `allowed-with-stop-rules` - Diagnostics are allowed for provenance and boundary drift, but missing evidence may not be inferred away and Water Mill remains non-authoritative. | `allowed-with-stop-rules` - Diagnostics are allowed for packet boundary drift, but must stop if runtime-state or scene-authority language appears. | `allowed-with-stop-rules` - Diagnostics are allowed for brief provenance and forbidden-assumption drift, but must stop if the brief is reframed as execution authority. | `allowed-with-stop-rules` - Diagnostics are allowed for review-decision drift, but must stop if approval wording becomes runtime or implementation authorization. |
| Fixture Export Consumer | `allowed-with-stop-rules` - Export of non-authoritative summaries is allowed, but exports may not replace the canonical record or flatten design-asset uncertainty. | `allowed-with-stop-rules` - Export of packet diagnostics is allowed, but exports must preserve packet/world-state separation and may not imply world-building authority. | `allowed-with-stop-rules` - Export of brief summaries is allowed, but exports must remain non-canonical and must not promote prompts into truth claims. | `allowed-with-stop-rules` - Export of review-decision summaries is allowed, but exports must preserve review-only meaning and may not imply execution approval. |

## Cell Interpretation Notes

- `allowed` means the profile-fixture interaction is conformant when it stays inside frozen profile rules and canonical fixture boundaries.
- `allowed-with-stop-rules` means the interaction is conformant only while the stop rules remain untriggered.
- `not-allowed` would mean the interaction is out of scope for the approved consumer-profile package. No cell is marked `not-allowed` in this first matrix because all four profiles have bounded read-only use across the four canonical fixture types, but several cells require explicit stop-rule overlays.
- no classification grants blanket downstream authority beyond the bounded interaction described by the approved profile, fixture, and stop-rule context.

## Cross-Cell Preservation Rules

Every cell in this matrix must preserve:
- canonical fixture immutability
- canonical paths as source of truth
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, not runtime world state
- single-dossier continuity
- derived outputs as non-authoritative and non-replacing

## Explicit Stop Overlay References

Each `allowed-with-stop-rules` cell is governed by:
- canonical ID drift stop rules
- provenance weakening stop rules
- packet/world-state confusion stop rules
- Water Mill authority inflation stop rules
- inferred-to-observed promotion stop rules
- second-dossier introduction stop rules
- derived-output replacement stop rules
- rewrite-disguised-as-normalization stop rules
