# UEIA Fixture Consumer Profile Spec v1

## Operational Read

- Status: `specification-only`
- Fixture scope: `four immutable canonical fixtures only`
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

## Purpose

This specification defines the first approved consumer-profile model for future tooling, tests, and bounded agents that may read immutable canonical fixtures.

It exists to preserve:
- immutable fixture handling
- single-dossier continuity
- packet/world-state separation
- Water Mill design-asset boundary discipline
- non-executing downstream consumption

## Fixture Scope

Only these canonical fixtures are in scope:
- [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md)
- [`promoted-agent-brief-v1.md`](C:/SmallVille/docs/examples/promoted-agent-brief-v1.md)
- [`promoted-review-decision-v1.md`](C:/SmallVille/docs/examples/promoted-review-decision-v1.md)

No additional fixtures, no second dossier, and no alternate fixture family are introduced by this specification.

## Source-Of-Truth Rule

The source of truth for fixture consumption remains the frozen canonical fixture text at the canonical paths listed in the canonical fixture contract.

No consumer profile, comparison output, summary output, or diagnostics export may replace that canonical source text.

## Consumer-Profile Model

Consumer profiles are bounded read-only profiles that define how a future tool, test, or agent may interact with the canonical fixture set.

Each profile must define:
- purpose
- allowed inputs
- allowed outputs
- allowed actions
- forbidden actions
- evidence boundary
- runtime boundary
- mutation boundary

Profiles are approval-scoped behavior descriptions only. They are not implementations, runtimes, validators, or scripts.

## No-Implementation Boundary

This specification does not authorize:
- tool implementation
- validator implementation
- script implementation
- fixture mirror generation
- runtime-facing ingestion

Profile definitions in this note are contract descriptions only.

## No-Runtime-Coupling Boundary

No consumer profile may:
- bind canonical fixtures to live Smallville runtime state
- treat a packet fixture as runtime world state
- treat a review decision as execution approval
- convert Water Mill design-asset context into scene or world mutation authority

## No-Mutation Boundary

No consumer profile may:
- mutate a canonical fixture file
- rewrite provenance wording
- relabel inferred content as observed
- silently repair missing evidence
- merge a second dossier into the canonical chain

## Support Matrix

### Supported

- read-only fixture consumption
- canonical ID checks
- schema-aligned section checks
- provenance-preservation checks
- fact-status preservation checks
- packet/world-state separation checks
- Water Mill design-asset boundary checks
- single-dossier continuity checks
- derived diagnostics exported outside canonical fixture files
- derived outputs that remain non-authoritative relative to canonical fixture text

### Unsupported

- runtime coupling
- mutation of fixture files
- execution authority
- scene or world mutation authority
- Water Mill treated as observed real-world authority
- inferred content promoted to observed
- silent second-dossier merge
- geometry, mesh or model asset interpretation
- runtime map layers
- renderable scene assets

### Intentionally Deferred

- implementation of consumer tools
- validator binaries or scripts
- runtime-facing fixture consumption
- alternate fixture formats or mirrors

## Explicit Stop Conditions

Stop if any future pass tries to:
- widen scope beyond the four canonical fixtures
- give any consumer profile mutation authority
- give any consumer profile runtime authority
- treat a packet fixture as runtime world state
- treat Water Mill design-asset context as observed built authority without accepted evidence
- silently merge a second dossier
- convert descriptive fixture consumption into implementation work
