package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nickm980.smallville.memory.MemoryLedger;
import io.github.nickm980.smallville.memory.MemoryLedger.DreamPack;
import io.github.nickm980.smallville.memory.MemoryLedger.LedgerMemoryEntry;
import io.github.nickm980.smallville.memory.MemoryLedger.MemoryLedgerSummary;
import io.github.nickm980.smallville.memory.MemoryLedger.RecallPolicy;

public class MemoryLedgerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    // --- Null-defensive setter tests ---

    @Test
    public void test_setPersonaAnchors_null_defaults_to_empty() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.setPersonaAnchors(null);
	assertNotNull(ledger.getPersonaAnchors());
	assertTrue(ledger.getPersonaAnchors().isEmpty());
    }

    @Test
    public void test_setWorking_null_defaults_to_empty() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.setWorking(null);
	assertNotNull(ledger.getWorking());
	assertTrue(ledger.getWorking().isEmpty());
    }

    @Test
    public void test_setRecent_null_defaults_to_empty() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.setRecent(null);
	assertNotNull(ledger.getRecent());
	assertTrue(ledger.getRecent().isEmpty());
    }

    @Test
    public void test_setArchived_null_defaults_to_empty() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.setArchived(null);
	assertNotNull(ledger.getArchived());
	assertTrue(ledger.getArchived().isEmpty());
    }

    @Test
    public void test_setDreamPacks_null_defaults_to_empty() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.setDreamPacks(null);
	assertNotNull(ledger.getDreamPacks());
	assertTrue(ledger.getDreamPacks().isEmpty());
    }

    @Test
    public void test_setAffect_null_gives_default() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.setAffect(null);
	assertNotNull(ledger.getAffect());
    }

    @Test
    public void test_setRecallPolicy_null_gives_default() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.setRecallPolicy(null);
	assertNotNull(ledger.getRecallPolicy());
    }

    @Test
    public void test_setSummary_null_gives_default() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.setSummary(null);
	assertNotNull(ledger.getSummary());
    }

    // --- rebuildSummary tests ---

    @Test
    public void test_rebuildSummary_counts_match_list_sizes() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.setLastDreamTick(42);
	ledger.setPersonaAnchors(List.of("trait1", "trait2"));
	ledger.setWorking(List.of(makeEntry("w1"), makeEntry("w2"), makeEntry("w3")));
	ledger.setRecent(List.of(makeEntry("r1")));
	ledger.setArchived(List.of(makeEntry("a1"), makeEntry("a2")));
	ledger.setDreamPacks(List.of(new DreamPack()));

	MemoryLedgerSummary summary = ledger.getSummary();
	assertEquals(2, summary.getPersonaAnchorCount());
	assertEquals(3, summary.getWorkingCount());
	assertEquals(1, summary.getRecentCount());
	assertEquals(2, summary.getArchivedCount());
	assertEquals(1, summary.getDreamPackCount());
	assertEquals(42, summary.getLastDreamTick());
    }

    @Test
    public void test_rebuildSummary_empty_ledger() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.rebuildSummary();
	MemoryLedgerSummary summary = ledger.getSummary();
	assertEquals(0, summary.getWorkingCount());
	assertEquals(0, summary.getRecentCount());
	assertEquals(0, summary.getArchivedCount());
	assertEquals(0, summary.getDreamPackCount());
    }

    // --- seenMemoryKeys tests ---

    @Test
    public void test_hasSeenKey_false_initially() {
	MemoryLedger ledger = new MemoryLedger();
	assertFalse(ledger.hasSeenKey("anything"));
    }

    @Test
    public void test_hasSeenKey_null_returns_false() {
	MemoryLedger ledger = new MemoryLedger();
	assertFalse(ledger.hasSeenKey(null));
    }

    @Test
    public void test_rememberKey_then_hasSeenKey() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.rememberKey("garden-watering");
	assertTrue(ledger.hasSeenKey("garden-watering"));
    }

    @Test
    public void test_rememberKey_blank_ignored() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.rememberKey("");
	ledger.rememberKey("  ");
	assertTrue(ledger.getSeenMemoryKeys().isEmpty());
    }

    @Test
    public void test_rememberKey_null_ignored() {
	MemoryLedger ledger = new MemoryLedger();
	ledger.rememberKey(null);
	assertTrue(ledger.getSeenMemoryKeys().isEmpty());
    }

    @Test
    public void test_rebuildSeenMemoryKeys_from_lists() {
	MemoryLedger ledger = new MemoryLedger();

	LedgerMemoryEntry w1 = makeEntry("key-working");
	LedgerMemoryEntry r1 = makeEntry("key-recent");
	LedgerMemoryEntry a1 = makeEntry("key-archived");

	ledger.setWorking(List.of(w1));
	ledger.setRecent(List.of(r1));
	ledger.setArchived(List.of(a1));

	ledger.rebuildSeenMemoryKeys();

	assertTrue(ledger.hasSeenKey("key-working"));
	assertTrue(ledger.hasSeenKey("key-recent"));
	assertTrue(ledger.hasSeenKey("key-archived"));
    }

    @Test
    public void test_rebuildSeenMemoryKeys_includes_dream_pack_memories() {
	MemoryLedger ledger = new MemoryLedger();

	DreamPack dp = new DreamPack();
	LedgerMemoryEntry dreamMem = makeEntry("key-in-dream");
	dp.setMemories(List.of(dreamMem));

	ledger.setDreamPacks(List.of(dp));
	ledger.rebuildSeenMemoryKeys();

	assertTrue(ledger.hasSeenKey("key-in-dream"));
    }

    @Test
    public void test_seenMemoryKeys_not_in_json() throws Exception {
	MemoryLedger ledger = new MemoryLedger();
	ledger.setAgent("Jamie");
	ledger.rememberKey("some-key");

	String json = mapper.writeValueAsString(ledger);
	assertFalse(json.contains("seenMemoryKeys"));
    }

    // --- RecallPolicy tests ---

    @Test
    public void test_recallPolicy_defaults() {
	RecallPolicy policy = new RecallPolicy();
	assertEquals(3, policy.getTopK());
	assertEquals(6, policy.getHotMemoryLimit());
	assertEquals("json.gz", policy.getArchiveCompression());
	assertEquals(List.of("working", "recent", "dream-packs", "archived"), policy.getOrder());
    }

    @Test
    public void test_recallPolicy_setOrder_null_defaults() {
	RecallPolicy policy = new RecallPolicy();
	policy.setOrder(null);
	assertEquals(List.of("working", "recent", "dream-packs", "archived"), policy.getOrder());
    }

    // --- LedgerMemoryEntry identity tests ---

    @Test
    public void test_ledgerMemoryEntry_equals_by_id() {
	LedgerMemoryEntry a = new LedgerMemoryEntry();
	a.setId("entry-1");
	LedgerMemoryEntry b = new LedgerMemoryEntry();
	b.setId("entry-1");
	assertEquals(a, b);
	assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void test_ledgerMemoryEntry_not_equal_different_id() {
	LedgerMemoryEntry a = new LedgerMemoryEntry();
	a.setId("entry-1");
	LedgerMemoryEntry b = new LedgerMemoryEntry();
	b.setId("entry-2");
	assertFalse(a.equals(b));
    }

    // --- JSON round-trip test ---

    @Test
    public void test_json_round_trip() throws Exception {
	MemoryLedger ledger = new MemoryLedger();
	ledger.setAgent("Jamie");
	ledger.setPersonaAnchors(List.of("kind", "curious"));
	ledger.setWorking(List.of(makeEntry("w1")));
	ledger.setRecent(List.of(makeEntry("r1")));
	ledger.setArchived(new ArrayList<>());
	ledger.setDreamPacks(new ArrayList<>());
	ledger.setLastDreamTick(10);
	ledger.setUpdatedAtUtc("2026-04-03T12:00:00Z");

	String json = mapper.writeValueAsString(ledger);
	MemoryLedger restored = mapper.readValue(json, MemoryLedger.class);

	assertEquals("Jamie", restored.getAgent());
	assertEquals(2, restored.getPersonaAnchors().size());
	assertEquals(1, restored.getWorking().size());
	assertEquals(1, restored.getRecent().size());
	assertEquals(10, restored.getLastDreamTick());
	assertEquals("2026-04-03T12:00:00Z", restored.getUpdatedAtUtc());
    }

    // --- Helper ---

    private static LedgerMemoryEntry makeEntry(String key) {
	LedgerMemoryEntry entry = new LedgerMemoryEntry();
	entry.setId(key);
	entry.setKey(key);
	entry.setKind("observation");
	entry.setDescription("test entry " + key);
	return entry;
    }
}
