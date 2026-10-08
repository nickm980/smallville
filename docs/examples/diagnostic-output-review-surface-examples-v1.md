# Diagnostic Output Review Surface Examples v1

## Purpose

These examples illustrate bounded review-surface presentation of already-consumed diagnostic outputs. They are illustrative only. They are not UI implementation, layout design, interaction design, validator behavior, runtime behavior, control behavior, or canonical replacement behavior.

## Valid `summary` Presentation

- Presented output family: `summary`
- Applied review-surface behaviors:
  - `show-canonical-path-reference`
  - `show-provenance-note`
  - `show-boundary-notes`
  - `highlight-severity`
- Why conformant:
  - the canonical path remains visible as source-of-truth reference
  - provenance and packet boundary context remain visible without creating new authority
  - severity is shown only as output-local boundary seriousness
- What remains forbidden:
  - treating the presented `summary` as substitute source text
  - treating severity emphasis as action priority

## Valid `drift-note` Presentation

- Presented output family: `drift-note`
- Applied review-surface behaviors:
  - `group-for-review`
  - `sort-by-existing-field`
  - `show-canonical-path-reference`
  - `show-provenance-note`
  - `show-boundary-notes`
- Why conformant:
  - the `drift-note` is arranged for bounded review context only
  - grouping does not create package semantics
  - provenance and Water Mill boundary context remain explicit
- What remains forbidden:
  - turning the `drift-note` into correction authority
  - hiding canonical-path visibility during grouping or sorting

## Valid `stop-report` Presentation

- Presented output family: `stop-report`
- Applied review-surface behaviors:
  - `highlight-stop-condition`
  - `show-canonical-path-reference`
  - `show-boundary-notes`
- Why conformant:
  - the halt condition is made more visible without being softened
  - the canonical path remains visible
  - approval-boundary meaning remains preserved
- What remains forbidden:
  - downgrading the stop condition into advisory-only wording
  - presenting the stop condition as execution approval

## Valid `diagnostics-bundle` Presentation

- Presented output family: `diagnostics-bundle`
- Applied review-surface behaviors:
  - `group-for-review`
  - `filter-by-existing-field`
  - `show-canonical-path-reference`
  - `show-provenance-note`
  - `show-boundary-notes`
- Why conformant:
  - the grouped descriptive output remains grouped review context only
  - filtering uses existing preserved fields only
  - the presented bundle does not gain archive, manifest, file-format, or transport semantics
- What remains forbidden:
  - treating the grouped presentation as packaging behavior
  - treating the bundle as an authoritative mirror set

## Valid `export-summary` Presentation

- Presented output family: `export-summary`
- Applied review-surface behaviors:
  - `show-canonical-path-reference`
  - `show-provenance-note`
  - `show-boundary-notes`
  - `highlight-severity`
- Why conformant:
  - the handoff-oriented output remains visibly tied to canonical source text
  - provenance and boundary context remain visible
  - severity emphasis remains non-prioritized and non-executing
- What remains forbidden:
  - treating the presented `export-summary` as canonical replacement
  - treating the presented `export-summary` as runtime-facing input

## Boundary-Edge But Still Acceptable Example

- Presented output family: `diagnostics-bundle`
- Applied review-surface behaviors:
  - `group-for-review`
  - `sort-by-existing-field`
  - `highlight-stop-condition`
  - `show-canonical-path-reference`
- Situation:
  - the grouped presentation includes a consumed `stop-report` and a consumed `drift-note` about packet/world-state separation
- Why still acceptable:
  - grouping remains descriptive only
  - the stop condition is made more visible, not softened
  - canonical-path visibility remains explicit for the underlying fixture
  - packet remains interpretive artifact only and not runtime world state
- Immediate requirement:
  - the halt condition must remain visible throughout the presented review context

## Clearly Non-Conformant Examples

### Non-conformant Highlighting

- Presented output family: `summary`
- Failure:
  - the review surface labels the highest `severity` items as “top priority actions”
- Why non-conformant:
  - turns highlighting into prioritization logic
  - implies execution direction from a derived output
  - exceeds bounded review presentation

### Non-conformant Grouping

- Presented output family: `diagnostics-bundle`
- Failure:
  - the grouped presentation is described as the new review package for later system handoff
- Why non-conformant:
  - turns grouping into packaging semantics
  - drifts into out-of-scope transport behavior
  - pressures the grouped output toward replacement authority

### Non-conformant Path Handling

- Presented output family: `export-summary`
- Failure:
  - canonical path references are hidden because the surfaced review text is considered sufficient on its own
- Why non-conformant:
  - weakens canonical source-of-truth visibility
  - increases replacement pressure on derived output
  - violates the requirement that canonical-path visibility remain explicit wherever an output is presented

### Non-conformant Stop Presentation

- Presented output family: `stop-report`
- Failure:
  - the stop condition is shown as an optional caution note while the presented review context continues as normal
- Why non-conformant:
  - softens a halt condition into advisory-only wording
  - weakens upstream stop-rule behavior
  - risks execution-authority drift

### Non-conformant Boundary Presentation

- Presented output family: `drift-note`
- Failure:
  - the presented boundary notes restate Water Mill material as settled built fact and refer to packet language as operational state
- Why non-conformant:
  - inflates Water Mill beyond design-asset-derived context
  - breaks packet/world-state separation
  - violates preserved review-surface boundary rules
