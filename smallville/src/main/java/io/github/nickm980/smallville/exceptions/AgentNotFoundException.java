package io.github.nickm980.smallville.exceptions;

public class AgentNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AgentNotFoundException(String name) {
	super("Agent not found " + name);
    }
}