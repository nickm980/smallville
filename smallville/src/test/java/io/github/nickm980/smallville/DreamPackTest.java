package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nickm980.smallville.memory.ledger.DreamPack;

public class DreamPackTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test_constructor_generates_uuid() {
	DreamPack pack = new DreamPack("Jamie", 50, "compressed morning memories",
		List.of("hash1", "hash2"), Map.of("hash1", "high importance"), 5);
	assertNotNull(pack.getPackId());
	assertTrue(pack.getPackId().matches("^[0-9a-f\\-]{36}$"));
    }

    @Test
    public void test_constructor_sets_all_fields() {
	List<String> hashes = List.of("h1", "h2", "h3");
	Map<String, String> reasons = Map.of("h1", "high recall", "h2", "persona anchor");
	DreamPack pack = new DreamPack("Alex", 100, "afternoon summary", hashes, reasons, 10);

	assertEquals("Alex", pack.getAgentName());
	assertEquals(100, pack.getCreatedAtTick());
	assertEquals("afternoon summary", pack.getSummaryText());
	assertEquals(3, pack.getSourceHashes().size());
	assertEquals(2, pack.getRetentionReasons().size());
	assertEquals(10, pack.getCompressedEntryCount());
    }

    @Test
    public void test_null_hashes_defaults_to_empty_list() {
	DreamPack pack = new DreamPack("Jamie", 10, "summary", null, null, 0);
	assertNotNull(pack.getSourceHashes());
	assertTrue(pack.getSourceHashes().isEmpty());
    }

    @Test
    public void test_null_retention_reasons_defaults_to_empty_map() {
	DreamPack pack = new DreamPack("Jamie", 10, "summary", null, null, 0);
	assertNotNull(pack.getRetentionReasons());
	assertTrue(pack.getRetentionReasons().isEmpty());
    }

    @Test
    public void test_source_hashes_are_defensive_copy() {
	List<String> original = new java.util.ArrayList<>(List.of("h1", "h2"));
	DreamPack pack = new DreamPack("Jamie", 10, "summary", original, null, 2);
	original.add("h3");
	assertEquals(2, pack.getSourceHashes().size());
    }

    @Test
    public void test_retention_reasons_are_defensive_copy() {
	Map<String, String> original = new java.util.HashMap<>(Map.of("h1", "reason1"));
	DreamPack pack = new DreamPack("Jamie", 10, "summary", null, original, 1);
	original.put("h2", "reason2");
	assertEquals(1, pack.getRetentionReasons().size());
    }

    @Test
    public void test_setter_null_hashes_defensive() {
	DreamPack pack = new DreamPack();
	pack.setSourceHashes(null);
	assertNotNull(pack.getSourceHashes());
	assertTrue(pack.getSourceHashes().isEmpty());
    }

    @Test
    public void test_setter_null_retention_reasons_defensive() {
	DreamPack pack = new DreamPack();
	pack.setRetentionReasons(null);
	assertNotNull(pack.getRetentionReasons());
	assertTrue(pack.getRetentionReasons().isEmpty());
    }

    @Test
    public void test_default_constructor_fields_null() {
	DreamPack pack = new DreamPack();
	assertNull(pack.getPackId());
	assertNull(pack.getAgentName());
	assertNull(pack.getSummaryText());
	assertEquals(0, pack.getCreatedAtTick());
	assertEquals(0, pack.getCompressedEntryCount());
    }

    @Test
    public void test_json_round_trip() throws Exception {
	DreamPack pack = new DreamPack("Jamie", 50, "morning compression",
		List.of("abc123", "def456"), Map.of("abc123", "high importance"), 3);

	String json = mapper.writeValueAsString(pack);
	DreamPack restored = mapper.readValue(json, DreamPack.class);

	assertEquals(pack.getPackId(), restored.getPackId());
	assertEquals(pack.getAgentName(), restored.getAgentName());
	assertEquals(pack.getCreatedAtTick(), restored.getCreatedAtTick());
	assertEquals(pack.getSummaryText(), restored.getSummaryText());
	assertEquals(pack.getSourceHashes(), restored.getSourceHashes());
	assertEquals(pack.getRetentionReasons(), restored.getRetentionReasons());
	assertEquals(pack.getCompressedEntryCount(), restored.getCompressedEntryCount());
    }
}
