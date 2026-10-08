package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nickm980.smallville.memory.ledger.LedgerEntryType;

public class LedgerEntryTypeTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test_enum_has_exactly_seven_values() {
	assertEquals(7, LedgerEntryType.values().length);
    }

    @Test
    public void test_observation_serializes_to_lowercase() {
	assertEquals("observation", LedgerEntryType.OBSERVATION.toJson());
    }

    @Test
    public void test_dream_compress_serializes_with_hyphen() {
	assertEquals("dream-compress", LedgerEntryType.DREAM_COMPRESS.toJson());
    }

    @ParameterizedTest
    @EnumSource(LedgerEntryType.class)
    public void test_round_trip_all_values(LedgerEntryType type) {
	String json = type.toJson();
	LedgerEntryType restored = LedgerEntryType.fromJson(json);
	assertEquals(type, restored);
    }

    @Test
    public void test_fromJson_unknown_value_throws() {
	assertThrows(IllegalArgumentException.class, () -> LedgerEntryType.fromJson("bogus"));
    }

    @Test
    public void test_jackson_serialization_round_trip() throws JsonProcessingException {
	for (LedgerEntryType type : LedgerEntryType.values()) {
	    String json = mapper.writeValueAsString(type);
	    LedgerEntryType restored = mapper.readValue(json, LedgerEntryType.class);
	    assertEquals(type, restored);
	}
    }

    @Test
    public void test_jackson_serializes_as_string_not_ordinal() throws JsonProcessingException {
	String json = mapper.writeValueAsString(LedgerEntryType.PONDER);
	assertEquals("\"ponder\"", json);
    }
}
