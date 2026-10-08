package io.github.nickm980.smallville.ueia.fixture;

import java.util.Objects;
import java.util.Optional;

/**
 * Result of conformance validation for a profile-fixture interaction.
 * 
 * The classification field is Optional: 
 * - If present, a frozen policy rule applies (ALLOWED, ALLOWED_WITH_STOP_RULES, or NOT_ALLOWED)
 * - If empty, no frozen rule exists for this profile-fixture combination
 */
public record ConformanceValidationResult(
    FixtureConsumerProfile profile,
    CanonicalFixtures.FixtureRole fixtureRole,
    Optional<FixtureConformanceClassification> classification,
    String frozenSourceReference
) {
    public ConformanceValidationResult {
        Objects.requireNonNull(profile, "profile must not be null");
        Objects.requireNonNull(fixtureRole, "fixtureRole must not be null");
        Objects.requireNonNull(classification, "classification must not be null");
        Objects.requireNonNull(frozenSourceReference, "frozenSourceReference must not be null");
    }

    /**
     * Returns true if a frozen rule exists for this interaction.
     */
    public boolean hasFrozenRule() {
        return classification.isPresent();
    }

    /**
     * Returns true if this result indicates an allowed interaction (with or without stop-rules).
     * Returns false if no frozen rule exists.
     */
    public boolean isAllowed() {
        return classification.map(c -> 
            c == FixtureConformanceClassification.ALLOWED ||
            c == FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES
        ).orElse(false);
    }

    /**
     * Returns true if this result indicates the interaction is not allowed.
     * Returns false if no frozen rule exists.
     */
    public boolean isNotAllowed() {
        return classification.map(c -> c == FixtureConformanceClassification.NOT_ALLOWED).orElse(false);
    }

    /**
     * Returns true if this result requires stop-and-report behavior.
     * Returns false if no frozen rule exists.
     */
    public boolean requiresStopRules() {
        return classification.map(c -> c == FixtureConformanceClassification.ALLOWED_WITH_STOP_RULES).orElse(false);
    }
}
