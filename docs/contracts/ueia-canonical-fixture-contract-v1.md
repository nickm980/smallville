# UEIA Canonical Fixture Contract v1

## Operational Read

- Status: `specification-only`
- Fixture scope: `immutable canonical references only`
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

## Purpose

This contract defines how future tools, tests, and bounded review agents may consume the promoted canonical specimens as immutable canonical fixtures.

It exists to preserve:
- provenance clarity
- single-dossier continuity
- packet/world-state separation
- Water Mill design-asset boundary discipline
- non-executing fixture consumption

## Canonical Fixture Set In Scope

Only these canonical fixtures are in scope:
- [`promoted-archive-record-v1.md`](C:/SmallVille/docs/examples/promoted-archive-record-v1.md)
- [`promoted-derived-packet-v1.md`](C:/SmallVille/docs/examples/promoted-derived-packet-v1.md)
- [`promoted-agent-brief-v1.md`](C:/SmallVille/docs/examples/promoted-agent-brief-v1.md)
- [`promoted-review-decision-v1.md`](C:/SmallVille/docs/examples/promoted-review-decision-v1.md)

No additional fixtures are introduced by this contract.

## Immutable Fixture Rule

Canonical fixtures are immutable reference artifacts.

They may be:
- read
- compared
- normalized into transient memory representations
- checked for schema-aligned sections
- used as fixture inputs for future non-mutating tooling or tests

They must not be:
- edited in place by consumers
- silently repaired
- rewritten to hide evidence gaps
- replaced by inferred or generated substitutes

## Source-Of-Truth Rule

The source of truth for canonical fixture consumption is the frozen file content at the canonical fixture paths.

Consumers must treat:
- the fixture file text
- the cited canonical IDs
- the preserved provenance notes
- the preserved boundary language

as authoritative for fixture use.

Consumers must not treat downstream derived diagnostics as replacements for the canonical fixture source.

## Read-Only Rule

All canonical fixture consumption is read-only.

Consumers may:
- parse
- index
- compare
- report diagnostics
- export derived diagnostics to separate non-canonical outputs

Consumers may not:
- mutate fixture files
- reserialize over fixture files
- promote diagnostics back into fixture source

## No Runtime Coupling Rule

Canonical fixtures must remain decoupled from runtime.

No consumer may:
- bind a fixture to live Smallville runtime state
- treat a packet fixture as runtime world state
- treat a review decision fixture as execution approval
- couple Water Mill design-asset context to scene or world mutation

## No Mutation Rule

No consumer gains mutation authority through this contract.

This includes:
- file mutation
- runtime mutation
- authority mutation through relabeling inferred facts as observed
- hidden mutation through auto-normalization that writes back to source

## Support Matrix

### Supported

- immutable reading of the four canonical fixtures
- schema-aligned content checks
- canonical ID checks
- provenance-preservation checks
- fact-status discipline checks
- packet/world-state separation checks
- Water Mill design-asset boundary checks
- single-dossier continuity checks
- derived diagnostics emitted outside the canonical fixture files

### Unsupported

- runtime coupling
- fixture mutation
- auto-repair of missing or weak evidence
- promotion of inferred content to observed
- second-dossier merge
- geometry, mesh or model asset interpretation
- runtime map layers
- renderable scene assets

### Intentionally Deferred

- tooling implementation
- validators or scripts
- runtime-facing fixture consumption
- canonical fixture materialization in alternative formats

## Explicit Stop Conditions

Stop if any future pass tries to:
- mutate a canonical fixture file
- treat a packet fixture as runtime world state
- treat Water Mill design assets as real-world built authority without accepted evidence
- infer away missing provenance or weak fact status
- add a second dossier
- couple fixtures to runtime, connectors, or scene-authority paths
