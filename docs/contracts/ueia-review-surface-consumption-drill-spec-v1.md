# UEIA Review Surface Consumption Drill Spec v1

## Purpose

This specification defines the bounded drill-governance layer for already-presented diagnostic outputs in the frozen UEIA fixture ladder. It describes how a drill may orient human review across already-presented outputs without changing authority, weakening source-of-truth discipline, or drifting into UI flow, runtime workflow, automation, validation, control behavior, prioritization logic, or execution sequencing.

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

## Upstream Authority

This layer remains subordinate to the frozen:

- canonical fixture authority
- consumer profile authority
- conformance authority
- capability crosswalk authority
- diagnostic-output authority
- diagnostic-output consumption authority
- review-surface authority

## Fixture Scope

Only these canonical fixtures are in scope:

- `docs/examples/promoted-archive-record-v1.md`
- `docs/examples/promoted-derived-packet-v1.md`
- `docs/examples/promoted-agent-brief-v1.md`
- `docs/examples/promoted-review-decision-v1.md`

## Consumer-Profile Scope

Only these approved consumer profiles are in scope:

- `Fixture Reader`
- `Fixture Comparator`
- `Fixture Diagnostics Consumer`
- `Fixture Export Consumer`

## Diagnostic Output Scope

Only these frozen output families are in scope:

- `summary`
- `drift-note`
- `stop-report`
- `diagnostics-bundle`
- `export-summary`

## Review-Surface Authority Scope

This layer depends only on already-presented outputs and the already-approved review-surface behaviors defined upstream. It does not add new presentation behavior families or reopen review-surface behavior definitions.

## Drill Model

A drill is a bounded review walkthrough over already-presented diagnostic outputs.

A drill may read already-presented diagnostic outputs and preserved review-surface context only.

A drill may orient human review but may not create new authority.

A drill is not a UI flow, runtime workflow, automation sequence, validator, operator control surface, prioritization engine, or execution sequence.

A drill may preserve already-presented order and grouped context for review only and may not become navigation design, workflow sequencing, or packaging semantics.

A drill may pause on drift-oriented review attention without creating correction authority, prioritization logic, or execution sequencing.

A drill must stop on a `stop-report` or any surfaced hard stop condition and may not continue as though the stop were advisory only.

Preserving canonical-path visibility means the canonical path for the underlying fixture remains visible wherever the drill references a presented diagnostic output.

A drill may surface provenance notes, boundary notes, and severity context as preserved review context only and may not strengthen them into new authority.

A drill may not treat Water Mill design-asset-derived context as observed built authority without accepted evidence.

A drill may not treat packet content as runtime world state.

A drill may not introduce a second dossier.

## Source-of-Truth Rule

Canonical fixture files at canonical paths remain the source of truth. A drill may preserve review order, grouped context, visibility, and pause/stop attention, but it may not replace, normalize, override, mirror, or supersede canonical fixture text.

## Non-Authoritative Drill Rule

All drill context remains derived only, audit-oriented, and non-replacing. Drill wording may orient review but may not create approval rights, correction rights, execution rights, runtime rights, or replacement authority.

## Boundary Preservation Rules

- Water Mill remains design-asset-derived context only and may not be restated as observed built authority without accepted evidence.
- The promoted derived packet remains an interpretive artifact only and never runtime world state.
- Single-dossier continuity remains explicit and may not be weakened by drill wording, preserved order, grouped context, pause behavior, or stop behavior.
- Drill context remains non-authoritative and non-replacing even when preserving surfaced severity, provenance notes, boundary notes, or hard-stop visibility.

## No-Implementation Boundary

This specification does not authorize UI components, layouts, routes, navigation, panels, widgets, interaction mechanics, validator logic, schema work, tooling logic, script logic, or implementation flow.

## No-Runtime-Coupling Boundary

This specification does not authorize runtime hooks, runtime workflow, runtime mutation, runtime approval, runtime control, or runtime-coupled drill behavior.

## No-Automation Boundary

This specification does not authorize automation, drill execution logic, machine-driven walkthrough progression, automatic stop-resolution behavior, or executable drill states.

## No-Control Boundary

This specification does not authorize dashboard behavior, operator-console behavior, control-surface behavior, prioritization engines, scoring systems, ranking systems, urgency models, or execution sequencing.

## Support Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Drill-governance definitions | Supported | Limited to already-presented diagnostic outputs |
| Boundary-preserving drill behaviors | Supported | Limited to the approved drill behavior vocabulary |
| Boundary-preserving examples | Supported | Illustrative only |
| UI implementation | Not supported | Deferred |
| Runtime workflow or hooks | Not supported | Forbidden in this phase |
| Automation or validator behavior | Not supported | Forbidden in this phase |
| Scoring or prioritization logic | Not supported | Forbidden in this phase |
| Packaging, mirrors, or executable drill states | Not supported | Forbidden in this phase |

## Explicit Stop Conditions

Stop if any drafting or later pass:

- widens scope beyond the four canonical fixtures
- widens scope beyond the four approved consumer profiles
- widens scope beyond the five frozen output families
- invents new fixture families, new consumer profiles, new output families, or mutable drill states
- implies UI mechanics, runtime workflow, automation, validator behavior, control behavior, or execution sequencing
- implies scoring, ranking, urgency, prioritization, or hidden authority selection
- hides or softens a hard stop condition
- weakens canonical source-of-truth rules
- weakens Water Mill boundary discipline
- weakens packet/world-state separation
- weakens single-dossier continuity
