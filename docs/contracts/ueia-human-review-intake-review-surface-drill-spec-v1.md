# UEIA Human Review Intake Review Surface Drill Spec v1

## Purpose

This specification defines the bounded drill-governance layer for already-presented consumed human-review intake artifacts in the frozen UEIA governance ladder. It describes how a drill may orient human review across already-presented consumed intake artifacts without changing authority, weakening source-of-truth discipline, or drifting into tooling, validators, scripts, schemas, workflow engines, routing logic, runtime behavior, UI design, dashboard control, scoring, ranking, prioritization, or review-decision handling.

## Baseline References

- Active accepted checkpoint tag: `smallville-post-proposal-parent-fix`
- Active accepted checkpoint commit: `150e2073fdb608efb4e76ad05cdacbfdb7119222`
- Provenance pack commit: `ab89743f19077d0e23c9a6270658a511f653c55e`
- Operator-surface consolidation freeze commit: `13732640d71a4a842142c37243bc46961f75eb5e`
- Diagnostic-output contracts freeze commit: `b9c48a431e7941c838525b5dd12d7a80eafbd0b3`
- Diagnostic-output examples freeze commit: `9483dea2e7feb17a95482f261f31d77ce01da56c`
- Diagnostic-output consumption contracts freeze commit: `5c67784cd13def6104b33bbc3ab8f748e2cd7fe1`
- Diagnostic-output consumption examples freeze commit: `3f01f85c2d07de90a83b17085b8b9867d60a51ef`
- Review-surface contracts freeze commit: `1eb7d8e994dddd3a92cbeb4cf033754b5589972f`
- Review-surface examples freeze commit: `043c1449729cf3ba62c0a0a8585e531fc06298c4`
- Review-surface drill contracts freeze commit: `749c520b3b1cba3f00c08c0ce6c61a2d260adbef`
- Review-surface drill examples freeze commit: `8984de042ffbc4f9f57b37c62dd1c86df89f1801`
- Review-surface drill outcome contracts freeze commit: `9a94031fa346a05c0da18ca5a933d200658b9e4d`
- Review-surface drill outcome examples freeze commit: `86121ec381f4e206e0c2a87853d388c840155257`
- Review-surface drill outcome consumption contracts freeze commit: `dcb5142bef2185352939b2ddf47761582d2bd56f`
- Review-surface drill outcome consumption examples freeze commit: `6f1115c17aaa86e0fe9b635fa7ae4d57999007fb`
- Human-review-intake contracts freeze commit: `6a70f5be41c15b79a8137d1709c66ccc8564c17f`
- Human-review-intake examples freeze commit: `276c5fb5b0486c4c2a2af3aa241563a5efbb5ba6`
- Human-review-intake consumption contracts freeze commit: `ae9a23adabc362f5469563c1e0ca55158276d7b6`
- Human-review-intake consumption examples freeze commit: `e852e5403f3360fbdc881aa94b3c5c57141654b6`
- Human-review-intake review-surface contracts freeze commit: `2b5fc186d8b6f419ba15eef06175f890e6d71c26`
- Human-review-intake review-surface examples freeze commit: `470bde3802fb537a14aa7a59e716bae50dd93f4d`

## Upstream Authority

This layer remains subordinate to the frozen:

- canonical fixture authority
- consumer profile authority
- conformance authority
- capability crosswalk authority
- diagnostic-output authority
- diagnostic-output consumption authority
- review-surface authority
- review-surface drill authority
- review-surface drill outcome authority
- review-surface drill outcome consumption authority
- human-review-intake authority
- human-review-intake consumption authority
- human-review-intake review-surface authority

## In-Scope Fixtures

Only these canonical fixtures are in scope:

- `docs/examples/promoted-archive-record-v1.md`
- `docs/examples/promoted-derived-packet-v1.md`
- `docs/examples/promoted-agent-brief-v1.md`
- `docs/examples/promoted-review-decision-v1.md`

## In-Scope Consumer Profiles

Only these approved consumer profiles are in scope:

- `Fixture Reader`
- `Fixture Comparator`
- `Fixture Diagnostics Consumer`
- `Fixture Export Consumer`

## In-Scope Human-Review Intake Families

Only these frozen human-review intake families are in scope:

- `review-intake-summary`
- `review-intake-pause-note`
- `review-intake-stop-note`
- `review-intake-handoff-note`

## In-Scope Upstream Review-Surface Behaviors

This layer depends only on already-presented consumed intake artifacts and the already-approved upstream review-surface behaviors:

- `group-for-review`
- `sort-by-existing-field`
- `filter-by-existing-field`
- `highlight-severity`
- `highlight-stop-condition`
- `show-canonical-path-reference`
- `show-provenance-note`
- `show-boundary-notes`

It does not add new presentation behavior families or reopen review-surface behavior definitions.

## Drill Model

A human-review-intake review-surface drill is a bounded review walkthrough over already-presented consumed human-review intake artifacts.

A human-review-intake review-surface drill may read already-presented consumed intake artifacts and preserved review-surface context only.

A human-review-intake review-surface drill may orient human review but may not create new authority.

A human-review-intake review-surface drill remains pre-decision only and may not become review decision handling, approval handling, workflow handling, execution handling, routing handling, or control handling.

