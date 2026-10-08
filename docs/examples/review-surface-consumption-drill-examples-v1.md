# Review Surface Consumption Drill Examples v1

## Purpose

These examples illustrate bounded review-surface consumption drills over already-presented diagnostic outputs. They are illustrative only. They are not UI implementation, runtime workflow, automation, validator behavior, control behavior, execution sequencing, or canonical replacement behavior.

## Valid `summary` Drill

- Presented output family: `summary`
- Applied drill behaviors:
  - `read-presented-output`
  - `preserve-presented-order`
  - `preserve-canonical-path-visibility`
  - `preserve-provenance-note-visibility`
  - `preserve-boundary-note-visibility`
  - `preserve-severity-visibility`
- Why conformant:
  - the drill reads already-presented `summary` context only
  - canonical-path visibility remains explicit
  - provenance, packet boundary, and severity context remain visible without creating new authority
- What remains forbidden:
  - treating the drill as a navigation sequence
  - treating the `summary` as substitute source text

## Valid Drift-Pause Drill

- Presented output family: `drift-note`
- Applied drill behaviors:
  - `read-presented-output`
  - `pause-on-drift`
  - `preserve-canonical-path-visibility`
  - `preserve-provenance-note-visibility`
  - `preserve-boundary-note-visibility`
- Why conformant:
  - the drill pauses review attention on already-presented drift context only
  - the pause does not create correction authority or prioritization logic
  - Water Mill design-asset-derived context remains explicit
- What remains forbidden:
  - treating the pause as correction approval
  - treating the pause as execution sequencing

## Valid Mandatory-Stop Drill

- Presented output family: `stop-report`
- Applied drill behaviors:
  - `read-presented-output`
  - `stop-on-hard-condition`
  - `preserve-canonical-path-visibility`
  - `preserve-boundary-note-visibility`
- Why conformant:
  - the drill recognizes the surfaced hard stop and halts
  - the canonical path remains visible
  - approval-boundary meaning remains preserved
- What remains forbidden:
  - reframing the hard stop as advisory only
  - continuing the drill on the flagged output or subject

## Valid Grouped-Context Drill

- Presented output family: `diagnostics-bundle`
- Applied drill behaviors:
  - `read-presented-output`
  - `preserve-grouped-review-context`
  - `preserve-presented-order`
  - `preserve-canonical-path-visibility`
  - `preserve-provenance-note-visibility`
  - `preserve-boundary-note-visibility`
- Why conformant:
  - grouped descriptive context remains grouped review context only
  - preserved order remains review orientation only
  - the drill does not create package or navigation semantics
- What remains forbidden:
  - treating grouped context as packaging behavior
  - turning preserved order into workflow sequence

## Valid `export-summary` Drill

- Presented output family: `export-summary`
- Applied drill behaviors:
  - `read-presented-output`
  - `preserve-presented-order`
  - `preserve-canonical-path-visibility`
  - `preserve-provenance-note-visibility`
  - `preserve-boundary-note-visibility`
  - `preserve-severity-visibility`
- Why conformant:
  - the drill reads already-presented handoff-oriented text only
  - canonical-path visibility remains explicit
  - Water Mill and dossier boundary context remain preserved
- What remains forbidden:
  - treating the `export-summary` as canonical replacement
  - treating the drill as handoff execution behavior

## Boundary-Edge But Still Acceptable Example

- Presented output family: `diagnostics-bundle`
- Applied drill behaviors:
  - `read-presented-output`
  - `preserve-grouped-review-context`
  - `preserve-presented-order`
  - `preserve-canonical-path-visibility`
  - `stop-on-hard-condition`
- Situation:
  - the grouped review context includes a surfaced `drift-note` and a surfaced hard stop related to packet/world-state confusion
- Why still acceptable:
  - the drill preserves grouped context without inventing package semantics
  - preserved order remains descriptive only
  - the hard stop remains visible and halts drill continuation on the flagged subject
  - packet remains interpretive artifact only and not runtime world state

## Clearly Non-Conformant Examples

### Non-conformant Drill Flow

- Presented output family: `summary`
- Failure:
  - the drill is described as the step-by-step UI route a reviewer should click through next
- Why non-conformant:
  - turns a drill into UI flow
  - introduces navigation design
  - exceeds bounded descriptive governance

### Non-conformant Drift Pause

- Presented output family: `drift-note`
- Failure:
  - the drill says the highest-risk drift should be handled first because it is the most urgent item
- Why non-conformant:
  - turns pause behavior into prioritization logic
  - implies urgency ranking
  - exceeds bounded review orientation

### Non-conformant Hard Stop

- Presented output family: `stop-report`
- Failure:
  - the drill notes the stop condition but continues the walkthrough as though the stop were optional
- Why non-conformant:
  - softens a hard stop into advisory-only wording
  - weakens upstream halt semantics
  - risks execution-sequence drift

### Non-conformant Boundary Handling

- Presented output family: `export-summary`
- Failure:
  - the drill restates Water Mill material as settled built fact and treats packet language as operational state
- Why non-conformant:
  - inflates Water Mill beyond design-asset-derived context
  - breaks packet/world-state separation
  - violates preserved drill boundary rules

### Non-conformant Dossier Handling

- Presented output family: `diagnostics-bundle`
- Failure:
  - the drill pulls in another dossier to help “complete” the grouped review context
- Why non-conformant:
  - introduces second-dossier expansion
  - weakens single-dossier continuity
  - exceeds the frozen scope of the drill layer
