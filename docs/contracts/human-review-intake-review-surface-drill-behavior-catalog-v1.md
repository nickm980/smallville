# Human Review Intake Review Surface Drill Behavior Catalog v1

## Purpose

This catalog defines the approved descriptive drill behavior vocabulary for bounded review walkthroughs over already-presented consumed human-review intake artifacts. It is descriptive only and may not be read as tooling logic, validator logic, runtime behavior, workflow logic, routing logic, dashboard control, or UI design.

## `read-presented-intake`

- Meaning:
  - read already-presented consumed human-review intake artifacts and preserved review-surface context only
- Allowed use:
  - orient human review around already-presented consumed intake artifacts without changing authority
- Forbidden use:
  - derive a new intake family, create approval handling, or create execution handling
- Boundary note:
  - reading remains pre-decision only and may not become review-decision handling, workflow handling, routing handling, or control handling

## `preserve-presented-order`

- Meaning:
  - preserve already-presented order while walking through consumed human-review intake artifacts for review only
- Allowed use:
  - retain already-presented order as bounded review context
- Forbidden use:
  - turn preserved order into navigation design, workflow sequencing, or routing logic
- Boundary note:
  - order remains descriptive only and may not create process-driving behavior

## `preserve-grouped-review-context`

- Meaning:
  - preserve already-presented grouped review context during the drill
- Allowed use:
  - keep together review context that the upstream review surface already grouped for bounded human review
- Forbidden use:
  - turn grouped context into packaging semantics, dashboard control, routing logic, or new artifact families
- Boundary note:
  - grouped context remains descriptive only and may not weaken review-decision separation or canonical source-of-truth discipline

## `pause-on-drift`

- Meaning:
  - pause review attention on already-presented drift-oriented context without changing authority
- Allowed use:
  - preserve drift-oriented review attention around a presented consumed intake artifact while keeping all boundaries explicit
- Forbidden use:
  - create correction authority, queue semantics, backlog semantics, prioritization logic, or execution handling
- Boundary note:
  - pause remains a review aid only and may not stand in for hard-stop behavior

## `stop-on-hard-condition`

- Meaning:
  - stop drill continuation when a presented consumed `review-intake-stop-note` or any surfaced unresolved hard stop is in scope
- Allowed use:
  - halt the drill on the flagged subject while preserving halt-aware review context
- Forbidden use:
  - soften the hard stop into advisory-only wording, resolve it implicitly, or continue as though it were cleared
- Boundary note:
  - stop behavior remains mandatory when unresolved hard-stop meaning is surfaced

## `preserve-canonical-path-visibility`

- Meaning:
  - keep canonical-path visibility explicit wherever the drill references a presented consumed intake artifact
- Allowed use:
  - show canonical-path references as source-of-truth visibility for the underlying fixture
- Forbidden use:
  - hide the canonical path or treat surfaced paths as replacement mirrors or mutable targets
- Boundary note:
  - canonical-path visibility is required wherever a presented consumed intake artifact is referenced in the drill

## `preserve-provenance-note-visibility`

- Meaning:
  - keep already-presented provenance notes visible during the drill
- Allowed use:
  - preserve source-lineage context without changing authority
- Forbidden use:
  - strengthen provenance into approval text, replacement authority, or execution guidance
- Boundary note:
  - provenance remains preserved review context only

## `preserve-boundary-note-visibility`

- Meaning:
  - keep already-presented boundary notes visible during the drill
- Allowed use:
  - preserve Water Mill, packet, dossier, and review-decision separation context during the walkthrough
- Forbidden use:
  - restate Water Mill as observed built authority, restate packet content as runtime world state, or weaken single-dossier continuity
- Boundary note:
  - boundary-note visibility remains preserved review context only and may not become new authority

## `preserve-severity-visibility`

- Meaning:
  - keep already-presented severity context visible during the drill
- Allowed use:
  - preserve intake-local severity context as bounded review orientation only
- Forbidden use:
  - turn severity into scoring, ranking, prioritization, urgency modeling, or execution guidance
- Boundary note:
  - severity remains preserved review context only and may not drive workflow, routing, or control behavior
