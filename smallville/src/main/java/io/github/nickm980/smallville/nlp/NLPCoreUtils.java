package io.github.nickm980.smallville.nlp;

public interface NLPCoreUtils {

    String extractLastOccurenceOfName(String observation);

    String convertToPastTense(String sentence);

    String convertToPresentTense(String description);

    String[] getEntities(String sentence);
}
