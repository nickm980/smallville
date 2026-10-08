# Human Review Intake Types v1

## Purpose

This note defines the approved read-only intake families for bounded human review intake over already-consumed drill outcomes. All intake families remain pre-decision only and remain distinct from canonical review-decision artifacts.

## Intake Families In Scope

- `review-intake-summary`
- `review-intake-pause-note`
- `review-intake-stop-note`
- `review-intake-handoff-note`

A `review-intake-summary` is bounded human-review intake summary only and does not imply completed review, approval readiness, or decision readiness.

A `review-intake-pause-note` preserves drift-pause context for human attention only and does not imply queued work, backlog, deferred workflow handling, or correction authority.

A `review-intake-stop-note` preserves unresolved hard-stop context for human attention only and does not imply stop resolution, rerouting, downstream clearance, or decision escalation.

A `review-intake-handoff-note` preserves bounded pre-decision human-attention handoff only and may not become execution handoff, workflow routing, transfer state, or review-decision proxy.

## Shared Intake Boundaries

All intake families:

- remain derived only, review-only, and pre-decision
- may preserve context from already-consumed drill outcomes only
- may support human attention only
- may not replace canonical fixtures
- may not replace, extend, restate, simulate, or paraphrase substitute approval text for canonical review decisions
- may not imply runtime authority, mutation authority, execution authority, approval inflation, workflow-state inflation, or canonical replacement

## `review-intake-summary`

**Purpose**

Preserve a bounded human-review intake summary from an already-consumed drill outcome while keeping canonical-path visibility and boundary context explicit.

**Allowed inputs**

- One or more already-consumed in-scope drill outcomes
- One in-scope consumer profile
- The underlying canonical fixture at its canonical path as source-of-truth reference

**Allowed contents**

- Intake identification and canonical fixture linkage
- Preserved review-only context from consumed outcomes
- Provenance and boundary notes
- Human attention notes that remain pre-decision

**Forbidden contents**

- Completed review claims
- Approval readiness claims
- Decision readiness claims
- Canonical substitute text
- Runtime or workflow language

**Authority level**

Derived only, review-only, pre-decision

**Replacement boundary**

May not replace canonical fixture text or function as substitute source authority

**Review-decision boundary**

May not become a review decision and may not replace, extend, restate, simulate, or paraphrase substitute approval text for a canonical review decision

**Runtime boundary**

May not imply runtime state, runtime action, or runtime approval

**Workflow-state boundary**

May not imply queued work, completion state, assignment semantics, or deferred workflow semantics

## `review-intake-pause-note`

**Purpose**

Preserve drift-pause context for human attention without turning pause meaning into workflow, backlog, or correction authority.

**Allowed inputs**

- One or more already-consumed in-scope drill outcomes with preserved drift-pause context
- One in-scope consumer profile
- The underlying canonical fixture at its canonical path as source-of-truth reference

**Allowed contents**

- Drift-pause preservation wording
- Provenance and boundary notes
- Human attention notes about paused review attention only
- Canonical-path visibility

**Forbidden contents**

- Queued work language
- Deferred workflow handling language
- Backlog language
- Correction authority
- Runtime or execution guidance

**Authority level**

Derived only, review-only, pre-decision

**Replacement boundary**

May not replace canonical fixture text or normalize drift by replacement

**Review-decision boundary**

May not become a review decision and may not convert pause context into approval language

**Runtime boundary**

May not imply runtime state, runtime response, or runtime priority

**Workflow-state boundary**

May not imply waiting state, assignment state, pending action state, or workflow state

## `review-intake-stop-note`

**Purpose**

Preserve that human review intake encountered an unresolved hard stop while keeping halt semantics explicit and unresolved.

**Allowed inputs**

- One or more already-consumed in-scope drill outcomes including `drill-stop-outcome` when present
- One in-scope consumer profile
- The underlying canonical fixture at its canonical path as source-of-truth reference

**Allowed contents**

- Unresolved hard-stop preservation wording
- Canonical-path visibility
- Boundary notes and provenance notes
- Human attention notes that preserve halt meaning only

**Forbidden contents**

- Stop resolution language
- Rerouting language
- Downstream clearance language
- Advisory-only stop language
- Runtime or execution guidance

**Authority level**

Derived only, review-only, pre-decision, hard-stop-preserving

**Replacement boundary**

May not replace canonical fixture text or operate as a substitute decision layer

**Review-decision boundary**

May not become a review decision and may not replace, extend, restate, simulate, or paraphrase substitute approval text for a canonical review decision

**Runtime boundary**

May not imply runtime enforcement, runtime state, runtime control, or runtime approval

**Workflow-state boundary**

May not imply stop resolution state, action state, or workflow state

## `review-intake-handoff-note`

**Purpose**

Preserve pre-decision human-attention handoff context from an already-consumed drill outcome without turning handoff into workflow transition or execution handoff.

**Allowed inputs**

- One or more already-consumed in-scope drill outcomes
- One in-scope consumer profile
- The underlying canonical fixture at its canonical path as source-of-truth reference

**Allowed contents**

- Human-attention handoff wording limited to review-only continuation
- Canonical-path visibility
- Provenance and boundary notes needed for safe human attention
- Explicit pre-decision caveats

**Forbidden contents**

- Execution handoff language
- Workflow transition language
- Routing logic language
- Approval inflation language
- Canonical replacement language

**Authority level**

Derived only, review-only, pre-decision, human-attention-handoff-oriented

**Replacement boundary**

May not replace canonical fixture text or act as downstream canonical authority

**Review-decision boundary**

May not become a review decision and may not replace, extend, restate, simulate, or paraphrase substitute approval text for a canonical review decision

**Runtime boundary**

May not imply runtime-facing input, runtime handoff, runtime control, or runtime action

**Workflow-state boundary**

May not imply assignment state, transfer state, workflow state, or execution-ready semantics

## Explicit Stop Conditions

Stop if intake-family wording:

- collapses pre-decision intake artifacts into review decisions
- collapses intake artifacts into workflow state, approval state, execution state, or control state
- softens unresolved hard-stop meaning
- weakens canonical source-of-truth rules
- weakens Water Mill boundary discipline
- weakens packet/world-state separation
- weakens single-dossier continuity
- implies runtime authority, mutation authority, execution authority, approval inflation, canonical replacement, or workflow-state inflation
