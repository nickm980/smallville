# Review Surface Drill Stop Rules v1

## Purpose

This note defines the stop-and-report rules for bounded review-surface consumption drills over already-presented diagnostic outputs.

## Core Hard-Stop Rule

A drill must stop on a `stop-report` or any surfaced hard stop condition and may not continue as though the stop were advisory only.

## Output-Family Stop Rules

### `summary`

- Stop if a drill treats the presented `summary` as substitute source text.
- Stop if a drill softens a surfaced hard stop affecting the presented `summary`.
- Stop if a drill weakens packet/world-state separation, Water Mill boundaries, or single-dossier continuity while orienting review around the `summary`.

### `drift-note`

- Stop if a drill turns surfaced drift into correction authority, prioritization logic, or execution sequencing.
- Stop if a drill weakens provenance, Water Mill boundary discipline, packet/world-state separation, or single-dossier continuity while pausing on drift.
- Stop if a drill uses preserved drift context to imply canonical replacement or hidden workflow control.

### `stop-report`

- Stop immediately when a drill encounters a presented `stop-report`.
- Stop if any drill wording reframes the `stop-report` as advisory only.
- Stop if any drill wording allows pause-on-drift semantics to stand in for hard-stop semantics.

### `diagnostics-bundle`

- Stop if a drill turns preserved grouped review context into packaging, archive, manifest, file-format, or transport semantics.
- Stop if grouped context hides or softens a hard stop condition.
- Stop if grouped context weakens the distinction between source-of-truth, presented output, and grouped descriptive context.

### `export-summary`

- Stop if a drill treats the presented `export-summary` as canonical replacement, runtime-facing input, or execution guidance.
- Stop if a drill weakens provenance, uncertainty, Water Mill boundary discipline, packet/world-state separation, or single-dossier continuity while orienting review around the `export-summary`.
- Stop if a drill uses handoff-oriented context to imply new authority.

## Cross-Family Stop Conditions

Stop and report if any drill:

- hides canonical-path visibility
- hides or softens a surfaced hard stop condition
- weakens provenance or boundary-note visibility when already presented
- treats Water Mill design-asset-derived context as observed built authority without accepted evidence
- treats packet content as runtime world state
- introduces a second dossier
- implies UI mechanics, runtime workflow, automation, validator behavior, control behavior, or execution sequencing
- implies scoring, ranking, urgency, prioritization, or hidden authority selection
- implies canonical replacement, mutation authority, runtime authority, or execution authority

## Human Review Rule

When a hard stop condition is surfaced, the drill may not continue on the flagged output or flagged subject until human review resolves the stop condition.

## Boundary Preservation Rules

All drill stop behavior must preserve:

- canonical paths as source of truth
- derived outputs as non-authoritative and non-replacing
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, never runtime world state
- single-dossier continuity

## Explicit Stop Conditions

Stop if this note or a later pass:

- weakens upstream stop overlays
- treats a drill as an execution sequence
- treats a drill as automation or control behavior
- turns pause behavior into substitute stop behavior for `stop-report`
- drifts into UI mechanics, runtime hooks, validator logic, tooling logic, packaging, schemas, or executable drill states
