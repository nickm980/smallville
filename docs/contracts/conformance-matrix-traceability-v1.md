# Conformance Matrix Traceability v1

## Operational Read

- Status: `documentation-only`
- Authority: none granted; this document is a traceability map only
- Frozen baseline HEAD: `419952b`
- Validator source: `FixtureConformanceValidator.initializeRules()`
- Test source: `FixtureConformanceValidatorTest.java`
- Ladder map: `ueia-ladder-phase-map-v1.md`
- Conformance spec: `ueia-fixture-consumer-conformance-spec-v1.md`

## Purpose

This document traces each frozen conformance rule back to its source in the 22-seam governance ladder and forward to its test verification in the validator test suite. It also traces each unresolved cell to the test that asserts its `Optional.empty()` status.

This document does not create new governance authority, change validator behavior, or resolve unresolved cells.

## Traceability Legend

- **Seam**: The governance ladder seam from `ueia-ladder-phase-map-v1.md` that establishes the relevant authority
- **Frozen rule source**: The example from `fixture-consumer-conformance-examples-v1.md` that defines the rule
- **Validator key**: The `ProfileFixturePair` used in `initializeRules()` to register the rule
- **Test method**: The JUnit test that verifies the rule or cell status
- **Classification**: The frozen classification assigned by the validator

## Resolved Rules Traceability

### Rule R1: FIXTURE_READER + ARCHIVE_RECORD

| Attribute | Value |
|-----------|-------|
| Seam | 9 — UEIA fixture consumer conformance |
| Contracts freeze | `302d6bb2ea44d9887c66d6a86a31c45bc9b508fd` |
| Examples freeze | `a5ad8ffda1c5f52d3aca10e325124b013a339390` |
| Frozen rule source | Example 1: "reader remains read-only, preserves Water Mill as design-asset-derived context only" |
| Classification | `ALLOWED_WITH_STOP_RULES` |
| Validator key | `FIXTURE_READER, ARCHIVE_RECORD` |
| Test method | `testFixtureReaderArchiveRecord()` |
| Test assertions | classification = ALLOWED_WITH_STOP_RULES, hasFrozenRule = true, isAllowed = true, requiresStopRules = true, reference contains "Example 1" |

### Rule R2: FIXTURE_COMPARATOR + AGENT_BRIEF

| Attribute | Value |
|-----------|-------|
| Seam | 9 — UEIA fixture consumer conformance |
| Contracts freeze | `302d6bb2ea44d9887c66d6a86a31c45bc9b508fd` |
| Examples freeze | `a5ad8ffda1c5f52d3aca10e325124b013a339390` |
| Frozen rule source | Example 2: "comparison stays inside canonical text, preserves single-dossier continuity, does not mutate" |
| Classification | `ALLOWED` |
| Validator key | `FIXTURE_COMPARATOR, AGENT_BRIEF` |
| Test method | `testFixtureComparatorAgentBrief()` |
| Test assertions | classification = ALLOWED, hasFrozenRule = true, isAllowed = true, requiresStopRules = false, reference contains "Example 2" |

### Rule R3: FIXTURE_EXPORT_CONSUMER + REVIEW_DECISION

| Attribute | Value |
|-----------|-------|
| Seam | 9 — UEIA fixture consumer conformance |
| Contracts freeze | `302d6bb2ea44d9887c66d6a86a31c45bc9b508fd` |
| Examples freeze | `a5ad8ffda1c5f52d3aca10e325124b013a339390` |
| Frozen rule source | Example 3: "export remains downstream only, preserves approval boundary" |
| Classification | `ALLOWED_WITH_STOP_RULES` |
| Validator key | `FIXTURE_EXPORT_CONSUMER, REVIEW_DECISION` |
| Test method | `testFixtureExportConsumerReviewDecision()` |
| Test assertions | classification = ALLOWED_WITH_STOP_RULES, hasFrozenRule = true, isAllowed = true, requiresStopRules = true, reference contains "Example 3" |

### Rule R4: FIXTURE_DIAGNOSTICS_CONSUMER + DERIVED_PACKET

