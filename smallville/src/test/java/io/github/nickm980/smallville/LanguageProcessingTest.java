package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.nlp.LocalNLP;

public class LanguageProcessingTest {
    LocalNLP nlp = new LocalNLP();

    @Test
    public void testNamedEntities() {
	assertEquals("I went to the mall", nlp.convertToPastTense("I will go to the mall"));
    }

}
