# Review Surface Drill Matrix v1

## Purpose

This matrix defines which approved drill behaviors may be applied to which approved diagnostic output families during a bounded review-surface consumption drill.

All cells remain subordinate to canonical source-of-truth rules, Water Mill boundary discipline, packet/world-state separation, single-dossier continuity, and upstream hard-stop overlays.

## Matrix

| Drill behavior | `summary` | `drift-note` | `stop-report` | `diagnostics-bundle` | `export-summary` |
| --- | --- | --- | --- | --- | --- |
| `read-presented-output` | `allowed-with-stop-rules` - May read the presented `summary` as bounded review context only. | `allowed-with-stop-rules` - May read the presented `drift-note` as bounded review context only. | `allowed-with-stop-rules` - May read the presented `stop-report`, but hard-stop meaning must remain intact. | `allowed-with-stop-rules` - May read the presented `diagnostics-bundle` as grouped descriptive context only. | `allowed-with-stop-rules` - May read the presented `export-summary` as bounded review context only. |
| `preserve-presented-order` | `allowed-with-stop-rules` - Preserved order may orient review only. | `allowed-with-stop-rules` - Preserved order may orient review only and may not rank drift into priority. | `allowed-with-stop-rules` - Preserved order may orient review only and may not delay visible stop meaning. | `allowed-with-stop-rules` - Preserved order may orient grouped review only. | `allowed-with-stop-rules` - Preserved order may orient handoff review only and may not imply sequencing authority. |
| `preserve-grouped-review-context` | `allowed-with-stop-rules` - Grouped context may be preserved without creating a new output family. | `allowed-with-stop-rules` - Grouped context may be preserved without creating correction authority. | `allowed-with-stop-rules` - Grouped context may be preserved, but hard-stop visibility must remain explicit. | `allowed-with-stop-rules` - Grouped context may be preserved and may not acquire packaging semantics. | `allowed-with-stop-rules` - Grouped context may be preserved without creating replacement authority. |
| `pause-on-drift` | `allowed-with-stop-rules` - May pause review attention on surfaced drift around a presented `summary` without creating correction authority. | `allowed-with-stop-rules` - May pause review attention on a surfaced `drift-note` without creating prioritization or execution sequencing. | `not-allowed` - `stop-report` requires hard-stop semantics, not drift pause behavior. | `allowed-with-stop-rules` - May pause on drift-oriented content within grouped descriptive context without changing grouped context into control flow. | `allowed-with-stop-rules` - May pause on surfaced drift around an `export-summary` without creating handoff authority. |
| `stop-on-hard-condition` | `allowed-with-stop-rules` - Hard-stop behavior applies when a surfaced hard stop affects the presented `summary`. | `allowed-with-stop-rules` - Hard-stop behavior applies when a surfaced hard stop affects the presented `drift-note`. | `allowed-with-stop-rules` - Hard-stop behavior is effectively mandatory when the presented output is a `stop-report`. | `allowed-with-stop-rules` - Hard-stop behavior applies when grouped context includes a surfaced hard stop. | `allowed-with-stop-rules` - Hard-stop behavior applies when a surfaced hard stop affects the presented `export-summary`. |
| `preserve-canonical-path-visibility` | `allowed` - Required wherever a presented `summary` is referenced in the drill. | `allowed` - Required wherever a presented `drift-note` is referenced in the drill. | `allowed` - Required wherever a presented `stop-report` is referenced in the drill. | `allowed` - Required wherever a presented `diagnostics-bundle` is referenced in the drill. | `allowed` - Required wherever a presented `export-summary` is referenced in the drill. |
| `preserve-provenance-note-visibility` | `allowed-with-stop-rules` - When present, provenance may remain visible as preserved context only. | `allowed-with-stop-rules` - When present, provenance may remain visible without weakening Water Mill or lineage boundaries. | `allowed-with-stop-rules` - When present, provenance may remain visible without softening hard-stop meaning. | `allowed-with-stop-rules` - When present, provenance may remain visible without turning grouped context into mirrors or packages. | `allowed-with-stop-rules` - When present, provenance may remain visible without strengthening export text into replacement authority. |
| `preserve-boundary-note-visibility` | `allowed-with-stop-rules` - When present, boundary notes may remain visible to preserve packet, dossier, or related limits. | `allowed-with-stop-rules` - When present, boundary notes may remain visible to preserve drift review without creating correction authority. | `allowed-with-stop-rules` - When present, boundary notes may remain visible to preserve halt meaning and approval boundaries. | `allowed-with-stop-rules` - When present, boundary notes may remain visible to preserve grouped review context without packaging semantics. | `allowed-with-stop-rules` - When present, boundary notes may remain visible to preserve handoff limits without implying runtime or replacement authority. |
| `preserve-severity-visibility` | `allowed-with-stop-rules` - Severity may remain visible as context only. | `allowed-with-stop-rules` - Severity may remain visible as context only and may not rank drift into priority. | `allowed-with-stop-rules` - Severity may remain visible as context only and may not replace hard-stop meaning. | `allowed-with-stop-rules` - Severity may remain visible as context only and may not create grouped prioritization logic. | `allowed-with-stop-rules` - Severity may remain visible as context only and may not turn handoff context into urgency ranking. |

## Cell Interpretation Notes

- `allowed-with-stop-rules` means the drill behavior is conformant only while all upstream and local stop rules remain untriggered.
- `allowed` means the drill behavior is permitted and still remains subordinate to all cross-cell preservation rules.
- `not-allowed` means the behavior is incompatible with the output family in this phase and may not be reframed by softer wording.
- No matrix cell authorizes UI flow, runtime workflow, automation, validator behavior, control behavior, prioritization logic, execution sequencing, canonical replacement, or second-dossier expansion.

## Cross-Cell Preservation Rules

All matrix cells must preserve:

- canonical paths as source of truth
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, not runtime world state
- single-dossier continuity
- derived outputs as non-authoritative and non-replacing

## Explicit Stop Overlay References

Drill behavior remains subject to the frozen stop overlays for:

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
