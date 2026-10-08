# Human Review Intake Review Surface Drill Outcome Fields v1

## Purpose

This note defines the approved descriptive field vocabulary for human-review-intake review-surface drill outcomes. It is vocabulary only. It does not define schema requiredness, serialization, validation logic, data types, or file formats.

## Vocabulary Boundary

All fields in this layer remain subordinate to the frozen canonical fixture, human-review-intake, review-surface, and drill layers.

All field meanings remain pre-decision only and may not convert a drill outcome into review-decision handling, approval handling, execution handling, workflow handling, routing handling, or control handling.

This vocabulary intentionally avoids generic `status` and generic `state` fields in order to avoid workflow-state inflation.

The fields `runtime_state_forbidden` and `workflow_state_forbidden` are explicit prohibition fields only. They do not authorize or define a permitted state model.

## Field Vocabulary

### `outcome_type`

Meaning: identifies one approved outcome family only.

Boundary note: must remain limited to `review-intake-drill-summary`, `review-intake-drill-pause-note`, `review-intake-drill-stop-outcome`, or `review-intake-drill-handoff-note`.

### `consumer_profile`

Meaning: identifies the approved frozen consumer profile in view when the bounded drill outcome was derived.

Boundary note: may not introduce a new consumer profile or imply broader authority than the named frozen profile.

### `source_intake_family`

Meaning: identifies the already-presented consumed human-review intake family from which the outcome preserves context.

Boundary note: may refer only to `review-intake-summary`, `review-intake-pause-note`, `review-intake-stop-note`, or `review-intake-handoff-note`.

### `source_intake_reference`

Meaning: records a reference to an already-presented consumed human-review intake artifact only.

Boundary note: must refer only to an already-presented consumed intake reference and may not function as replacement source authority.

### `fixture_id`

Meaning: identifies the in-scope canonical fixture subject tied to the preserved outcome context.

Boundary note: remains descriptive only and does not replace canonical fixture text.

### `fixture_type`

Meaning: names the in-scope canonical fixture type tied to the preserved outcome context.

Boundary note: may not widen fixture scope beyond the frozen canonical set.

### `canonical_path`

Meaning: preserves the canonical path for the underlying fixture.

Boundary note: Canonical fixture files at canonical paths remain the source of truth wherever a human-review-intake review-surface drill outcome is recorded, read, or handed forward.

### `drill_behavior_context`

Meaning: describes which approved frozen drill behaviors were preserved in the bounded drill context.

Boundary note: must refer only to approved frozen drill behaviors and may not imply workflow sequencing, UI mechanics, or new drill behaviors.

### `hard_stop_preserved`

Meaning: preserves descriptive note-level context about whether unresolved hard-stop meaning is present in the outcome.

Boundary note: remains a descriptive preservation indicator only and may not imply workflow handling, routing handling, queue semantics, stop resolution, rerouting, or downstream clearance.

### `drift_pause_preserved`

Meaning: preserves descriptive note-level context about whether drift-pause meaning is present in the outcome.

Boundary note: remains a descriptive preservation indicator only and may not imply workflow handling, routing handling, queue semantics, deferred work, or backlog semantics.

### `provenance_note`

Meaning: preserves provenance-aware review context tied to the underlying canonical fixture and source intake artifact.

Boundary note: may not strengthen derived wording into new authority.

### `boundary_note`

Meaning: preserves general boundary context carried forward from the bounded drill.

Boundary note: may not weaken unresolved hard-stop, approval-boundary, or source-of-truth rules.

### `packet_boundary_note`

Meaning: preserves packet boundary context where packet-related interpretation was surfaced.

Boundary note: Packet content remains interpretive artifact only within human-review-intake review-surface drill outcomes and may not be restated as runtime world state.

### `water_mill_boundary_note`

Meaning: preserves Water Mill boundary context where relevant to the preserved outcome.

Boundary note: Water Mill remains design-asset-derived context only within human-review-intake review-surface drill outcomes and may not be restated as observed built authority without accepted evidence.

### `dossier_boundary_note`

Meaning: preserves single-dossier continuity context where relevant to the preserved outcome.

Boundary note: may not weaken single-dossier continuity or introduce a second dossier.

### `severity_note`

Meaning: preserves severity as review-only context only.

Boundary note: may not become scoring, ranking, urgency, prioritization, or execution guidance.

### `handoff_note`

Meaning: preserves human handoff wording where a bounded drill outcome supports handoff.

Boundary note: may support human handoff only and may not become execution handoff, routing logic, assignment semantics, transfer semantics, or downstream clearance.

### `human_review_note`

Meaning: preserves review-only notes for human interpretation of the outcome.

Boundary note: remains pre-decision only and may not become approval language, decision language, or execution language.

### `review_decision_replacement_forbidden`

Meaning: explicitly preserves that a human-review-intake review-surface drill outcome may not replace, extend, restate, simulate, or paraphrase substitute approval text for a canonical review decision.

Boundary note: a human-review-intake review-surface drill outcome may reference a promoted review decision fixture as subject matter only and may not replace, extend, restate, simulate, or paraphrase substitute approval text for a canonical review decision.

### `runtime_state_forbidden`

Meaning: explicitly preserves that a human-review-intake review-surface drill outcome may not become runtime state or runtime-facing authority.

Boundary note: prohibition only, not a permitted state model.

### `workflow_state_forbidden`

Meaning: explicitly preserves that a human-review-intake review-surface drill outcome may not become workflow state, routing state, or process-tracking state.

Boundary note: prohibition only, not a permitted workflow model.

### `execution_authority_forbidden`

Meaning: explicitly preserves that a human-review-intake review-surface drill outcome may not become execution authority or downstream clearance.

Boundary note: prohibition only, not a deferred execution model.

## Cross-Field Preservation Rules

All field use must preserve:

- canonical paths as source of truth
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, never runtime world state
- single-dossier continuity
- pre-decision separation from canonical review decisions
- no runtime authority
- no mutation authority
- no execution authority
- no approval inflation
- no workflow-state inflation
- no canonical replacement

## Explicit Stop Conditions

Stop if field vocabulary:

- introduces generic `status`
- introduces generic `state`
- introduces queue-like, routing-like, or workflow-like wording
- weakens source-of-truth rules
- weakens unresolved hard-stop semantics
- weakens Water Mill boundary discipline
- weakens packet/world-state separation
- weakens single-dossier continuity
- weakens pre-decision versus review-decision separation
- implies runtime authority, mutation authority, execution authority, approval inflation, canonical replacement, or workflow-state inflation
