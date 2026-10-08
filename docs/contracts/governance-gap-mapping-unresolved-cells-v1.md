# Governance Gap Mapping: Unresolved Conformance-Matrix Cells v1

## Operational Read

- Status: `documentation-only`
- Authority: none granted; this document is a gap map, not a policy instrument
- Frozen baseline HEAD: `419952b`
- Validator source: `FixtureConformanceValidator.initializeRules()` at HEAD `419952b`
- Companion evaluation: `ueia-unresolved-cell-strategy-evaluation-v1.md`
- Conformance spec: `ueia-fixture-consumer-conformance-spec-v1.md`

## Purpose

This document maps the 8 unresolved conformance-matrix cells to their gap reasons. It records which profile-fixture combinations have no frozen rule in `FixtureConformanceValidator.initializeRules()` and why each cell remains unresolved.

This document does not propose resolutions, infer classifications, expand vocabulary, or authorize implementation.

## Conformance Matrix Overview

The matrix spans 4 consumer profiles × 4 fixture roles = 16 cells total.

- **8 cells have explicit frozen rules** (Examples 1–8 from `fixture-consumer-conformance-examples-v1.md`)
- **8 cells return `Optional.empty()`** (no frozen rule exists)

## Resolved Cells (for reference)

| # | Profile | Fixture Role | Classification | Source |
|---|---------|-------------|---------------|--------|
| 1 | FIXTURE_READER | ARCHIVE_RECORD | ALLOWED_WITH_STOP_RULES | Example 1 |
| 2 | FIXTURE_COMPARATOR | AGENT_BRIEF | ALLOWED | Example 2 |
| 3 | FIXTURE_EXPORT_CONSUMER | REVIEW_DECISION | ALLOWED_WITH_STOP_RULES | Example 3 |
| 4 | FIXTURE_DIAGNOSTICS_CONSUMER | DERIVED_PACKET | ALLOWED_WITH_STOP_RULES | Example 4 |
| 5 | FIXTURE_READER | REVIEW_DECISION | ALLOWED_WITH_STOP_RULES | Example 5 |
| 6 | FIXTURE_COMPARATOR | ARCHIVE_RECORD | ALLOWED_WITH_STOP_RULES | Example 6 |
| 7 | FIXTURE_EXPORT_CONSUMER | DERIVED_PACKET | ALLOWED_WITH_STOP_RULES | Example 7 |
| 8 | FIXTURE_DIAGNOSTICS_CONSUMER | AGENT_BRIEF | ALLOWED_WITH_STOP_RULES | Example 8 |

## Unresolved Cells

### Cell U1: FIXTURE_READER + DERIVED_PACKET

- **Validator return**: `Optional.empty()`
- **Test coverage**: `testUnresolvedCellReaderDerivedPacket()`
- **Gap reason**: No frozen example defines reader behavior against derived packets. The packet/world-state separation boundary applies (packets are interpretive artifacts, never runtime world state), but there is no explicit frozen rule governing read-only reader access to packet content.
- **Relevant ladder seams**: Seam 7 (canonical fixture contract), Seam 9 (fixture consumer conformance)
- **Blocking authority absence**: No frozen authority assigns a classification for this interaction.

### Cell U2: FIXTURE_READER + AGENT_BRIEF

- **Validator return**: `Optional.empty()`
- **Test coverage**: `testUnresolvedCellReaderAgentBrief()`
- **Gap reason**: No frozen example defines reader behavior against agent briefs. Reader profile is defined in Examples 1 and 5 against ARCHIVE_RECORD and REVIEW_DECISION respectively, but no example extends to AGENT_BRIEF.
- **Relevant ladder seams**: Seam 8 (fixture consumer profiles), Seam 9 (fixture consumer conformance)
- **Blocking authority absence**: No frozen authority assigns a classification for this interaction.

### Cell U3: FIXTURE_COMPARATOR + DERIVED_PACKET

- **Validator return**: `Optional.empty()`
- **Test coverage**: `testUnresolvedCellComparatorDerivedPacket()`
- **Gap reason**: No frozen example defines comparator behavior against derived packets. Comparator profile is defined in Examples 2 and 6 against AGENT_BRIEF and ARCHIVE_RECORD respectively. Packet/world-state separation applies but no explicit comparison rule exists for packet content.
- **Relevant ladder seams**: Seam 7 (canonical fixture contract), Seam 9 (fixture consumer conformance)
- **Blocking authority absence**: No frozen authority assigns a classification for this interaction.

