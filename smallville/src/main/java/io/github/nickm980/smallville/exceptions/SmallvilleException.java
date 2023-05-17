package io.github.nickm980.smallville.exceptions;

public class SmallvilleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SmallvilleException(String message) {
	super(message);
    }

    public SmallvilleException(String message, Throwable cause) {
	super(message, cause);
    }
}