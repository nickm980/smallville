package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.entities.AffectState;
import io.github.nickm980.smallville.memory.ledger.DreamPack;
import io.github.nickm980.smallville.memory.ledger.LedgerEntryType;
import io.github.nickm980.smallville.memory.ledger.MemoryJournalWriter;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerEntry;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerIndex;

public class MemoryJournalWriterTest {

    private MemoryJournalWriter writer;
    private String tmpRoot;

    @BeforeEach
    public void setUp() {
	tmpRoot = System.getProperty("java.io.tmpdir") + "/smallville-journal-test-" + System.currentTimeMillis();
	writer = new MemoryJournalWriter(tmpRoot);
    }

    @Test
    public void test_append_entry_creates_ndjson_file() {
	MemoryLedgerEntry entry = new MemoryLedgerEntry();
	entry.setAgentName("TestAgent");
	entry.setTick(1);
	entry.setType(LedgerEntryType.OBSERVATION);
	entry.setDescription("watered the garden");

	writer.appendEntry("TestAgent", entry);

	List<MemoryLedgerEntry> entries = writer.loadEntries("TestAgent");
	assertFalse(entries.isEmpty());
	assertEquals("watered the garden", entries.get(0).getDescription());
    }

    @Test
    public void test_multiple_entries_append_correctly() {
	for (int i = 0; i < 5; i++) {
	    MemoryLedgerEntry entry = new MemoryLedgerEntry();
	    entry.setAgentName("TestAgent");
	    entry.setTick(i);
	    entry.setType(LedgerEntryType.OBSERVATION);
	    entry.setDescription("event " + i);
	    writer.appendEntry("TestAgent", entry);
	}

	List<MemoryLedgerEntry> entries = writer.loadEntries("TestAgent");
	assertEquals(5, entries.size());
    }

    @Test
    public void test_write_and_load_index() {
	MemoryLedgerIndex index = new MemoryLedgerIndex("TestAgent");
	index.setTotalEvents(10);
	index.setHotCount(5);
	index.setArchivedCount(3);
	index.setDreamPackCount(1);
	index.setLastDreamTick(20);
	index.setLastAffect(AffectState.neutral(20));

	writer.writeIndex("TestAgent", index);

	MemoryLedgerIndex loaded = writer.loadOrCreateIndex("TestAgent");
	assertEquals("TestAgent", loaded.getAgentName());
	assertEquals(10, loaded.getTotalEvents());
	assertEquals(5, loaded.getHotCount());
	assertEquals(3, loaded.getArchivedCount());
	assertEquals(1, loaded.getDreamPackCount());
	assertEquals(20, loaded.getLastDreamTick());
	assertNotNull(loaded.getLastAffect());
    }

    @Test
    public void test_load_or_create_index_returns_new_for_unknown_agent() {
	MemoryLedgerIndex index = writer.loadOrCreateIndex("UnknownAgent");
	assertNotNull(index);
	assertEquals("UnknownAgent", index.getAgentName());
	assertEquals(0, index.getTotalEvents());
    }

    @Test
    public void test_write_and_load_dream_pack() {
	DreamPack pack = new DreamPack(
	    "TestAgent", 30, "summary of archived memories",
	    List.of("hash1", "hash2"), java.util.Map.of("hash1", "routine", "hash2", "social"),
	    5
	);

	writer.writeDreamPack("TestAgent", pack, true);

	List<DreamPack> packs = writer.loadDreamPacks("TestAgent");
	assertFalse(packs.isEmpty());
	assertEquals("TestAgent", packs.get(0).getAgentName());
	assertEquals("summary of archived memories", packs.get(0).getSummaryText());
	assertEquals(2, packs.get(0).getSourceHashes().size());
    }

    @Test
    public void test_has_persisted_data_returns_false_for_new_agent() {
	assertFalse(writer.hasPersistedData("NonexistentAgent"));
    }

    @Test
    public void test_has_persisted_data_returns_true_after_write() {
	MemoryLedgerIndex index = new MemoryLedgerIndex("TestAgent");
	writer.writeIndex("TestAgent", index);

	assertTrue(writer.hasPersistedData("TestAgent"));
    }

    @Test
    public void test_load_recent_entries_by_type() {
	for (int i = 0; i < 10; i++) {
	    MemoryLedgerEntry entry = new MemoryLedgerEntry();
	    entry.setAgentName("TestAgent");
	    entry.setTick(i);
	    entry.setType(i % 2 == 0 ? LedgerEntryType.OBSERVATION : LedgerEntryType.PLAN);
	    entry.setDescription("entry " + i);
	    writer.appendEntry("TestAgent", entry);
	}

	List<MemoryLedgerEntry> observations = writer.loadRecentEntriesByType("TestAgent", LedgerEntryType.OBSERVATION, 3);
	assertEquals(3, observations.size());
	for (MemoryLedgerEntry e : observations) {
	    assertEquals(LedgerEntryType.OBSERVATION, e.getType());
	}
    }

    @Test
    public void test_agent_name_sanitization() {
	// Agent names with special characters should be sanitized for filesystem paths
	MemoryLedgerEntry entry = new MemoryLedgerEntry();
	entry.setAgentName("Agent With Spaces");
	entry.setTick(1);
	entry.setType(LedgerEntryType.OBSERVATION);
	entry.setDescription("test");

	writer.appendEntry("Agent With Spaces", entry);
	List<MemoryLedgerEntry> entries = writer.loadEntries("Agent With Spaces");
	assertFalse(entries.isEmpty());
    }
}
