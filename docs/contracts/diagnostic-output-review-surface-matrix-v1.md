# Diagnostic Output Review Surface Matrix v1

## Purpose

This matrix defines which approved review-surface behaviors may be applied to which approved diagnostic output families.

All cells remain subordinate to canonical source-of-truth rules, Water Mill boundary discipline, packet/world-state separation, single-dossier continuity, and upstream stop overlays.

## Matrix

| Review-surface behavior | `summary` | `drift-note` | `stop-report` | `diagnostics-bundle` | `export-summary` |
| --- | --- | --- | --- | --- | --- |
| `group-for-review` | `allowed-with-stop-rules` - May group a `summary` with other outputs for review context only and may not create a new output family. | `allowed-with-stop-rules` - May group a `drift-note` for review context only and may not turn drift into package semantics. | `allowed-with-stop-rules` - May group a `stop-report`, but the halt requirement must remain explicit and visible. | `allowed-with-stop-rules` - May group a `diagnostics-bundle` with other presented outputs, but grouping may not rewrite the bundle into packaging semantics. | `allowed-with-stop-rules` - May group an `export-summary` for bounded handoff review only and may not create replacement authority. |
| `sort-by-existing-field` | `allowed-with-stop-rules` - May sort by existing fields only and may not recast `summary` review order as priority. | `allowed-with-stop-rules` - May sort by existing fields only and may not hide drift risk or provenance limits. | `allowed-with-stop-rules` - May sort by existing fields only, but stop visibility may not be suppressed or downgraded. | `allowed-with-stop-rules` - May sort by existing fields only and may not turn grouped context into ranking logic. | `allowed-with-stop-rules` - May sort by existing fields only and may not imply export priority or new authority. |
| `filter-by-existing-field` | `allowed-with-stop-rules` - May filter by existing fields only, but canonical-path visibility and review-only meaning must remain explicit for presented items. | `allowed-with-stop-rules` - May filter by existing fields only, but provenance and drift context for presented items may not be hidden. | `allowed-with-stop-rules` - May filter by existing fields only, but a present stop condition may not be concealed or softened. | `allowed-with-stop-rules` - May filter by existing fields only and may not make grouped context appear canonical or packaged. | `allowed-with-stop-rules` - May filter by existing fields only and may not conceal that the exported text remains non-authoritative. |
| `highlight-severity` | `allowed-with-stop-rules` - May highlight existing severity only as output-local boundary seriousness. | `allowed-with-stop-rules` - May highlight existing severity only and may not turn drift into ranking or correction authority. | `allowed-with-stop-rules` - May highlight existing severity only and may not replace the stop condition with urgency scoring. | `allowed-with-stop-rules` - May highlight existing severity only and may not create grouped prioritization logic. | `allowed-with-stop-rules` - May highlight existing severity only and may not turn export-oriented text into action priority. |
| `highlight-stop-condition` | `allowed-with-stop-rules` - May highlight a triggered stop condition affecting a `summary`, and this becomes effectively mandatory when present. | `allowed-with-stop-rules` - May highlight a triggered stop condition affecting a `drift-note`, and this becomes effectively mandatory when present. | `allowed-with-stop-rules` - May highlight the `stop-report` condition itself, and this becomes effectively mandatory when present. | `allowed-with-stop-rules` - May highlight a triggered stop condition within grouped review context, and this becomes effectively mandatory when present. | `allowed-with-stop-rules` - May highlight a triggered stop condition affecting an `export-summary`, and this becomes effectively mandatory when present. |
| `show-canonical-path-reference` | `allowed` - Required wherever a `summary` is presented to preserve source-of-truth visibility. | `allowed` - Required wherever a `drift-note` is presented to preserve source-of-truth visibility. | `allowed` - Required wherever a `stop-report` is presented to preserve source-of-truth visibility. | `allowed` - Required wherever a `diagnostics-bundle` is presented to preserve source-of-truth visibility. | `allowed` - Required wherever an `export-summary` is presented to preserve source-of-truth visibility. |
| `show-provenance-note` | `allowed-with-stop-rules` - When present, provenance may be shown as preserved context only and may not become new authority. | `allowed-with-stop-rules` - When present, provenance may be shown as preserved context only and may not weaken Water Mill or lineage boundaries. | `allowed-with-stop-rules` - When present, provenance may be shown as preserved context only and may not alter stop meaning. | `allowed-with-stop-rules` - When present, provenance may be shown as preserved context only and may not turn grouped review into a mirror set. | `allowed-with-stop-rules` - When present, provenance may be shown as preserved context only and may not strengthen exported text into replacement authority. |
| `show-boundary-notes` | `allowed-with-stop-rules` - When present, boundary notes may be shown to preserve packet, dossier, or related limits. | `allowed-with-stop-rules` - When present, boundary notes may be shown to preserve drift awareness without creating correction authority. | `allowed-with-stop-rules` - When present, boundary notes may be shown to preserve halt meaning and approval boundaries. | `allowed-with-stop-rules` - When present, boundary notes may be shown to preserve grouped review context without creating packaging semantics. | `allowed-with-stop-rules` - When present, boundary notes may be shown to preserve handoff limits without implying runtime or replacement authority. |

## Cell Interpretation Notes

- `allowed-with-stop-rules` means the behavior is conformant only while all upstream and local stop rules remain untriggered.
- `allowed` means the behavior is permitted as part of bounded presentation and still remains subject to all cross-cell preservation rules.
- No matrix cell authorizes runtime coupling, mutation, execution, canonical replacement, packaging semantics, scoring, or second-dossier expansion.

## Cross-Cell Preservation Rules

All matrix cells must preserve:

- canonical paths as source of truth
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, not runtime world state
- single-dossier continuity
- derived outputs as non-authoritative and non-replacing

## Explicit Stop Overlay References

Review-surface presentation remains subject to the frozen stop overlays for:

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
