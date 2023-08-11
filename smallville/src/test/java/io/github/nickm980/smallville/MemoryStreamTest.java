package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.llm.ChatGPT;
import io.github.nickm980.smallville.memory.Memory;
import io.github.nickm980.smallville.memory.MemoryStream;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.memory.Plan;
import io.github.nickm980.smallville.prompts.ChatService;

public class MemoryStreamTest {

    @Test
    public void test_observation_is_added_to_stream() {
	MemoryStream stream = new MemoryStream();
	stream.add(new Observation("memory"));

	assertEquals(1, stream.getObservations().size());
    }

    @Test
    public void test_correct_relevant_memories_and_correct_amount_are_fetched() {
	MemoryStream stream = new MemoryStream();
	stream.add(new Observation("memory"));
	stream.add(new Observation(""));
	stream.add(new Observation("2"));
	stream.add(new Observation("3"));
	stream.add(new Observation("4"));
	stream.add(new Observation("5"));
	stream.add(new Observation("6"));
	stream.add(new Observation("7"));
	stream.add(new Observation("8"));
	stream.add(new Observation("memory two"));
	stream.add(new Observation("9"));

	List<Memory> memories = stream.getRelevantMemories("memory", -1);

	assertEquals(3, memories.size());
	assertEquals("memory", memories.get(0).getDescription());
	assertEquals("memory two", memories.get(1).getDescription());
    }

    @Test
    public void test_correct_relevant_memories_and_correct_amount_are_fetched2() {
	MemoryStream stream = new MemoryStream();
	stream.add(new Observation("i love playing basketball"));
	stream.add(new Observation("on saturday i slept in"));
	stream.add(new Observation("i completed my homework"));
	stream.add(new Observation("finished my homework"));
	stream.add(new Observation("woke up and made breakfast"));
	stream.add(new Observation("played video games for an hour"));
	stream.add(new Observation("played soccer for an hour"));
	stream.add(new Observation("played Battlefield 1 for an hour"));
	stream.add(new Observation("likes to play video games"));
	stream.add(new Observation("saw a bird fly by"));
	stream.add(new Observation("memory"));
	stream.add(new Observation("memory two"));

	List<Memory> memories = stream.getRelevantMemories("memory", 0);

	assertEquals(3, memories.size());
	assertEquals("memory", memories.get(0).getDescription());
	assertEquals("memory two", memories.get(1).getDescription());
    }

    @Test
    public void test_adding_and_getting_plans_from_memory_stream() {
	ChatService service = new ChatService(new World(), new ChatGPT());
	List<Plan> plans = service.parsePlans("""
		\nI will then go to the Green House and sleep from 12:05 AM.
		\nI will wake up and make breakfast from 11:30 PM - 9:30 AM.
		\nI will then go to the Forest and spend some time gathering branches from 10:00 AM - 11:00 AM.
				""");
	MemoryStream stream = new MemoryStream();

	stream.addAll(plans);

	assertEquals(3, stream.getPlans().size());
    }
}