| Attribute | Value |
|-----------|-------|
| Seam | 9 — UEIA fixture consumer conformance |
| Contracts freeze | `302d6bb2ea44d9887c66d6a86a31c45bc9b508fd` |
| Examples freeze | `a5ad8ffda1c5f52d3aca10e325124b013a339390` |
| Frozen rule source | Example 4: "diagnostics remain external and non-authoritative, consumer stops short of mutation" |
| Classification | `ALLOWED_WITH_STOP_RULES` |
| Validator key | `FIXTURE_DIAGNOSTICS_CONSUMER, DERIVED_PACKET` |
| Test method | `testFixtureDiagnosticsConsumerDerivedPacket()` |
| Test assertions | classification = ALLOWED_WITH_STOP_RULES, hasFrozenRule = true, isAllowed = true, requiresStopRules = true, reference contains "Example 4" |

### Rule R5: FIXTURE_READER + REVIEW_DECISION

| Attribute | Value |
|-----------|-------|
| Seam | 9 — UEIA fixture consumer conformance |
| Contracts freeze | `302d6bb2ea44d9887c66d6a86a31c45bc9b508fd` |
| Examples freeze | `a5ad8ffda1c5f52d3aca10e325124b013a339390` |
| Frozen rule source | Example 5: "reader preserves approval boundary instead of inflating it into execution authority" |
| Classification | `ALLOWED_WITH_STOP_RULES` |
| Validator key | `FIXTURE_READER, REVIEW_DECISION` |
| Test method | `testFixtureReaderReviewDecision()` |
| Test assertions | classification = ALLOWED_WITH_STOP_RULES, hasFrozenRule = true, isAllowed = true, requiresStopRules = true, reference contains "Example 5" |

### Rule R6: FIXTURE_COMPARATOR + ARCHIVE_RECORD

| Attribute | Value |
|-----------|-------|
| Seam | 9 — UEIA fixture consumer conformance |
| Contracts freeze | `302d6bb2ea44d9887c66d6a86a31c45bc9b508fd` |
| Examples freeze | `a5ad8ffda1c5f52d3aca10e325124b013a339390` |
| Frozen rule source | Example 6: "stop-and-report required if canonical ID or provenance link is changed/dropped" |
| Classification | `ALLOWED_WITH_STOP_RULES` |
| Validator key | `FIXTURE_COMPARATOR, ARCHIVE_RECORD` |
| Test method | `testFixtureComparatorArchiveRecordStopCase()` |
| Test assertions | classification = ALLOWED_WITH_STOP_RULES, hasFrozenRule = true, isAllowed = true, requiresStopRules = true, reference contains "Example 6", reference contains "canonical ID or provenance link is changed" |

### Rule R7: FIXTURE_EXPORT_CONSUMER + DERIVED_PACKET

| Attribute | Value |
|-----------|-------|
| Seam | 9 — UEIA fixture consumer conformance |
| Contracts freeze | `302d6bb2ea44d9887c66d6a86a31c45bc9b508fd` |
| Examples freeze | `a5ad8ffda1c5f52d3aca10e325124b013a339390` |
| Frozen rule source | Example 7: "stop-and-report required if export frames packet as runtime world state" |
| Classification | `ALLOWED_WITH_STOP_RULES` |
| Validator key | `FIXTURE_EXPORT_CONSUMER, DERIVED_PACKET` |
| Test method | `testFixtureExportConsumerDerivedPacketStopCase()` |
| Test assertions | classification = ALLOWED_WITH_STOP_RULES, hasFrozenRule = true, isAllowed = true, requiresStopRules = true, reference contains "Example 7", reference contains "packet as runtime world state" |

### Rule R8: FIXTURE_DIAGNOSTICS_CONSUMER + AGENT_BRIEF

| Attribute | Value |
|-----------|-------|
| Seam | 9 — UEIA fixture consumer conformance |
| Contracts freeze | `302d6bb2ea44d9887c66d6a86a31c45bc9b508fd` |
| Examples freeze | `a5ad8ffda1c5f52d3aca10e325124b013a339390` |
| Frozen rule source | Example 8: "stop-and-report required if diagnostics flatten open-question/inferred content into observed" |
| Classification | `ALLOWED_WITH_STOP_RULES` |
| Validator key | `FIXTURE_DIAGNOSTICS_CONSUMER, AGENT_BRIEF` |
| Test method | `testFixtureDiagnosticsConsumerAgentBriefStopCase()` |
| Test assertions | classification = ALLOWED_WITH_STOP_RULES, hasFrozenRule = true, isAllowed = true, requiresStopRules = true, reference contains "Example 8", reference contains "open-question/inferred content into observed" |

