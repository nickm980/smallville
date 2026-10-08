package io.github.nickm980.smallville.ueia.fixture;

/**
 * Approved consumer profiles for canonical fixture consumption.
 * 
 * Frozen source: ueia-fixture-consumer-conformance-spec-v1.md, "Consumer-Profile Scope"
 * 
 * Only these four profiles are in scope. No additional profile gains approval.
 */
public enum FixtureConsumerProfile {
    /**
     * Fixture Reader: reads canonical fixture content (IDs, provenance, boundaries).
     * Frozen reference: fixture-consumer-conformance-examples-v1.md, Examples 1, 5
     */
    FIXTURE_READER("Fixture Reader"),

    /**
     * Fixture Comparator: compares current fixture content against canonical expectations.
     * Frozen reference: fixture-consumer-conformance-examples-v1.md, Examples 2, 6
     */
    FIXTURE_COMPARATOR("Fixture Comparator"),

    /**
     * Fixture Diagnostics Consumer: emits warnings about boundary weakening.
     * Frozen reference: fixture-consumer-conformance-examples-v1.md, Examples 4, 8
     */
    FIXTURE_DIAGNOSTICS_CONSUMER("Fixture Diagnostics Consumer"),

    /**
     * Fixture Export Consumer: exports non-authoritative summaries preserving boundaries.
     * Frozen reference: fixture-consumer-conformance-examples-v1.md, Examples 3, 7
     */
    FIXTURE_EXPORT_CONSUMER("Fixture Export Consumer");

    private final String displayName;

    FixtureConsumerProfile(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
