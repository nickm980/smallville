# UEIA Fixture Consumer Diagnostic Output Spec v1

## Purpose

This document defines the approved read-only diagnostic output shapes for future fixture consumers operating against the frozen UEIA canonical fixture set. This layer is descriptive only. It exists to support future tooling and test design without becoming tooling design, validator design, script design, packaging design, schema work, or runtime coupling.

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

## Fixture Scope

Only these canonical fixtures are in scope:

- `docs/examples/promoted-archive-record-v1.md`
- `docs/examples/promoted-derived-packet-v1.md`
- `docs/examples/promoted-agent-brief-v1.md`
- `docs/examples/promoted-review-decision-v1.md`

No additional fixture family, mirror, or derived substitute is in scope.

## Consumer-Profile Scope

Only these approved consumer profiles are in scope:

- `Fixture Reader`
- `Fixture Comparator`
- `Fixture Diagnostics Consumer`
- `Fixture Export Consumer`

No additional consumer profile is authorized by this document.

## Capability Scope

This layer depends only on the frozen capability-crosswalk authority. Diagnostic outputs may be derived only from the already-approved read-only capability vocabulary and may not introduce new capability classes, implicit execution rights, or runtime-facing permissions.

## Diagnostic Output Scope

This layer defines approved read-only diagnostic output families, approved descriptive output vocabulary, and the boundaries that keep those outputs non-authoritative and non-replacing. It does not define file formats, schemas, validators, packaging, runtime bindings, or implementation behavior.

## Source-of-Truth Rule

Canonical fixture files at their canonical paths remain the sole source of truth for the promoted specimen set. Diagnostic outputs may refer to canonical content, summarize it, compare it, or report boundary risk, but they may not supersede, rewrite, replace, mirror, or weaken the canonical fixture text.

## Non-Authoritative Output Rule

All diagnostic outputs defined in this layer are derived only. They are non-authoritative, non-replacing, and review-oriented. They may surface drift, uncertainty, stop conditions, provenance concerns, or boundary concerns, but they may not create a new authority layer above the canonical fixture text.

## Boundary Preservation Rules

- Water Mill remains design-asset-derived context only and may not be restated as observed built authority without accepted evidence.
- The promoted derived packet remains an interpretive artifact only and never runtime world state.
- Single-dossier continuity remains explicit and may not be weakened by downstream output wording.
- Derived outputs remain non-authoritative and non-replacing even when produced by an approved consumer profile against an allowed capability.

## No-Implementation Boundary

This specification does not authorize tooling, validator logic, script logic, packaging design, schema design, access-control implementation, logging implementation, or runtime behavior.

## No-Runtime-Coupling Boundary

This specification does not authorize runtime-facing ingestion, runtime state generation, runtime mutation, runtime approval, or any coupling between derived diagnostic outputs and Smallville runtime behavior.

## No-Mutation Boundary

This specification does not authorize fixture mutation, canonical rewrite, canonical replacement, inferred-to-observed promotion, silent uncertainty repair, or downstream softening of stop conditions.

## Support Matrix

| Area | Status | Notes |
| --- | --- | --- |
| Read-only diagnostic output family definitions | Supported | Limited to approved output families only |
| Read-only output vocabulary definitions | Supported | Vocabulary only, not schema or implementation |
| Boundary-preserving examples | Supported | Illustrative only |
| Tooling design | Not supported | Deferred |
| Validator design | Not supported | Deferred |
| Packaging or file-format design | Not supported | Deferred |
| Runtime coupling | Not supported | Forbidden in this phase |
| Canonical replacement behavior | Not supported | Forbidden in this phase |

## Explicit Stop Conditions

Stop if any drafting or later pass:

- widens scope beyond the four canonical fixtures
- widens scope beyond the four approved consumer profiles
- defines a diagnostic output in a way that implies runtime authority
- defines a diagnostic output in a way that implies mutation authority
- defines a diagnostic output in a way that implies execution authority
- presents any derived output as canonical replacement
- treats packet content as runtime world state
- treats Water Mill design-asset-derived context as observed built authority without accepted evidence
- introduces a second dossier
- drifts into tooling, validators, scripts, mirrors, schemas, packaging, or runtime-coupled behavior
- weakens earlier frozen stop-rule or conformance boundaries
