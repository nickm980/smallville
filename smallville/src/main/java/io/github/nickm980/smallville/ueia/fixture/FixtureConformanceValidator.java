package io.github.nickm980.smallville.ueia.fixture;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Validates fixture consumer profile conformance against frozen rules.
 * 
 * This validator encodes only the explicitly frozen conformance rules from:
 * - ueia-fixture-consumer-conformance-spec-v1.md
 * - fixture-consumer-conformance-examples-v1.md (Examples 1-12)
 * 
 * For profile-fixture combinations with no frozen rule, returns Optional.empty().
 * Does not infer or guess for unresolved cells.
 */
public final class FixtureConformanceValidator {

    private static final Map<ProfileFixturePair, ConformanceRule> EXPLICIT_RULES = initializeRules();

    private FixtureConformanceValidator() {
    }

    /**
     * Validates conformance for a profile consuming a fixture.
     * 
     * @param profile the consumer profile
     * @param fixtureRole the fixture role being consumed
     * @return validation result with optional classification and frozen source reference
     */
    public static ConformanceValidationResult validate(
            FixtureConsumerProfile profile,
            CanonicalFixtures.FixtureRole fixtureRole) {

        Objects.requireNonNull(profile, "profile must not be null");
        Objects.requireNonNull(fixtureRole, "fixtureRole must not be null");

        ProfileFixturePair pair = new ProfileFixturePair(profile, fixtureRole);
        ConformanceRule rule = EXPLICIT_RULES.get(pair);

        if (rule != null) {
            return new ConformanceValidationResult(
                profile,
                fixtureRole,
                Optional.of(rule.classification),
                rule.frozenSourceReference
            );
        } else {
            return new ConformanceValidationResult(
                profile,
                fixtureRole,
                Optional.empty(),
                "No explicitly frozen rule"
            );
        }
    }

    // ============================================================================
    // Frozen Conformance Rules (from fixture-consumer-conformance-examples-v1.md)
    // ============================================================================

    private static Map<ProfileFixturePair, ConformanceRule> initializeRules() {
        Map<ProfileFixturePair, ConformanceRule> rules = new HashMap<>();

        // Example 1: Fixture Reader + Archive Record
        rules.put(
            new ProfileFixturePair(FixtureConsumerProfile.FIXTURE_READER, CanonicalFixtures.FixtureRole.ARCHIVE_RECORD),
            new ConformanceRule(
                FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES,
                "fixture-consumer-conformance-examples-v1.md, Example 1: reader remains read-only, preserves Water Mill as design-asset-derived context only"
            )
        );

        // Example 2: Fixture Comparator + Agent Brief
        rules.put(
            new ProfileFixturePair(FixtureConsumerProfile.FIXTURE_COMPARATOR, CanonicalFixtures.FixtureRole.AGENT_BRIEF),
            new ConformanceRule(
                FixtureConformanceClassification.ALLOWED,
                "fixture-consumer-conformance-examples-v1.md, Example 2: comparison stays inside canonical text, preserves single-dossier continuity, does not mutate"
            )
        );

        // Example 3: Fixture Export Consumer + Review Decision
        rules.put(
            new ProfileFixturePair(FixtureConsumerProfile.FIXTURE_EXPORT_CONSUMER, CanonicalFixtures.FixtureRole.REVIEW_DECISION),
            new ConformanceRule(
                FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES,
                "fixture-consumer-conformance-examples-v1.md, Example 3: export remains downstream only, preserves approval boundary"
            )
        );

        // Example 4: Fixture Diagnostics Consumer + Derived Packet
        rules.put(
            new ProfileFixturePair(FixtureConsumerProfile.FIXTURE_DIAGNOSTICS_CONSUMER, CanonicalFixtures.FixtureRole.DERIVED_PACKET),
            new ConformanceRule(
                FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES,
                "fixture-consumer-conformance-examples-v1.md, Example 4: diagnostics remain external and non-authoritative, consumer stops short of mutation"
            )
        );

        // Example 5: Fixture Reader + Review Decision
        rules.put(
            new ProfileFixturePair(FixtureConsumerProfile.FIXTURE_READER, CanonicalFixtures.FixtureRole.REVIEW_DECISION),
            new ConformanceRule(
                FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES,
                "fixture-consumer-conformance-examples-v1.md, Example 5: reader preserves approval boundary instead of inflating it into execution authority"
            )
        );

        // Example 6: Fixture Comparator + Archive Record (stop case)
        rules.put(
            new ProfileFixturePair(FixtureConsumerProfile.FIXTURE_COMPARATOR, CanonicalFixtures.FixtureRole.ARCHIVE_RECORD),
            new ConformanceRule(
                FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES,
                "fixture-consumer-conformance-examples-v1.md, Example 6: stop-and-report required if canonical ID or provenance link is changed/dropped"
            )
        );

        // Example 7: Fixture Export Consumer + Derived Packet (stop case)
        rules.put(
            new ProfileFixturePair(FixtureConsumerProfile.FIXTURE_EXPORT_CONSUMER, CanonicalFixtures.FixtureRole.DERIVED_PACKET),
            new ConformanceRule(
                FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES,
                "fixture-consumer-conformance-examples-v1.md, Example 7: stop-and-report required if export frames packet as runtime world state"
            )
        );

        // Example 8: Fixture Diagnostics Consumer + Agent Brief (stop case)
        rules.put(
            new ProfileFixturePair(FixtureConsumerProfile.FIXTURE_DIAGNOSTICS_CONSUMER, CanonicalFixtures.FixtureRole.AGENT_BRIEF),
            new ConformanceRule(
                FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES,
                "fixture-consumer-conformance-examples-v1.md, Example 8: stop-and-report required if diagnostics flatten open-question/inferred content into observed"
            )
        );

        return Map.copyOf(rules);
    }

    // ============================================================================
    // Helper types
    // ============================================================================

    private record ProfileFixturePair(FixtureConsumerProfile profile, CanonicalFixtures.FixtureRole fixtureRole) {
    }

    private static class ConformanceRule {
        final FixtureConformanceClassification classification;
        final String frozenSourceReference;

        ConformanceRule(FixtureConformanceClassification classification, String frozenSourceReference) {
            this.classification = classification;
            this.frozenSourceReference = frozenSourceReference;
        }
    }
}
