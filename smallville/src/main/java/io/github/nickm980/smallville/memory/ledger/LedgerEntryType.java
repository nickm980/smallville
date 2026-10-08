package io.github.nickm980.smallville.memory.ledger;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Canonical vocabulary for {@link MemoryLedgerEntry#getType()}.
 * <p>
 * Authoritative source — must stay in sync with
 * {@code schemas/ledger-export.schema.yaml#ledgerEntryType}
 * and {@code docs/contracts/dreaming-memory-governance-v1.md} Event Types table.
 */
public enum LedgerEntryType {

    OBSERVATION("observation"),
    PLAN("plan"),
    REFLECTION("reflection"),
    CHARACTERISTIC("characteristic"),
    RECALL("recall"),
    DREAM_COMPRESS("dream-compress"),
    PONDER("ponder");

    private final String jsonValue;

    LedgerEntryType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String toJson() {
        return jsonValue;
    }

    private static final Map<String, LedgerEntryType> LOOKUP =
        Stream.of(values()).collect(Collectors.toMap(LedgerEntryType::toJson, v -> v));

    @JsonCreator
    public static LedgerEntryType fromJson(String value) {
        LedgerEntryType result = LOOKUP.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Unknown LedgerEntryType: " + value);
        }
        return result;
    }
}