### Cell U4: FIXTURE_COMPARATOR + REVIEW_DECISION

- **Validator return**: `Optional.empty()`
- **Test coverage**: `testUnresolvedCellComparatorReviewDecision()`
- **Gap reason**: No frozen example defines comparator behavior against review decisions. The pre-decision boundary (review decisions are pre-decision artifacts, not execution approval) applies, but no explicit comparison rule exists for review decision content.
- **Relevant ladder seams**: Seam 9 (fixture consumer conformance), Seam 17 (human review intake)
- **Blocking authority absence**: No frozen authority assigns a classification for this interaction.

### Cell U5: FIXTURE_DIAGNOSTICS_CONSUMER + ARCHIVE_RECORD

- **Validator return**: `Optional.empty()`
- **Test coverage**: `testUnresolvedCellDiagnosticsArchiveRecord()`
- **Gap reason**: No frozen example defines diagnostics consumer behavior against archive records. Diagnostics consumer is defined in Examples 4 and 8 against DERIVED_PACKET and AGENT_BRIEF respectively. Archive records carry provenance and canonical-ID integrity requirements but no explicit diagnostic rule exists for them.
- **Relevant ladder seams**: Seam 6 (promoted canonical specimens), Seam 11 (fixture consumer diagnostic output)
- **Blocking authority absence**: No frozen authority assigns a classification for this interaction.

### Cell U6: FIXTURE_DIAGNOSTICS_CONSUMER + REVIEW_DECISION

- **Validator return**: `Optional.empty()`
- **Test coverage**: `testUnresolvedCellDiagnosticsReviewDecision()`
- **Gap reason**: No frozen example defines diagnostics consumer behavior against review decisions. The pre-decision boundary applies, but no explicit diagnostic rule exists for review decision content.
- **Relevant ladder seams**: Seam 9 (fixture consumer conformance), Seam 11 (fixture consumer diagnostic output)
- **Blocking authority absence**: No frozen authority assigns a classification for this interaction.

### Cell U7: FIXTURE_EXPORT_CONSUMER + ARCHIVE_RECORD

- **Validator return**: `Optional.empty()`
- **Test coverage**: `testUnresolvedCellExportArchiveRecord()`
- **Gap reason**: No frozen example defines export consumer behavior against archive records. Export consumer is defined in Examples 3 and 7 against REVIEW_DECISION and DERIVED_PACKET respectively. Archive records carry provenance integrity that export must preserve, but no explicit export rule exists.
- **Relevant ladder seams**: Seam 6 (promoted canonical specimens), Seam 9 (fixture consumer conformance)
- **Blocking authority absence**: No frozen authority assigns a classification for this interaction.

### Cell U8: FIXTURE_EXPORT_CONSUMER + AGENT_BRIEF

- **Validator return**: `Optional.empty()`
- **Test coverage**: `testUnresolvedCellExportAgentBrief()`
- **Gap reason**: No frozen example defines export consumer behavior against agent briefs. The single-dossier continuity rule applies (agent brief maintains single-dossier boundary), but no explicit export rule exists for brief content.
- **Relevant ladder seams**: Seam 3 (single-dossier bootstrap), Seam 9 (fixture consumer conformance)
- **Blocking authority absence**: No frozen authority assigns a classification for this interaction.

## Gap Pattern Summary

| Gap pattern | Affected cells | Count |
|-------------|---------------|-------|
| Profile has no frozen rule for this fixture type | All 8 | 8 |
| Relevant governance boundaries exist but no explicit rule maps them to a classification | All 8 | 8 |
| Would require new frozen authority to resolve | All 8 | 8 |

Each profile has exactly 2 resolved cells and 2 unresolved cells in the 4×4 matrix. The unresolved cells are evenly distributed.

## What This Document Does Not Authorize

- No unresolved-cell resolution or classification assignment
- No vocabulary expansion beyond ALLOWED, ALLOWED_WITH_STOP_RULES, NOT_ALLOWED
- No validator code changes
- No consuming-seam design or implementation
- No inference of what classifications should be
- No runtime, integration, or toolchain work
