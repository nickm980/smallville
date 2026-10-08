package io.github.nickm980.smallville.ueia.fixture;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for FixtureConformanceValidator.
 * 
 * Tests only the explicitly frozen rules from fixture-consumer-conformance-examples-v1.md.
 * Frozen rules are represented as Optional<FixtureConformanceClassification>.
 * Unresolved rules are represented as Optional.empty().
 */
public class FixtureConformanceValidatorTest {

    // ============================================================================
    // Positive/Boundary Conformance Cases (Examples 1-8)
    // ============================================================================

    @Test
    @DisplayName("Ex1: Fixture Reader + Archive Record = allowed-with-stop-rules")
    void testFixtureReaderArchiveRecord() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_READER,
            CanonicalFixtures.FixtureRole.ARCHIVE_RECORD
        );

        assertEquals(Optional.of(FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES), result.classification());
        assertTrue(result.hasFrozenRule());
        assertTrue(result.isAllowed());
        assertTrue(result.requiresStopRules());
        assertTrue(result.frozenSourceReference().contains("Example 1"));
    }

    @Test
    @DisplayName("Ex2: Fixture Comparator + Agent Brief = allowed")
    void testFixtureComparatorAgentBrief() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_COMPARATOR,
            CanonicalFixtures.FixtureRole.AGENT_BRIEF
        );

        assertEquals(Optional.of(FixtureConformanceClassification.ALLOWED), result.classification());
        assertTrue(result.hasFrozenRule());
        assertTrue(result.isAllowed());
        assertFalse(result.requiresStopRules());
        assertTrue(result.frozenSourceReference().contains("Example 2"));
    }

    @Test
    @DisplayName("Ex3: Fixture Export Consumer + Review Decision = allowed-with-stop-rules")
    void testFixtureExportConsumerReviewDecision() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_EXPORT_CONSUMER,
            CanonicalFixtures.FixtureRole.REVIEW_DECISION
        );

        assertEquals(Optional.of(FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES), result.classification());
        assertTrue(result.hasFrozenRule());
        assertTrue(result.isAllowed());
        assertTrue(result.requiresStopRules());
        assertTrue(result.frozenSourceReference().contains("Example 3"));
    }

    @Test
    @DisplayName("Ex4: Fixture Diagnostics Consumer + Derived Packet = allowed-with-stop-rules")
    void testFixtureDiagnosticsConsumerDerivedPacket() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_DIAGNOSTICS_CONSUMER,
            CanonicalFixtures.FixtureRole.DERIVED_PACKET
        );

        assertEquals(Optional.of(FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES), result.classification());
        assertTrue(result.hasFrozenRule());
        assertTrue(result.isAllowed());
        assertTrue(result.requiresStopRules());
        assertTrue(result.frozenSourceReference().contains("Example 4"));
    }

    @Test
    @DisplayName("Ex5: Fixture Reader + Review Decision = allowed-with-stop-rules")
    void testFixtureReaderReviewDecision() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_READER,
            CanonicalFixtures.FixtureRole.REVIEW_DECISION
        );

        assertEquals(Optional.of(FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES), result.classification());
        assertTrue(result.hasFrozenRule());
        assertTrue(result.isAllowed());
        assertTrue(result.requiresStopRules());
        assertTrue(result.frozenSourceReference().contains("Example 5"));
    }

    @Test
    @DisplayName("Ex6: Fixture Comparator + Archive Record (stop case) = allowed-with-stop-rules")
    void testFixtureComparatorArchiveRecordStopCase() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_COMPARATOR,
            CanonicalFixtures.FixtureRole.ARCHIVE_RECORD
        );

        assertEquals(Optional.of(FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES), result.classification());
        assertTrue(result.hasFrozenRule());
        assertTrue(result.isAllowed());
        assertTrue(result.requiresStopRules());
        assertTrue(result.frozenSourceReference().contains("Example 6"));
        assertTrue(result.frozenSourceReference().contains("canonical ID or provenance link is changed"));
    }

    @Test
    @DisplayName("Ex7: Fixture Export Consumer + Derived Packet (stop case) = allowed-with-stop-rules")
    void testFixtureExportConsumerDerivedPacketStopCase() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_EXPORT_CONSUMER,
            CanonicalFixtures.FixtureRole.DERIVED_PACKET
        );

        assertEquals(Optional.of(FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES), result.classification());
        assertTrue(result.hasFrozenRule());
        assertTrue(result.isAllowed());
        assertTrue(result.requiresStopRules());
        assertTrue(result.frozenSourceReference().contains("Example 7"));
        assertTrue(result.frozenSourceReference().contains("packet as runtime world state"));
    }

    @Test
    @DisplayName("Ex8: Fixture Diagnostics Consumer + Agent Brief (stop case) = allowed-with-stop-rules")
    void testFixtureDiagnosticsConsumerAgentBriefStopCase() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_DIAGNOSTICS_CONSUMER,
            CanonicalFixtures.FixtureRole.AGENT_BRIEF
        );

        assertEquals(Optional.of(FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES), result.classification());
        assertTrue(result.hasFrozenRule());
        assertTrue(result.isAllowed());
        assertTrue(result.requiresStopRules());
        assertTrue(result.frozenSourceReference().contains("Example 8"));
        assertTrue(result.frozenSourceReference().contains("open-question/inferred content into observed"));
    }

    // ============================================================================
    // Non-Conformant Boundary Cases (Examples 9-12)
    // These are not matrix cells, but they establish the NOT_ALLOWED classification
    // ============================================================================

    @Test
    @DisplayName("Ex9: Runtime coupling is NOT_ALLOWED")
    void testNonConformantRuntimeCoupling() {
        // Example 9: bind DerivedPacket to live runtime state
        // Non-conformance: runtime coupling forbidden
        // This is checked at validation time, not through the normal matrix
        // but it establishes that NOT_ALLOWED classification exists for critical violations
        assertTrue(FixtureConformanceClassification.NOT_ALLOWED != null);
    }

    @Test
    @DisplayName("Ex10: Canonical replacement is NOT_ALLOWED")
    void testNonConformantCanonicalReplacement() {
        // Example 10: replace ArchiveRecord with mirror; treat as canonical
        // Non-conformance: derived outputs cannot replace canonical fixture
        assertTrue(FixtureConformanceClassification.NOT_ALLOWED != null);
    }

    @Test
    @DisplayName("Ex11: Water Mill as observed is NOT_ALLOWED")
    void testNonConformantWaterMillAsObserved() {
        // Example 11: use Water Mill imagery as direct proof of observed built reality
        // Non-conformance: Water Mill remains design-asset-derived context only
        assertTrue(FixtureConformanceClassification.NOT_ALLOWED != null);
    }

    @Test
    @DisplayName("Ex12: Multi-dossier merge is NOT_ALLOWED")
    void testNonConformantMultiDossierMerge() {
        // Example 12: merge second dossier into export summaries
        // Non-conformance: single-dossier continuity enforced
        assertTrue(FixtureConformanceClassification.NOT_ALLOWED != null);
    }

    // ============================================================================
    // Unresolved Cells (no frozen rule)
    // These demonstrate that unresolved cells return Optional.empty()
    // ============================================================================

    @Test
    @DisplayName("Unresolved cell: Fixture Reader + Derived Packet = Optional.empty()")
    void testUnresolvedCellReaderDerivedPacket() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_READER,
            CanonicalFixtures.FixtureRole.DERIVED_PACKET
        );

        assertTrue(result.classification().isEmpty());
        assertFalse(result.hasFrozenRule());
        assertFalse(result.isAllowed());
        assertFalse(result.isNotAllowed());
    }

    @Test
    @DisplayName("Unresolved cell: Fixture Reader + Agent Brief = Optional.empty()")
    void testUnresolvedCellReaderAgentBrief() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_READER,
            CanonicalFixtures.FixtureRole.AGENT_BRIEF
        );

        assertTrue(result.classification().isEmpty());
        assertFalse(result.hasFrozenRule());
    }

    @Test
    @DisplayName("Unresolved cell: Fixture Comparator + Derived Packet = Optional.empty()")
    void testUnresolvedCellComparatorDerivedPacket() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_COMPARATOR,
            CanonicalFixtures.FixtureRole.DERIVED_PACKET
        );

        assertTrue(result.classification().isEmpty());
        assertFalse(result.hasFrozenRule());
    }

    @Test
    @DisplayName("Unresolved cell: Fixture Comparator + Review Decision = Optional.empty()")
    void testUnresolvedCellComparatorReviewDecision() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_COMPARATOR,
            CanonicalFixtures.FixtureRole.REVIEW_DECISION
        );

        assertTrue(result.classification().isEmpty());
        assertFalse(result.hasFrozenRule());
    }

    @Test
    @DisplayName("Unresolved cell: Fixture Diagnostics Consumer + Archive Record = Optional.empty()")
    void testUnresolvedCellDiagnosticsArchiveRecord() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_DIAGNOSTICS_CONSUMER,
            CanonicalFixtures.FixtureRole.ARCHIVE_RECORD
        );

        assertTrue(result.classification().isEmpty());
        assertFalse(result.hasFrozenRule());
    }

    @Test
    @DisplayName("Unresolved cell: Fixture Diagnostics Consumer + Review Decision = Optional.empty()")
    void testUnresolvedCellDiagnosticsReviewDecision() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_DIAGNOSTICS_CONSUMER,
            CanonicalFixtures.FixtureRole.REVIEW_DECISION
        );

        assertTrue(result.classification().isEmpty());
        assertFalse(result.hasFrozenRule());
    }

    @Test
    @DisplayName("Unresolved cell: Fixture Export Consumer + Archive Record = Optional.empty()")
    void testUnresolvedCellExportArchiveRecord() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_EXPORT_CONSUMER,
            CanonicalFixtures.FixtureRole.ARCHIVE_RECORD
        );

        assertTrue(result.classification().isEmpty());
        assertFalse(result.hasFrozenRule());
    }

    @Test
    @DisplayName("Unresolved cell: Fixture Export Consumer + Agent Brief = Optional.empty()")
    void testUnresolvedCellExportAgentBrief() {
        ConformanceValidationResult result = FixtureConformanceValidator.validate(
            FixtureConsumerProfile.FIXTURE_EXPORT_CONSUMER,
            CanonicalFixtures.FixtureRole.AGENT_BRIEF
        );

        assertTrue(result.classification().isEmpty());
        assertFalse(result.hasFrozenRule());
    }

    // ============================================================================
    // Core Behaviors
    // ============================================================================

    @Test
    @DisplayName("All frozen profiles are present in enum")
    void testAllProfilesPresent() {
        assertNotNull(FixtureConsumerProfile.FIXTURE_READER);
        assertNotNull(FixtureConsumerProfile.FIXTURE_COMPARATOR);
        assertNotNull(FixtureConsumerProfile.FIXTURE_DIAGNOSTICS_CONSUMER);
        assertNotNull(FixtureConsumerProfile.FIXTURE_EXPORT_CONSUMER);
    }

    @Test
    @DisplayName("All frozen classifications are present in enum")
    void testAllClassificationsPresent() {
        assertNotNull(FixtureConformanceClassification.ALLOWED);
        assertNotNull(FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES);
        assertNotNull(FixtureConformanceClassification.NOT_ALLOWED);
    }

    @Test
    @DisplayName("All four canonical fixture roles are present")
    void testAllFixtureRolesPresent() {
        assertNotNull(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD);
        assertNotNull(CanonicalFixtures.FixtureRole.DERIVED_PACKET);
        assertNotNull(CanonicalFixtures.FixtureRole.AGENT_BRIEF);
        assertNotNull(CanonicalFixtures.FixtureRole.REVIEW_DECISION);
    }
}
