# UEIA Human Review Intake Spec v1

## Purpose

This specification defines the bounded human-review-intake layer for already-consumed drill outcomes inside the frozen UEIA governance ladder. It describes how approved human-review intake artifacts may preserve pre-decision human attention context without changing authority, weakening source-of-truth discipline, or drifting into tooling, validators, scripts, schemas, workflow engines, routing logic, runtime behavior, or UI design.

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

## In-Scope Drill Outcome Families

Only these frozen drill outcome families are in scope:

- `drill-summary`
- `drill-pause-note`
- `drill-stop-outcome`
- `drill-handoff-note`

## Intake Model

Human review intake means bounded pre-decision handoff of an already-consumed drill outcome for human attention only.

Human review intake remains pre-decision only and may not become review decision generation, approval handling, workflow handling, execution handling, or control handling.

Human review intake may read, preserve, and interpret already-consumed drill outcomes only and may not derive a new drill outcome family.

A human-review intake artifact may preserve review-only context, drift-pause context, unresolved hard-stop context, or human-attention handoff context only.

When a human-review intake artifact includes a consumed `drill-stop-outcome`, human review intake may continue only for the purpose of preserving halt-aware human attention context and may not continue on the flagged subject as though the hard stop were resolved.

A human-review intake artifact may reference a promoted review decision fixture as subject matter only and may not replace, extend, restate, simulate, or paraphrase substitute approval text for a canonical review decision.

The hard distinctions remain explicit:

- drill outcome = pre-decision artifact
- drill outcome consumption = bounded read-only downstream use of a pre-decision artifact
- human review intake = bounded pre-decision handoff for human attention only
- review decision = approval-boundary artifact

## Source-of-Truth Rule

Canonical fixture files at canonical paths remain the source of truth wherever human-review intake artifacts are recorded, read, or handed forward for human attention.

## Pre-Decision Rule

Human-review intake artifacts remain pre-decision artifacts only. Intake may orient human attention and preserve bounded downstream meaning, but it may not become review decision generation, may not simulate approval, and may not compress L3 review work into L4 human approval.

## Non-Authoritative And Non-Replacing Rule

All human-review intake artifacts remain derived only, non-authoritative, and non-replacing. No human-review intake artifact may imply runtime authority, mutation authority, execution authority, approval inflation, workflow-state inflation, or canonical replacement.

## Boundary Preservation Rules

- Water Mill remains design-asset-derived context only within human-review intake artifacts and may not be restated as observed built authority without accepted evidence.
- Packet content remains interpretive artifact only within human-review intake artifacts and may not be restated as runtime world state.
- Single-dossier continuity remains explicit within human-review intake artifacts and may not be weakened by intake wording.
- Review-decision boundaries remain explicit within human-review intake artifacts and may not be weakened by downstream wording.
- Canonical-path visibility remains explicit wherever a human-review intake artifact refers to an in-scope fixture or prior consumed outcome context.

## No-Runtime Boundary

This specification does not authorize runtime behavior, runtime state creation, runtime hooks, runtime mutation, runtime approval, runtime control, or runtime-coupled intake behavior.

## No-Mutation Boundary

This specification does not authorize canonical rewrite, canonical replacement, fixture mutation, inferred-to-observed promotion, silent repair, or downstream correction authority.

## No-Execution Boundary

This specification does not authorize execution handling, executable handoff semantics, execution clearance, or control behavior.

## No-Approval-Inflation Boundary

This specification does not authorize approval substitution, review-decision simulation, approval-ready wording, or any intake language that would replace, extend, restate, simulate, or paraphrase substitute approval text for a canonical review decision.

## No-Workflow-State Boundary

This specification does not authorize workflow state, queue semantics, routing logic, backlog semantics, assignment semantics, transfer semantics, deferred-work semantics, or other process-tracking behavior disguised as review-only intake language.

## Support Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Human-review-intake definitions | Supported | Limited to bounded pre-decision human-attention handoff |
| Intake-family definitions | Supported | Limited to the four approved intake families |
| Intake field vocabulary | Supported | Vocabulary only, not schema |
| Boundary-preserving examples | Supported | Illustrative only |
| Intake-permission matrix doc | Not supported | Forbidden in this phase |
| Tooling, validators, or scripts | Not supported | Forbidden in this phase |
| Runtime, workflow, or routing behavior | Not supported | Forbidden in this phase |
| Review-decision replacement or simulation | Not supported | Forbidden in this phase |
| UI, schemas, packaging, or mirrors | Not supported | Forbidden in this phase |

## Explicit Stop Conditions

Stop if any drafting or later pass:

- widens scope beyond the four canonical fixtures
- widens scope beyond the four approved consumer profiles
- widens scope beyond the four frozen drill outcome families
- collapses intake into review decision generation, approval handling, workflow handling, execution handling, or control handling
- weakens unresolved hard-stop semantics
- weakens canonical source-of-truth rules
- weakens Water Mill boundary discipline
- weakens packet/world-state separation
- weakens single-dossier continuity
- implies replacement authority, runtime authority, mutation authority, execution authority, approval inflation, or workflow-state inflation
- drifts into tooling, validators, scripts, schemas, runtime behavior, workflow engines, routing logic, or UI design
