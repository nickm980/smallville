package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nickm980.smallville.entities.AffectState;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerIndex;

public class MemoryLedgerIndexTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test_name_constructor_sets_agent() {
	MemoryLedgerIndex index = new MemoryLedgerIndex("Jamie");
	assertEquals("Jamie", index.getAgentName());
    }

    @Test
    public void test_default_constructor_fields_zero() {
	MemoryLedgerIndex index = new MemoryLedgerIndex();
	assertNull(index.getAgentName());
	assertEquals(0, index.getTotalEvents());
	assertEquals(0, index.getHotCount());
	assertEquals(0, index.getArchivedCount());
	assertEquals(0, index.getDreamPackCount());
	assertEquals(0, index.getLastDreamTick());
	assertEquals(0, index.getLastRecallTick());
	assertEquals(0, index.getPonderCount());
	assertEquals(0, index.getLastPonderTick());
	assertNull(index.getLastAffect());
    }

    @Test
    public void test_setters_update_all_fields() {
	MemoryLedgerIndex index = new MemoryLedgerIndex("Alex");
	index.setTotalEvents(15);
	index.setHotCount(5);
	index.setArchivedCount(8);
	index.setDreamPackCount(2);
	index.setLastDreamTick(100);
	index.setLastRecallTick(95);
	index.setPonderCount(3);
	index.setLastPonderTick(90);

	assertEquals(15, index.getTotalEvents());
	assertEquals(5, index.getHotCount());
	assertEquals(8, index.getArchivedCount());
	assertEquals(2, index.getDreamPackCount());
	assertEquals(100, index.getLastDreamTick());
	assertEquals(95, index.getLastRecallTick());
	assertEquals(3, index.getPonderCount());
	assertEquals(90, index.getLastPonderTick());
    }

    @Test
    public void test_last_affect_carrier() {
	MemoryLedgerIndex index = new MemoryLedgerIndex("Jamie");
	assertNull(index.getLastAffect());

	AffectState affect = AffectState.neutral(10);
	index.setLastAffect(affect);

	assertNotNull(index.getLastAffect());
	assertEquals(10, index.getLastAffect().getUpdatedAtTick());
    }

    @Test
    public void test_json_round_trip() throws Exception {
	MemoryLedgerIndex index = new MemoryLedgerIndex("Jamie");
	index.setTotalEvents(20);
	index.setHotCount(6);
	index.setArchivedCount(10);
	index.setDreamPackCount(3);
	index.setLastDreamTick(50);
	index.setLastRecallTick(45);
	index.setPonderCount(4);
	index.setLastPonderTick(48);
	index.setLastAffect(AffectState.neutral(50));

	String json = mapper.writeValueAsString(index);
	MemoryLedgerIndex restored = mapper.readValue(json, MemoryLedgerIndex.class);

	assertEquals(index.getAgentName(), restored.getAgentName());
	assertEquals(index.getTotalEvents(), restored.getTotalEvents());
	assertEquals(index.getHotCount(), restored.getHotCount());
	assertEquals(index.getArchivedCount(), restored.getArchivedCount());
	assertEquals(index.getDreamPackCount(), restored.getDreamPackCount());
	assertEquals(index.getLastDreamTick(), restored.getLastDreamTick());
	assertEquals(index.getLastRecallTick(), restored.getLastRecallTick());
	assertEquals(index.getPonderCount(), restored.getPonderCount());
	assertEquals(index.getLastPonderTick(), restored.getLastPonderTick());
	assertNotNull(restored.getLastAffect());
    }

    @Test
    public void test_counts_consistency() {
	MemoryLedgerIndex index = new MemoryLedgerIndex("Alex");
	index.setHotCount(5);
	index.setArchivedCount(10);
	index.setTotalEvents(15);

	assertEquals(index.getHotCount() + index.getArchivedCount(), index.getTotalEvents());
    }
}
