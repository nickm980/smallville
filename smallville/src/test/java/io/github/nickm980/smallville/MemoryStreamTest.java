package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.math.SmallvilleMath;
import io.github.nickm980.smallville.memory.Memory;
import io.github.nickm980.smallville.memory.MemoryStream;
import io.github.nickm980.smallville.memory.Observation;

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
    public void testScores() {
	System.out.println(SmallvilleMath.calculateSentenceSimilarity("you love playing soccer", "i like watching baseball"));
    }
    
    @Test
    public void testFetchMemories() {
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

 	List<Memory> memories = stream.getRelevantMemories("memory", 0);

 	assertEquals(3, memories.size());
 	assertEquals("memory", memories.get(0).getDescription());
 	assertEquals("memory two", memories.get(1).getDescription());
     }
}
