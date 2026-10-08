package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.nickm980.smallville.memory.ledger.LedgerEntryType;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerEntry;

public class MemoryLedgerEntryTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    public void test_constructor_generates_uuid() {
	MemoryLedgerEntry entry = new MemoryLedgerEntry("Alice", 5,
		LocalDateTime.of(2026, 4, 3, 12, 0),
		LedgerEntryType.OBSERVATION, "watered the garden", 0.7, null);
	assertNotNull(entry.getId());
	assertTrue(entry.getId().matches("^[0-9a-f\\-]{36}$"));
    }

    @Test
    public void test_constructor_computes_source_hash() {
	MemoryLedgerEntry entry = new MemoryLedgerEntry("Alice", 1,
		LocalDateTime.now(), LedgerEntryType.OBSERVATION, "hello world", 0.5, null);
	assertNotNull(entry.getSourceHash());
	assertEquals(64, entry.getSourceHash().length());
    }

    @Test
    public void test_same_description_produces_same_hash() {
	String desc = "watered the garden";
	MemoryLedgerEntry a = new MemoryLedgerEntry("Alice", 1, LocalDateTime.now(),
		LedgerEntryType.OBSERVATION, desc, 0.5, null);
	MemoryLedgerEntry b = new MemoryLedgerEntry("Bob", 2, LocalDateTime.now(),
		LedgerEntryType.PLAN, desc, 0.8, "important");
	assertEquals(a.getSourceHash(), b.getSourceHash());
    }

    @Test
    public void test_sha256_empty_input_returns_empty() {
	assertEquals("", MemoryLedgerEntry.sha256(""));
	assertEquals("", MemoryLedgerEntry.sha256(null));
    }

    @Test
    public void test_sha256_known_value() {
	// SHA-256 of "test" is well-known
	String hash = MemoryLedgerEntry.sha256("test");
	assertEquals("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", hash);
    }

    @Test
    public void test_retention_reason_nullable() {
	MemoryLedgerEntry entry = new MemoryLedgerEntry("Alice", 1,
		LocalDateTime.now(), LedgerEntryType.REFLECTION, "pondering", 0.3, null);
	assertNull(entry.getRetentionReason());

	entry.setRetentionReason("high importance");
	assertEquals("high importance", entry.getRetentionReason());
    }

    @Test
    public void test_all_fields_set_via_constructor() {
	LocalDateTime ts = LocalDateTime.of(2026, 4, 3, 10, 30);
	MemoryLedgerEntry entry = new MemoryLedgerEntry("Jamie", 42, ts,
		LedgerEntryType.PONDER, "thinking about flowers", 0.6, "curiosity");

	assertEquals("Jamie", entry.getAgentName());
	assertEquals(42, entry.getTick());
	assertEquals(ts, entry.getTimestamp());
	assertEquals(LedgerEntryType.PONDER, entry.getType());
	assertEquals("thinking about flowers", entry.getDescription());
	assertEquals(0.6, entry.getImportance());
	assertEquals("curiosity", entry.getRetentionReason());
    }

    @Test
    public void test_json_round_trip() throws Exception {
	MemoryLedgerEntry entry = new MemoryLedgerEntry("Alex", 10,
		LocalDateTime.of(2026, 4, 3, 14, 0),
		LedgerEntryType.DREAM_COMPRESS, "compressed dream", 0.9, "high recall");

	String json = mapper.writeValueAsString(entry);
	MemoryLedgerEntry restored = mapper.readValue(json, MemoryLedgerEntry.class);

	assertEquals(entry.getId(), restored.getId());
	assertEquals(entry.getAgentName(), restored.getAgentName());
	assertEquals(entry.getTick(), restored.getTick());
	assertEquals(entry.getType(), restored.getType());
	assertEquals(entry.getDescription(), restored.getDescription());
	assertEquals(entry.getSourceHash(), restored.getSourceHash());
	assertEquals(entry.getImportance(), restored.getImportance());
	assertEquals(entry.getRetentionReason(), restored.getRetentionReason());
    }

    @Test
    public void test_default_constructor_fields_null() {
	MemoryLedgerEntry entry = new MemoryLedgerEntry();
	assertNull(entry.getId());
	assertNull(entry.getAgentName());
	assertNull(entry.getType());
	assertNull(entry.getDescription());
	assertNull(entry.getSourceHash());
	assertEquals(0, entry.getTick());
	assertEquals(0.0, entry.getImportance());
    }
}
