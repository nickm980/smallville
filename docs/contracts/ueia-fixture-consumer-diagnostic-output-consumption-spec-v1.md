# UEIA Diagnostic Output Consumption Spec v1

## Purpose

This specification defines the approved read-only consumption model for already-derived diagnostic outputs within the frozen UEIA fixture governance ladder. It is a consumption-governance layer only. It does not define tooling, validators, scripts, schemas, packaging, mirrors, runtime coupling, or implementation behavior.

## Baseline References

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
- Fixture consumer capability crosswalk contract freeze commit: `69c7332e0271e9eb905579a2e13db7a0cbd7eb80`
- Fixture consumer capability crosswalk example freeze commit: `b18819e64266ba14251f8353389a629cca8240a7`
- Diagnostic-output contracts freeze commit: `b9c48a431e7941c838525b5dd12d7a80eafbd0b3`
- Diagnostic-output examples freeze commit: `9483dea2e7feb17a95482f261f31d77ce01da56c`

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

## Capability Scope

This layer depends only on the frozen capability-crosswalk authority. `export-diagnostics` remains a capability name from the frozen capability layer and is not a sixth diagnostic output family.

## Consumption Model

Producing outputs means deriving an approved diagnostic output family from canonical fixtures under an approved read-only capability.

Consuming outputs means read-only downstream use of an already-derived diagnostic output without changing its authority level.

Interpreting outputs means assigning bounded review meaning to a diagnostic output without elevating it into source truth, runtime state, or execution approval.

Stopping on outputs means halting further downstream consumption when a `stop-report` is present or when a consumed output reveals a triggered stop-rule condition.

## Source-of-Truth Rule

Canonical fixture files at their canonical paths remain the source of truth. Consumed diagnostic outputs may support review, comparison, downstream reading, or bounded handoff, but they may not replace, override, mirror, normalize, or supersede canonical fixture text.

## Non-Authoritative Consumption Rule

All consumed diagnostic outputs remain derived only, non-authoritative, and non-replacing. Consumption does not upgrade authority, create runtime rights, or weaken upstream stop-rule boundaries.

## Boundary Preservation Rules

- Water Mill remains design-asset-derived context only and may not be restated as observed built authority without accepted evidence.
- The promoted derived packet remains an interpretive artifact only and never runtime world state.
- Single-dossier continuity remains explicit and may not be weakened by consumption wording.
- Consumed outputs remain non-authoritative and non-replacing even when read by an approved consumer profile.

## No-Implementation Boundary

This specification does not authorize tooling, validator logic, script logic, schema work, packaging design, mirror design, access-control implementation, or runtime behavior.

## No-Runtime-Coupling Boundary

This specification does not authorize runtime-facing ingestion, runtime state creation, runtime approval, runtime binding, or runtime mutation through consumed diagnostic outputs.

## No-Mutation Boundary

This specification does not authorize fixture mutation, canonical rewrite, canonical replacement, inferred-to-observed promotion, downstream correction authority, or silent uncertainty repair.

## Support Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Read-only consumption of approved diagnostic output families | Supported | Limited to the five frozen output families |
| Read-only interpretation rules for consumed outputs | Supported | Bounded review meaning only |
| Stop-and-report consumption rules | Supported | Must preserve upstream stop overlays |
| Tooling design | Not supported | Deferred |
| Validator design | Not supported | Deferred |
| Packaging or transport design | Not supported | Deferred |
| Runtime coupling | Not supported | Forbidden in this phase |
| Canonical replacement behavior | Not supported | Forbidden in this phase |

## Explicit Stop Conditions

Stop if any drafting or later pass:

- widens scope beyond the four canonical fixtures
- widens scope beyond the four approved consumer profiles
- widens scope beyond the five frozen output families
- implies runtime authority
- implies mutation authority
- implies execution authority
- implies canonical replacement
- treats packet content as runtime world state
- treats Water Mill design-asset-derived context as observed built authority without accepted evidence
- weakens single-dossier continuity
- converts `diagnostics-bundle` into package semantics
- turns `stop-report` into advisory-only wording
- collapses `summary` and `export-summary` into one indistinct authority class
- drifts into tooling, validators, scripts, schemas, mirrors, packaging, or runtime-coupled behavior
