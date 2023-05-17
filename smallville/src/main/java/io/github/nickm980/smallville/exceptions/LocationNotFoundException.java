package io.github.nickm980.smallville.exceptions;

public class LocationNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LocationNotFoundException(String name) {
	super("Location does not exist: " + name);
    }
}