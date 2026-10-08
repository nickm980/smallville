# UEIA Fixture Consumer Capability Crosswalk Spec v1

## Operational Read

- Status: `specification-only`
- Execution environment: `local repository and local files only`
- Fixture scope: `four immutable canonical fixtures only`
- Consumer-profile scope: `four approved read-only profiles only`
- Capability scope: `explicit v1 capability vocabulary only`
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
- Fixture consumer conformance contract freeze commit: `302d6bb2ea44d9887c66d6a86a31c45bc9b508fd`
- Fixture consumer conformance example freeze commit: `a5ad8ffda1c5f52d3aca10e325124b013a339390`

## Purpose

This specification defines the first capability-precision layer beneath the frozen fixture consumer profiles and the frozen conformance matrix.

It exists to preserve:
- read-only canonical fixture use
- canonical paths as source of truth
- Water Mill as design-asset-derived context only
- packet as interpretive artifact only, never runtime world state
- single-dossier continuity
- derived outputs as non-authoritative and non-replacing

## Fixture Scope

Only these canonical fixtures are in scope:
- [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md)
- [`promoted-agent-brief-v1.md`](C:/SmallVille/docs/examples/promoted-agent-brief-v1.md)
- [`promoted-review-decision-v1.md`](C:/SmallVille/docs/examples/promoted-review-decision-v1.md)

No additional fixtures, mirrors, or second dossier are introduced by this specification.

## Consumer-Profile Scope

Only these consumer profiles are in scope:
- `Fixture Reader`
- `Fixture Comparator`
- `Fixture Diagnostics Consumer`
- `Fixture Export Consumer`

No additional profile gains approval through this specification.

## Capability Scope

This v1 crosswalk is limited to the explicit capability vocabulary defined in:
- [`fixture-consumer-capability-catalog-v1.md`](C:/SmallVille/docs/contracts/fixture-consumer-capability-catalog-v1.md)

This scope is intentionally narrower than the broader permission language in the frozen consumer-profile package.

For v1:
- capability tokens are explicit and closed
- broader validation language remains preserved by the frozen profile layer
- no new capability may be invented implicitly

## Crosswalk Model

The crosswalk maps:
- one approved consumer profile
- one approved canonical fixture type
- one approved or forbidden capability token
- one conformance classification
- one short rationale
- one stop-overlay reference when required

Allowed classifications are:
- `allowed`
- `allowed-with-stop-rules`
- `not-allowed`

These classifications are bounded interaction classifications only. They do not grant blanket downstream approval beyond the named profile, fixture type, capability, and stop-overlay context.

## Source-Of-Truth Rule

The frozen canonical fixture text at the canonical paths remains the source of truth for all capability use in this package.

No summary, comparison note, diagnostic note, export, or other derived output may replace canonical fixture text as source of truth.

## No-Implementation Boundary

This specification does not authorize:
- tooling implementation
- validator implementation
- script implementation
- fixture mirror generation
- runtime-facing ingestion

The capability layer is descriptive only.

## No-Runtime-Coupling Boundary

No capability in this crosswalk may:
- bind a canonical fixture to live Smallville runtime state
- treat a packet as runtime world state
- treat a review decision as execution approval
- convert Water Mill design-asset context into scene or world mutation authority

## No-Mutation Boundary

No capability in this crosswalk may:
- mutate a canonical fixture file
- rewrite canonical wording
- infer away missing evidence
- promote inferred content to observed
- silently merge a second dossier

## Support Matrix

### Supported

- explicit read-only capability vocabulary for the approved profiles
- capability-level mapping across the four canonical fixture types
- rationale-bearing capability classifications
- stop-overlay mapping for sensitive capability contexts
- derived outputs that remain external, non-authoritative, and non-replacing

### Unsupported

- runtime authority
- mutation authority
- validator or script design
- fixture mirrors
- Water Mill treated as observed built authority
- packet treated as runtime world state
- second-dossier expansion

### Intentionally Deferred

- capability implementation
- validator or script enforcement
- runtime-facing fixture consumption
- non-canonical alternative fixture representations

## Explicit Stop Conditions

Stop if any future pass tries to:
- widen scope beyond the four canonical fixtures
- widen scope beyond the four approved consumer profiles
- define a capability that implies runtime authority
- define a capability that implies mutation authority
- treat packet content as runtime world state
- treat Water Mill design-asset context as observed built authority without accepted evidence
- present derived output as canonical replacement
- silently introduce a second dossier
- convert the capability layer into tooling, validators, scripts, mirrors, or runtime-coupled behavior