A human-review-intake review-surface drill may preserve already-presented order and grouped context for review only and may not become navigation design, workflow sequencing, routing logic, or packaging semantics.

A human-review-intake review-surface drill may pause on drift-oriented review attention without creating correction authority, queue semantics, backlog semantics, prioritization logic, or execution handling.

A human-review-intake review-surface drill must stop on a presented consumed `review-intake-stop-note` or any surfaced unresolved hard stop and may not continue as though the hard stop were advisory only.

Preserving canonical-path visibility means the canonical path for the underlying fixture remains visible wherever the drill references a presented consumed human-review intake artifact.

A human-review-intake review-surface drill may surface provenance notes, boundary notes, and severity context as preserved review context only and may not strengthen them into new authority.

A human-review-intake review-surface drill may not treat Water Mill design-asset-derived context as observed built authority without accepted evidence.

A human-review-intake review-surface drill may not treat packet content as runtime world state.

A human-review-intake review-surface drill may not introduce a second dossier.

The hard distinctions remain explicit:

- drill outcome = pre-decision artifact
- drill outcome consumption = bounded read-only downstream use of a pre-decision artifact
- human review intake = bounded pre-decision handoff for human attention only
- human review intake consumption = bounded read-only downstream use of a pre-decision intake artifact
- human review intake review surface = bounded presentation of already-consumed intake artifacts for human review only
- human review intake review surface drill = bounded review walkthrough over already-presented consumed intake artifacts only
- review decision = approval-boundary artifact

## Source-of-Truth Rule

Canonical fixture files at canonical paths remain the source of truth. A drill may preserve review order, grouped context, pause attention, stop attention, and contextual visibility, but it may not replace, normalize, override, mirror, or supersede canonical fixture text.

## Pre-Decision Rule

All human-review-intake review-surface drill behavior remains pre-decision only. A drill may orient human review around already-presented consumed intake artifacts, but it may not become review-decision handling, approval handling, or substitute approval text.

## Non-Authoritative And Non-Replacing Rule

All drill context remains derived only, non-authoritative, and non-replacing. No human-review-intake review-surface drill may imply runtime authority, mutation authority, execution authority, approval inflation, canonical replacement, or workflow-state inflation.

## Boundary Preservation Rules

- Water Mill remains design-asset-derived context only and may not be restated as observed built authority without accepted evidence.
- Packet content remains interpretive artifact only and never runtime world state.
- Single-dossier continuity remains explicit and may not be weakened by drill wording, preserved order, grouped context, pause behavior, or stop behavior.
- Drill context remains non-authoritative and non-replacing even when preserving surfaced severity, provenance notes, boundary notes, canonical-path visibility, or hard-stop visibility.

## No-Runtime Boundary

This specification does not authorize runtime behavior, runtime hooks, runtime workflow, runtime mutation, runtime approval, runtime control, or runtime-coupled drill behavior.

## No-Mutation Boundary

This specification does not authorize canonical rewrite, canonical replacement, fixture mutation, inferred-to-observed promotion, silent repair, or downstream correction authority.

## No-Execution Boundary

This specification does not authorize execution handling, execution handoff, downstream clearance, executable drill states, or execution-sequencing behavior.

## No-Approval-Inflation Boundary

This specification does not authorize review-decision replacement, approval substitution, substitute approval text, approval-ready wording, or any drill wording that would replace, extend, restate, simulate, or paraphrase a canonical review decision.

## No-Workflow-State Boundary

This specification does not authorize workflow handling, workflow state, routing handling, routing logic, queue semantics, backlog semantics, assignment semantics, transfer semantics, navigation design, workflow sequencing, dashboard control, or other process-driving behavior disguised as drill context.

## Support Matrix

| Area | Availability | Notes |
| --- | --- | --- |
| Drill-governance definitions | Supported | Limited to already-presented consumed intake artifacts |
| Boundary-preserving drill behaviors | Supported | Limited to the approved drill behavior vocabulary |
| Boundary-preserving examples | Supported | Illustrative only |
| Tooling, validators, or scripts | Not supported | Forbidden in this phase |
| Workflow, routing, or control behavior | Not supported | Forbidden in this phase |
| Runtime behavior or UI design | Not supported | Forbidden in this phase |
| Review-decision replacement or simulation | Not supported | Forbidden in this phase |
| Scoring, ranking, or prioritization logic | Not supported | Forbidden in this phase |

## Explicit Stop Conditions

Stop if any drafting or later pass:

- widens scope beyond the four canonical fixtures
- widens scope beyond the four approved consumer profiles
- widens scope beyond the four frozen human-review intake families
- widens scope beyond the frozen upstream review-surface behaviors
- implies tooling, validators, scripts, schemas, workflow engines, routing logic, runtime behavior, UI design, dashboard control, scoring, ranking, prioritization, or execution sequencing
- collapses drill behavior into review-decision handling, workflow handling, routing handling, execution handling, or control handling
- hides or softens an unresolved hard stop
- weakens canonical source-of-truth rules
- weakens Water Mill boundary discipline
- weakens packet/world-state separation
- weakens single-dossier continuity
