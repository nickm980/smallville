# UEIA Fixture Consumer Conformance Spec v1

## Operational Read

- Status: `specification-only`
- Fixture scope: `four immutable canonical fixtures only`
- Consumer-profile scope: `four approved read-only profiles only`
- Active accepted checkpoint tag: `smallville-post-proposal-parent-fix`
- Active accepted checkpoint commit: `150e2073fdb608efb4e76ad05cdacbfdb7119222`
- Provenance pack commit: `ab89743f19077d0e23c9a6270658a511f653c55e`
- Operator-surface consolidation freeze commit: `13732640d71a4a842142c37243bc46961f75eb5e`
- Phase 1 docs freeze commit: `fe49aafe8ac977c7264c2e33fd1ac3794399df24`
- Phase 1 schema freeze commit: `8a1ce0d2a218105ce3e46f94bdcd18fa792684a9`
- Single-dossier bootstrap spec freeze commit: `13ccb92f3b2fa7eff5fac48d12c781af29178cba`
- Dossier example-set freeze commit: `c59e9c4892dffe86c0d89fa1fb1d79bdabc88fc9`
- Promotion-pack freeze commit: `c5c6a4088866e938e03fa2cb1369b891c9f6a741`
- Promoted-specimen freeze commit: `61a8f705c7cc611494ef97d5bbb17ce619e6b225`
- Canonical fixture contract freeze commit: `0748e95ab164fdd5d80ecf61bf339f081e421049`
- Canonical fixture reference freeze commit: `193b2ed8cacc309765c367ee980ecf1e5a8ae2ca`
- Fixture consumer profile contract freeze commit: `d090068395f0728cc8fceeb0277a9b3452959aba`
- Fixture consumer profile example freeze commit: `d863fa0f3b210196fd701b62c52ab4eb7166d806`

## Purpose

This specification defines the first conformance model for mapping approved consumer profiles to approved canonical fixture types.

It exists to preserve:
- immutable canonical fixture handling
- single-dossier continuity
- packet/world-state separation
- Water Mill design-asset boundary discipline
- explicit stop-and-report behavior when boundaries are threatened

## Fixture Scope

Only these canonical fixtures are in scope:
- [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md)
- [`promoted-agent-brief-v1.md`](C:/SmallVille/docs/examples/promoted-agent-brief-v1.md)
- [`promoted-review-decision-v1.md`](C:/SmallVille/docs/examples/promoted-review-decision-v1.md)

No additional fixture family, mirror, or second dossier is introduced by this specification.

## Consumer-Profile Scope

Only these consumer profiles are in scope:
- `Fixture Reader`
- `Fixture Comparator`
- `Fixture Diagnostics Consumer`
- `Fixture Export Consumer`

No additional profile gains approval through this specification.

## Conformance Model

Conformance is defined as a bounded relationship between:
- one approved consumer profile
- one approved canonical fixture type
- one conformance classification
- one short rationale
- one stop-rule overlay when required

The only allowed classifications are:
- `allowed`
- `allowed-with-stop-rules`
- `not-allowed`

The matrix is descriptive only. It does not implement access control, validation logic, or runtime behavior.
Conformance classifications are bounded interaction classifications only. They do not grant blanket approval for unlimited downstream capability.

## Source-Of-Truth Rule

The frozen canonical fixture text at the canonical paths remains the source of truth for all conformant consumer behavior.

No conformance note, comparison output, diagnostics bundle, or export summary may replace that source text.

## No-Implementation Boundary

This specification does not authorize:
- consumer implementation
- validator implementation
- script implementation
- fixture mirror generation
- runtime-facing ingestion

## No-Runtime-Coupling Boundary

No conformant consumer behavior may:
- bind canonical fixtures to live Smallville runtime state
- treat a packet fixture as runtime world state
- treat a review decision as execution approval
- convert Water Mill design-asset context into scene or world mutation authority

## No-Mutation Boundary

No conformant consumer behavior may:
- mutate a canonical fixture file
- rewrite provenance wording
- promote inferred content to observed
- auto-repair missing evidence
- silently merge a second dossier

## Support Matrix

### Supported

- read-only conformance mapping between approved profiles and approved fixtures
- rationale-bearing cell classifications
- stop-rule overlays for sensitive fixture interactions
- derived diagnostics that remain external and non-authoritative
- preservation checks for provenance, fact status, packet/world-state separation, Water Mill boundary, and single-dossier continuity

### Unsupported

- runtime-coupled fixture use
- mutation-capable consumer behavior
- execution authority
- scene/world mutation authority
- Water Mill treated as observed real-world authority
- packet treated as runtime world state
- second-dossier expansion

### Intentionally Deferred

- tooling implementation
- validator or script implementation
- automated conformance enforcement
- alternative fixture formats or mirrors

## Explicit Stop Conditions

Stop if any future pass tries to:
- widen scope beyond the four canonical fixtures
- widen scope beyond the four approved consumer profiles
- grant runtime authority to any consumer
- grant mutation authority to any consumer
- treat a packet fixture as runtime world state
- treat Water Mill design-asset context as observed built authority without accepted evidence
- present derived output as canonical replacement
- convert conformance mapping into implementation work
