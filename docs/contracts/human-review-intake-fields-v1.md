# Human Review Intake Fields v1

## Purpose

This note defines the approved descriptive field vocabulary for human-review intake artifacts. It is vocabulary only. It does not define schema requiredness, serialization, validation logic, data types, or file formats.

## Vocabulary Boundary

All fields in this layer remain subordinate to the frozen canonical fixture, diagnostic-output, review-surface, drill, drill-outcome, and drill-outcome-consumption layers.

All field meanings remain pre-decision only and may not convert human-review intake into a review decision, approval state, execution state, workflow state, or control state.

This vocabulary intentionally avoids generic `status` and generic `state` fields in order to avoid workflow-state inflation.

The fields `runtime_state_forbidden` and `workflow_state_forbidden` are explicit prohibition fields only. They do not authorize or define a permitted state model.

## Field Vocabulary

### `intake_type`

Meaning: identifies one approved intake family only.

Boundary note: must remain limited to `review-intake-summary`, `review-intake-pause-note`, `review-intake-stop-note`, or `review-intake-handoff-note`.

### `consumer_profile`

Meaning: identifies the approved frozen consumer profile in view when the human-review intake artifact was derived.

Boundary note: may not introduce a new consumer profile or imply broader authority than the named frozen profile.

### `source_outcome_family`

Meaning: identifies the already-consumed frozen drill outcome family from which intake preserves context.

Boundary note: may refer only to `drill-summary`, `drill-pause-note`, `drill-stop-outcome`, or `drill-handoff-note`.

### `source_outcome_reference`

Meaning: records a reference to an already-consumed drill outcome only.

Boundary note: must refer only to an already-consumed drill outcome reference and may not function as replacement source authority.

### `fixture_id`

Meaning: identifies the in-scope canonical fixture subject tied to the preserved intake context.

Boundary note: remains descriptive only and does not replace canonical fixture text.

### `fixture_type`

Meaning: names the in-scope canonical fixture type tied to the preserved intake context.

Boundary note: may not widen fixture scope beyond the frozen canonical set.

### `canonical_path`

Meaning: preserves the canonical path for the underlying fixture.

Boundary note: Canonical fixture files at canonical paths remain the source of truth wherever human-review intake artifacts are recorded, read, or handed forward for human attention.

### `intake_context`

Meaning: describes the bounded intake context preserved from already-consumed drill outcomes.

Boundary note: remains descriptive only and may not imply routing logic, queue semantics, backlog semantics, assignment semantics, transfer semantics, or execution-ready semantics.

### `hard_stop_preserved`

Meaning: preserves descriptive note-level context about whether unresolved hard-stop meaning is present in the intake artifact.

Boundary note: remains a descriptive preservation indicator only and may not imply workflow state, action state, stop resolution, rerouting, or clearance.

### `drift_pause_preserved`

Meaning: preserves descriptive note-level context about whether drift-pause meaning is present in the intake artifact.

Boundary note: remains a descriptive preservation indicator only and may not imply workflow state, deferred work, queue semantics, or backlog semantics.

### `provenance_note`

Meaning: preserves provenance-aware human-review context tied to the underlying canonical fixture and consumed outcome.

Boundary note: may not strengthen derived wording into new authority.

### `boundary_note`

Meaning: preserves general boundary context carried forward from the already-consumed outcome.

Boundary note: may not weaken hard-stop, approval-boundary, or source-of-truth rules.

### `packet_boundary_note`

Meaning: preserves packet boundary context where packet-related interpretation was surfaced.

Boundary note: Packet content remains interpretive artifact only within human-review intake artifacts and may not be restated as runtime world state.

### `water_mill_boundary_note`

Meaning: preserves Water Mill boundary context where relevant to the intake artifact.

Boundary note: Water Mill remains design-asset-derived context only within human-review intake artifacts and may not be restated as observed built authority without accepted evidence.

### `dossier_boundary_note`

Meaning: preserves single-dossier continuity context where relevant to the intake artifact.

Boundary note: may not weaken single-dossier continuity or introduce a second dossier.

### `severity_note`

Meaning: preserves severity as review-only context only.

Boundary note: may not become scoring, ranking, urgency, prioritization, or execution guidance.

### `handoff_note`

Meaning: preserves human-attention handoff wording where an intake artifact supports bounded human attention.

Boundary note: may support human attention only and may not become execution handoff, workflow transition, or review-decision proxy.

### `human_attention_note`

Meaning: preserves review-only notes for human attention to the intake artifact.

Boundary note: remains pre-decision only and may not become approval language, decision language, or execution language.

### `review_decision_replacement_forbidden`

Meaning: explicitly preserves that a human-review intake artifact may not replace, extend, restate, simulate, or paraphrase substitute approval text for a canonical review decision.

Boundary note: a human-review intake artifact may reference a promoted review decision fixture as subject matter only and may not replace, extend, restate, simulate, or paraphrase substitute approval text for a canonical review decision.

### `runtime_state_forbidden`

Meaning: explicitly preserves that a human-review intake artifact may not become runtime state or runtime-facing authority.

Boundary note: prohibition only, not a permitted state model.

### `workflow_state_forbidden`

Meaning: explicitly preserves that a human-review intake artifact may not become workflow state or process-tracking state.

Boundary note: prohibition only, not a permitted workflow model.

### `execution_authority_forbidden`

Meaning: explicitly preserves that a human-review intake artifact may not become execution authority or executable handoff.

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
- introduces routing semantics, queue semantics, backlog semantics, assignment semantics, transfer semantics, approval-ready semantics, or execution-ready semantics outside explicit prohibition wording
- weakens source-of-truth rules
- weakens hard-stop semantics
- weakens Water Mill boundary discipline
- weakens packet/world-state separation
- weakens single-dossier continuity
- weakens pre-decision versus review-decision separation
- implies runtime authority, mutation authority, execution authority, approval inflation, canonical replacement, or workflow-state inflation