## Boundary Rules Traceability (Examples 9–12)

These examples establish the `NOT_ALLOWED` classification as cross-cutting prohibitions, not as specific matrix cell rules.

| Example | Prohibition | Test method | Governing principle |
|---------|------------|-------------|-------------------|
| 9 | Runtime coupling: bind DerivedPacket to live runtime state | `testNonConformantRuntimeCoupling()` | No-Runtime-Coupling Boundary (conformance spec) |
| 10 | Canonical replacement: replace ArchiveRecord with mirror | `testNonConformantCanonicalReplacement()` | Source-Of-Truth Rule (conformance spec) |
| 11 | Water Mill as observed: treat design-asset context as built reality | `testNonConformantWaterMillAsObserved()` | Water Mill design-asset boundary (conformance spec) |
| 12 | Multi-dossier merge: merge second dossier into exports | `testNonConformantMultiDossierMerge()` | Single-dossier continuity (Seam 3) |

## Unresolved Cells Traceability

Each unresolved cell is verified by a test asserting `Optional.empty()`.

| Cell | Profile | Fixture Role | Test method | Asserts |
|------|---------|-------------|-------------|---------|
| U1 | FIXTURE_READER | DERIVED_PACKET | `testUnresolvedCellReaderDerivedPacket()` | classification empty, hasFrozenRule false, isAllowed false, isNotAllowed false |
| U2 | FIXTURE_READER | AGENT_BRIEF | `testUnresolvedCellReaderAgentBrief()` | classification empty, hasFrozenRule false |
| U3 | FIXTURE_COMPARATOR | DERIVED_PACKET | `testUnresolvedCellComparatorDerivedPacket()` | classification empty, hasFrozenRule false |
| U4 | FIXTURE_COMPARATOR | REVIEW_DECISION | `testUnresolvedCellComparatorReviewDecision()` | classification empty, hasFrozenRule false |
| U5 | FIXTURE_DIAGNOSTICS_CONSUMER | ARCHIVE_RECORD | `testUnresolvedCellDiagnosticsArchiveRecord()` | classification empty, hasFrozenRule false |
| U6 | FIXTURE_DIAGNOSTICS_CONSUMER | REVIEW_DECISION | `testUnresolvedCellDiagnosticsReviewDecision()` | classification empty, hasFrozenRule false |
| U7 | FIXTURE_EXPORT_CONSUMER | ARCHIVE_RECORD | `testUnresolvedCellExportArchiveRecord()` | classification empty, hasFrozenRule false |
| U8 | FIXTURE_EXPORT_CONSUMER | AGENT_BRIEF | `testUnresolvedCellExportAgentBrief()` | classification empty, hasFrozenRule false |

## Ladder Seam Coverage

All 8 resolved rules trace to **Seam 9** (UEIA fixture consumer conformance) as their governing authority seam. The underlying fixtures they operate on are established by earlier seams:

| Seam | Role in conformance traceability |
|------|--------------------------------|
| 3 | Single-dossier bootstrap — establishes continuity boundary referenced by Ex2, Ex12 |
| 5 | Bootstrap promotion pack — establishes promoted specimen foundation |
| 6 | Promoted canonical specimens — establishes canonical fixture source material |
| 7 | Canonical fixture contract — establishes the 4 fixture roles and canonical paths |
| 8 | Fixture consumer profiles — establishes the 4 approved consumer profiles |
| 9 | Fixture consumer conformance — establishes all 8 explicit conformance rules (Examples 1–8) |
| 11 | Fixture consumer diagnostic output — establishes diagnostic output families (relevant to U5, U6) |

## Test Suite Summary

| Category | Tests | Count |
|----------|-------|-------|
| Resolved rules (Examples 1–8) | 8 positive/boundary tests | 8 |
| Boundary prohibitions (Examples 9–12) | 4 NOT_ALLOWED existence tests | 4 |
| Unresolved cells | 8 Optional.empty() assertion tests | 8 |
| Core behaviors | Profile enum, null handling, etc. | 3 |
| **Total** | | **23** |

All 23 tests pass at HEAD `419952b`.

## What This Document Does Not Authorize

- No validator code changes
- No unresolved-cell resolution
- No new governance seams or authority
- No consuming-seam design or implementation
- No vocabulary expansion
