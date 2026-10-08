# Human Review Intake Review Surface Drill Stop Rules v1

## Purpose

This note defines the hard-stop preservation rules for bounded review-surface drills over already-presented consumed human-review intake artifacts.

## Core Hard-Stop Rule

A human-review-intake review-surface drill must stop on a presented consumed `review-intake-stop-note` or any surfaced unresolved hard stop and may not continue as though the hard stop were advisory only.

## Intake-Family Stop Rules

### `review-intake-summary`

- Stop if a drill treats the presented consumed `review-intake-summary` as completed review, approval handling, or substitute approval text.
- Stop if a drill softens a surfaced unresolved hard stop affecting the presented consumed `review-intake-summary`.
- Stop if a drill weakens canonical-path visibility, Water Mill boundaries, packet/world-state separation, or single-dossier continuity while orienting review around the presented consumed `review-intake-summary`.

### `review-intake-pause-note`

- Stop if a drill turns surfaced drift-pause context into correction authority, queue semantics, backlog semantics, workflow handling, routing handling, or execution handling.
- Stop if a drill weakens provenance visibility, Water Mill boundary discipline, packet/world-state separation, or single-dossier continuity while pausing on drift.
- Stop if a drill uses preserved pause context to imply canonical replacement or hidden workflow control.

### `review-intake-stop-note`

- Stop immediately when a drill encounters a presented consumed `review-intake-stop-note`.
- Stop if any drill wording reframes the unresolved hard stop as advisory only.
- Stop if any drill wording allows `pause-on-drift` semantics to stand in for hard-stop semantics.
- Stop if downstream drill context continues on the flagged subject as though the hard stop were resolved.

### `review-intake-handoff-note`

- Stop if a drill treats the presented consumed `review-intake-handoff-note` as execution handoff, routing logic, assignment semantics, transfer semantics, downstream clearance, or review-decision proxy.
- Stop if handoff-oriented context weakens review-decision separation, Water Mill boundary discipline, packet/world-state separation, or single-dossier continuity.
- Stop if handoff-oriented context is used to imply new authority.

## Cross-Family Stop Conditions

Stop and report if any drill:

- hides canonical-path visibility
- hides or softens a surfaced unresolved hard stop
- weakens provenance-note visibility or boundary-note visibility when already presented
- treats a presented consumed intake artifact as review-decision handling, approval handling, execution handling, or control handling
- creates workflow, routing, queue, backlog, assignment, or transfer semantics
- treats Water Mill design-asset-derived context as observed built authority without accepted evidence
- treats packet content as runtime world state
- introduces a second dossier
- implies tooling, validators, scripts, schemas, workflow engines, routing logic, runtime behavior, dashboard control, scoring, ranking, prioritization, or UI design
- implies canonical replacement, mutation authority, runtime authority, or execution authority

## Human Review Rule

When a presented consumed `review-intake-stop-note` or any surfaced unresolved hard stop is in scope, the drill may continue only for the purpose of preserving halt-aware human review context and may not continue on the flagged subject as though the hard stop were resolved.

## Boundary Preservation Rules

All drill stop behavior must preserve:

- canonical paths as source of truth
- presented consumed intake artifacts as derived only, non-authoritative, and non-replacing
- Water Mill as design-asset-derived context only
- packet content as interpretive artifact only, never runtime world state
- single-dossier continuity
- explicit pre-decision versus review-decision separation

## Explicit Stop Conditions

Stop if this note or a later pass:

- weakens unresolved hard-stop semantics
- treats a drill as workflow handling, routing handling, execution handling, or control behavior
- treats a drill as review-decision handling or substitute approval text
- turns pause behavior into substitute stop behavior for `review-intake-stop-note`
- drifts into tooling, schemas, runtime behavior, workflow engines, routing logic, queue semantics, backlog semantics, dashboard control, scoring, ranking, prioritization, or UI design
