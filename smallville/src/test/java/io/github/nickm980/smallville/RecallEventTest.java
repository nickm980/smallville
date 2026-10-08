package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nickm980.smallville.memory.ledger.RecallEvent;

public class RecallEventTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void test_constructor_sets_all_fields() {
	RecallEvent event = new RecallEvent("Jamie", 30, "what did I do this morning?",
		"abc123def", "hot", true);

	assertEquals("Jamie", event.getAgentName());
	assertEquals(30, event.getTick());
	assertEquals("what did I do this morning?", event.getQuery());
	assertEquals("abc123def", event.getSelectedMemoryHash());
	assertEquals("hot", event.getSourceLayer());
	assertTrue(event.isPromotedToWorking());
    }

    @Test
    public void test_source_layer_working() {
	RecallEvent event = new RecallEvent("Alex", 10, "query", "hash", "working", false);
	assertEquals("working", event.getSourceLayer());
	assertFalse(event.isPromotedToWorking());
    }

    @Test
    public void test_source_layer_dream_summary() {
	RecallEvent event = new RecallEvent("Alex", 10, "query", "hash", "dream-summary", true);
	assertEquals("dream-summary", event.getSourceLayer());
    }

    @Test
    public void test_source_layer_archive() {
	RecallEvent event = new RecallEvent("Alex", 10, "query", "hash", "archive", true);
	assertEquals("archive", event.getSourceLayer());
    }

    @Test
    public void test_default_constructor_fields() {
	RecallEvent event = new RecallEvent();
	assertNull(event.getAgentName());
	assertEquals(0, event.getTick());
	assertNull(event.getQuery());
	assertNull(event.getSelectedMemoryHash());
	assertNull(event.getSourceLayer());
	assertFalse(event.isPromotedToWorking());
    }

    @Test
    public void test_json_round_trip() throws Exception {
	RecallEvent event = new RecallEvent("Jamie", 42, "what happened at lunch?",
		"deadbeef", "hot", true);

	String json = mapper.writeValueAsString(event);
	RecallEvent restored = mapper.readValue(json, RecallEvent.class);

	assertEquals(event.getAgentName(), restored.getAgentName());
	assertEquals(event.getTick(), restored.getTick());
	assertEquals(event.getQuery(), restored.getQuery());
	assertEquals(event.getSelectedMemoryHash(), restored.getSelectedMemoryHash());
	assertEquals(event.getSourceLayer(), restored.getSourceLayer());
	assertEquals(event.isPromotedToWorking(), restored.isPromotedToWorking());
    }

    @Test
    public void test_setters_update_fields() {
	RecallEvent event = new RecallEvent();
	event.setAgentName("Alex");
	event.setTick(99);
	event.setQuery("recalled query");
	event.setSelectedMemoryHash("somehash");
	event.setSourceLayer("archive");
	event.setPromotedToWorking(true);

	assertEquals("Alex", event.getAgentName());
	assertEquals(99, event.getTick());
	assertEquals("recalled query", event.getQuery());
	assertEquals("somehash", event.getSelectedMemoryHash());
	assertEquals("archive", event.getSourceLayer());
	assertTrue(event.isPromotedToWorking());
    }
}
