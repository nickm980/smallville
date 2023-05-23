package io.github.nickm980.smallville.entities;

/**
 * An interface for objects that can be converted to a natural language string
 * representation.
 * 
 * This interface defines a single method, {@link #asNaturalLanguage()}, that
 * should be implemented by any class that can be converted to a natural
 * language string. The returned string should be a human-readable
 * representation of the object.
 */
public interface NaturalLanguageConvertible {
    /**
     * Returns a string representation of this object in natural language.
     * 
     * This method should be implemented by any class that can be converted to a
     * natural language string representation. The returned string should be a
     * human-readable representation of the object, suitable for display, as input
     * for language models and communication with non-technical users.
     * 
     * @return a natural language string representation of this object.
     */
    String asNaturalLanguage();
}
