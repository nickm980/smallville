package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.nlp.LocalNLP;

public class LanguageProcessingTest {
    LocalNLP nlp = new LocalNLP();

    @Test
    public void testFutureToPast() {
	assertEquals("I went to the mall", nlp.convertToPastTense("I will go to the mall"));
    }
    
    @Test
    public void testFutureToPresent() {
	assertEquals("finishes cooking dinner and cleans up the campsite", nlp.convertToPresentTense("Finish cooking dinner and clean up the campsite"));
    }
}
