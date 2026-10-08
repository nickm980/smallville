package io.github.nickm980.smallville.ueia.fixture;

/**
 * Conformance classifications for profile-to-fixture interactions.
 * 
 * Frozen source: ueia-fixture-consumer-conformance-spec-v1.md, "Conformance Model"
 * 
 * These are the ONLY frozen policy classifications. Absence of a frozen rule is represented
 * separately via Optional in ConformanceValidationResult, not as a classification value.
 */
public enum FixtureConformanceClassification {
    /**
     * Consumer behavior is allowed without constraints.
     * Frozen reference: fixture-consumer-conformance-examples-v1.md, Example 2
     */
    ALLOWED("allowed", "Consumer behavior is allowed without constraints."),

    /**
     * Consumer behavior is allowed, but with explicit stop-and-report rules.
     * Stop is required when specific boundary conditions are triggered.
     * Frozen reference: fixture-consumer-conformance-examples-v1.md, Examples 1, 3, 4, 5, 6, 7, 8
     */
    ALLOWED_WITH_STOP_RULES("allowed-with-stop-rules", "Consumer behavior is allowed, but with stop-and-report rules."),

    /**
     * Consumer behavior is not allowed under any circumstances.
     * Frozen reference: fixture-consumer-conformance-examples-v1.md, Examples 9, 10, 11, 12
     */
    NOT_ALLOWED("not-allowed", "Consumer behavior is not allowed.");

    private final String frozenName;
    private final String description;

    FixtureConformanceClassification(String frozenName, String description) {
        this.frozenName = frozenName;
        this.description = description;
    }

    public String getFrozenName() {
        return frozenName;
    }

    public String getDescription() {
        return description;
    }
}
