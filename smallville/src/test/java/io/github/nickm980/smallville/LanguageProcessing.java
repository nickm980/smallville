package io.github.nickm980.smallville;

import org.junit.Test;

import io.github.nickm980.smallville.math.SentenceTokenizer;

public class LanguageProcessing {

    @Test
    public void testNamedEntities() {
	SentenceTokenizer comparison = new SentenceTokenizer();
	comparison.getNamedEntities("John Lin went to the mall with John Smith which is inside the Barn Yard");
    }
}
