# Diagnostic Output Consumption Examples v1

## Purpose

These examples illustrate bounded read-only consumption of already-derived diagnostic outputs. They are illustrative only. They are not tooling design, validator design, runtime behavior, packaging design, or canonical replacement behavior.

## Valid `summary` Consumption

- Consumer profile: `Fixture Reader`
- Consumed output family: `summary`
- Canonical fixture in view: `docs/examples/promoted-derived-packet-v1.md`
- Consumed meaning: the `summary` is used as a review aid only to orient a human reader to preserved packet boundaries before the canonical packet text is read.
- Required preservation:
  - canonical path remains source of truth
  - the packet remains interpretive artifact only, not runtime world state
  - Water Mill references remain design-asset-derived context only
  - no second dossier is introduced
- Forbidden interpretation:
  - treating the `summary` as substitute source text
  - treating the `summary` as runtime guidance

## Valid `drift-note` Consumption

- Consumer profile: `Fixture Comparator`
- Consumed output family: `drift-note`
- Canonical fixture in view: `docs/examples/promoted-archive-record-v1.md`
- Consumed meaning: the `drift-note` is used to focus review attention on weakened provenance wording around Water Mill design-asset handling.
- Required preservation:
  - the `drift-note` remains non-authoritative
  - review attention increases, but no correction authority is created
  - Water Mill remains design-asset-derived context only
  - canonical archive-record text remains source of truth
- Forbidden interpretation:
  - treating the `drift-note` as permission to rewrite source text
  - treating the `drift-note` as approved correction

## Valid `stop-report` Consumption

- Consumer profile: `Fixture Diagnostics Consumer`
- Consumed output family: `stop-report`
- Canonical fixture in view: `docs/examples/promoted-review-decision-v1.md`
- Consumed meaning: the `stop-report` flags that a downstream reading has started to treat review approval as execution approval.
- Required preservation:
  - downstream consumption of the flagged output or subject halts
  - human review is required before any further downstream use
  - review decision text remains bounded by approval language only
- Forbidden interpretation:
  - treating the `stop-report` as advisory only
  - continuing downstream consumption as though no stop condition exists

## Valid `diagnostics-bundle` Consumption

- Consumer profile: `Fixture Reader`
- Consumed output family: `diagnostics-bundle`
- Canonical fixture in view: `docs/examples/promoted-agent-brief-v1.md`
- Consumed meaning: the grouped descriptive output is read as audit context combining a `summary` and `drift-note` for bounded review.
- Required preservation:
  - grouping does not change authority level
  - grouping does not create package, archive, manifest, file-format, or transport semantics
  - canonical brief text remains source of truth
- Forbidden interpretation:
  - treating the grouped output as an authoritative mirror set
  - treating grouping as a transport or packaging specification

## Valid `export-summary` Consumption

- Consumer profile: `Fixture Export Consumer`
- Consumed output family: `export-summary`
- Canonical fixture in view: `docs/examples/promoted-archive-record-v1.md`
- Consumed meaning: the `export-summary` is handed off as a downstream review aid that preserves provenance and boundary language without replacing canonical source text.
- Required preservation:
  - export remains non-authoritative and non-replacing
  - Water Mill remains design-asset-derived context only
  - handoff does not imply runtime-facing ingestion
  - single-dossier continuity remains explicit
- Forbidden interpretation:
  - treating the `export-summary` as canonical replacement
  - treating the `export-summary` as execution or runtime guidance

## Boundary-Edge But Still Acceptable Example

- Consumer profile: `Fixture Comparator`
- Consumed output family: `diagnostics-bundle`
- Canonical fixture in view: `docs/examples/promoted-derived-packet-v1.md`
- Situation: the grouped output contains a `summary` and `drift-note` describing packet/world-state separation risk.
- Why still acceptable:
  - the bundle is read as grouped descriptive context only
  - the packet still remains interpretive artifact only
  - the comparator uses the grouped output to focus canonical review, not to replace canonical packet text
- Immediate requirement:
  - if the grouped reading reveals a triggered stop-rule condition, downstream consumption must stop

## Clearly Non-Conformant Examples

### Non-conformant `summary` Consumption

- Consumer profile: `Fixture Reader`
- Consumed output family: `summary`
- Failure: the reader treats the `summary` as the new working source because it is shorter than the canonical packet text.
- Why non-conformant:
  - creates canonical replacement behavior
  - weakens packet/world-state separation
  - elevates a derived output above canonical source text

### Non-conformant `drift-note` Consumption

- Consumer profile: `Fixture Comparator`
- Consumed output family: `drift-note`
- Failure: the comparator uses the `drift-note` as permission to rewrite the canonical archive record and remove uncertainty language.
- Why non-conformant:
  - creates mutation and rewrite authority
  - weakens provenance and uncertainty discipline
  - breaks canonical source-of-truth rules

### Non-conformant `stop-report` Consumption

- Consumer profile: `Fixture Export Consumer`
- Consumed output family: `stop-report`
- Failure: the exporter reads the stop condition but still hands off downstream text as though the flagged subject were cleared.
- Why non-conformant:
  - treats a hard stop as advisory only
  - continues downstream consumption after a triggered stop condition
  - risks execution-authority drift

### Non-conformant `diagnostics-bundle` Consumption

- Consumer profile: `Fixture Reader`
- Consumed output family: `diagnostics-bundle`
- Failure: the grouped output is described as the new diagnostic package for transport into later systems.
- Why non-conformant:
  - creates package semantics
  - drifts into design territory that is out of scope
  - risks turning grouped descriptive output into authoritative transport behavior

### Non-conformant `export-summary` Consumption

- Consumer profile: `Fixture Export Consumer`
- Consumed output family: `export-summary`
- Failure: the exported text is treated as settled built authority for Water Mill structures and is merged with another dossier for convenience.
- Why non-conformant:
  - inflates Water Mill beyond design-asset-derived context
  - introduces second-dossier expansion
  - creates canonical-replacement pressure
