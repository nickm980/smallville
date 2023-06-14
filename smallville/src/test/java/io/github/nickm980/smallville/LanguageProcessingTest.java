package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.nlp.LocalNLP;

public class LanguageProcessingTest {
    LocalNLP nlp = new LocalNLP();

    @Test
    public void testObservationalEntities() {
	String[] values = nlp.getEntities("John finished cooking dinner with the rock");
	String entities = nlp.getNamedEntities("John finished cooking dinner with the rock");
	System.out.println(entities);
	System.out.println("testing!!");
	for (String s : values) {
	    System.out.println(s);
	}
    }
    
    @Test
    @Disabled
    public void testFutureToPast() {
	assertEquals("I went to the mall", nlp.convertToPastTense("I will go to the mall"));
    }
    
    @Test
    @Disabled
    public void testFutureToPresent() {
	assertEquals("finishes cooking dinner and cleans up the campsite", nlp.convertToPresentTense("Finish cooking dinner and clean up the campsite"));
    }
}
