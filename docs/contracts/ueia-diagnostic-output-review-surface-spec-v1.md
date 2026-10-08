# UEIA Diagnostic Output Review Surface Spec v1

## Purpose

This specification defines the bounded review-surface governance layer for already-consumed diagnostic outputs in the frozen UEIA fixture ladder. It describes how future review surfaces may present those outputs for review without changing authority, weakening canonical source-of-truth rules, or drifting into tooling design, UI implementation, runtime coupling, validation, scoring, packaging, or control behavior.

## Baseline References

- Active accepted checkpoint tag: `smallville-post-proposal-parent-fix`
- Active accepted checkpoint commit: `150e2073fdb608efb4e76ad05cdacbfdb7119222`
- Provenance pack commit: `ab89743f19077d0e23c9a6270658a511f653c55e`
- Operator-surface consolidation freeze commit: `13732640d71a4a842142c37243bc46961f75eb5e`
- Diagnostic-output consumption contracts freeze commit: `5c67784cd13def6104b33bbc3ab8f748e2cd7fe1`
- Diagnostic-output consumption examples freeze commit: `3f01f85c2d07de90a83b17085b8b9867d60a51ef`

## Upstream Authority

This layer remains subordinate to the frozen:

- canonical fixture authority
- consumer profile authority
- conformance authority
- capability crosswalk authority
- diagnostic-output authority
- diagnostic-output consumption authority

## Fixture Scope

Only these canonical fixtures are in scope:

- `docs/examples/promoted-archive-record-v1.md`
- `docs/examples/promoted-derived-packet-v1.md`
- `docs/examples/promoted-agent-brief-v1.md`
- `docs/examples/promoted-review-decision-v1.md`

## Consumer-Profile Scope

Only these approved consumer profiles are in scope:

- `Fixture Reader`
- `Fixture Comparator`
- `Fixture Diagnostics Consumer`
- `Fixture Export Consumer`

## Diagnostic Output Scope

Only these frozen output families are in scope:

- `summary`
- `drift-note`
- `stop-report`
- `diagnostics-bundle`
- `export-summary`

## Review-Surface Behavior Scope

Only these review-surface behaviors are in scope:

- `group-for-review`
- `sort-by-existing-field`
- `filter-by-existing-field`
- `highlight-severity`
- `highlight-stop-condition`
- `show-canonical-path-reference`
- `show-provenance-note`
- `show-boundary-notes`

## Presentation Model

Presenting outputs means exposing already-consumed diagnostic outputs for bounded review without changing their authority, provenance, or stop status.

Grouping outputs for review means arranging already-consumed outputs together for bounded review context only and may not create a package, archive, manifest, canonical set, or new output family.

Highlighting stop conditions means making an already-present stop condition more visible for review and may not downgrade, override, or soften the halt requirement.

Preserving canonical-path visibility means the canonical path for the underlying fixture remains visible wherever a diagnostic output is presented.

Sorting and filtering are review-orientation behaviors only and may not suppress the existence of triggered stop conditions, canonical-path visibility, provenance notes, or boundary notes.

Severity may be highlighted as output-local boundary seriousness only and may not be recast as execution priority, urgency ranking, action score, or prioritization logic.

A review surface may show provenance notes and boundary notes as preserved context only and may not strengthen them into new authority.

A review surface may present a `diagnostics-bundle` as grouped descriptive review context only and may not acquire packaging, archive, manifest, file-format, or transport semantics.

A review surface may show canonical path references for source-of-truth visibility, but may not treat surfaced paths as replacement mirrors or mutable targets.

No review-surface presentation may hide that the canonical fixture at the canonical path remains source of truth.

## Source-of-Truth Rule

Canonical fixture files at canonical paths remain the source of truth. Review-surface presentation may increase visibility, arrange review order, or preserve contextual reading aids, but it may not replace, normalize, override, mirror, or supersede canonical fixture text.

## Non-Authoritative Presentation Rule

All presented diagnostic outputs remain derived only, non-authoritative, and non-replacing. Presentation does not upgrade authority, create approval rights, or soften upstream stop behavior.

## Boundary Preservation Rules

- Water Mill remains design-asset-derived context only and may not be restated as observed built authority without accepted evidence.
- The promoted derived packet remains an interpretive artifact only and never runtime world state.
- Single-dossier continuity remains explicit and may not be weakened by presentation language, ordering, filtering, or grouping.
- Presented outputs remain non-authoritative and non-replacing even when shown with highlighting or grouped review context.

## No-Implementation Boundary

This specification does not authorize UI components, layouts, routes, navigation, panels, interaction mechanics, tooling logic, validator logic, script logic, schema work, or implementation flow.

## No-Runtime-Coupling Boundary

This specification does not authorize runtime-facing ingestion, runtime control, runtime mutation, runtime approval, runtime status generation, or review-surface coupling to runtime behavior.

## No-Mutation Boundary

This specification does not authorize fixture mutation, canonical rewrite, canonical replacement, inferred-to-observed promotion, downstream correction authority, or silent uncertainty repair.

## No-Control Boundary

This specification does not authorize control-surface behavior, operator-console behavior, prioritization engines, scoring systems, ranking systems, urgency models, or execution gating.

## Support Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Review-surface behavior definitions | Supported | Limited to the approved behavior vocabulary |
| Output-family presentation rules | Supported | Limited to the five frozen output families |
| Boundary-preserving examples | Supported | Illustrative only |
| UI implementation | Not supported | Deferred |
| Tooling implementation | Not supported | Deferred |
| Scoring or prioritization logic | Not supported | Forbidden in this phase |
| Packaging, manifest, or mirror design | Not supported | Forbidden in this phase |
| Runtime coupling or control behavior | Not supported | Forbidden in this phase |

## Explicit Stop Conditions

Stop if any drafting or later pass:

- widens scope beyond the four canonical fixtures
- widens scope beyond the four approved consumer profiles
- widens scope beyond the five frozen output families
- implies runtime authority
- implies mutation authority
- implies execution authority
- implies canonical replacement
- turns grouping into packaging, archive, manifest, or transport semantics
- turns highlighting into scoring, ranking, urgency, or prioritization logic
- turns a review surface into control, validator, or runtime interface behavior
- treats packet content as runtime world state
- treats Water Mill design-asset-derived context as observed built authority without accepted evidence
- weakens single-dossier continuity
- makes canonical-path visibility optional enough to weaken source-of-truth discipline
