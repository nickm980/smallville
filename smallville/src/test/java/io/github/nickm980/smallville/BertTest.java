package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.math.SmallvilleMath;

public class BertTest {
    
    @Test
    public void testCosineSimilarity() {
	double p1 = SmallvilleMath.calculateSentenceSimilarity("The quick brown fox jumps over the lazy dog.",
		"The lazy dog is jumped over by the quick brown fox.");

	assertTrue(p1 > .4, "Memory with same semantic meaning not given a high enough rating " + p1);
    }

    @Test
    public void testMemoryImportance() {
	String a = "Eddy lin is my son";

	double p2 = SmallvilleMath.calculateSentenceSimilarity(a, "Who is your son?");

	assertTrue(p2 > 0);
    }
}
