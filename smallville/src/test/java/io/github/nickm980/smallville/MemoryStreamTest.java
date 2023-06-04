package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.entities.memory.Memory;
import io.github.nickm980.smallville.entities.memory.MemoryStream;
import io.github.nickm980.smallville.entities.memory.Observation;

public class MemoryStreamTest {

    @Test
    public void testMemoryCreation() {
	MemoryStream stream = new MemoryStream();
	stream.add(new Observation("memory"));

	assertEquals(1, stream.getObservations().size());
    }

    @Test
    public void testRelevantMemories() {
	MemoryStream stream = new MemoryStream();
	stream.add(new Observation("memory"));
	stream.add(new Observation("1"));
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
}
