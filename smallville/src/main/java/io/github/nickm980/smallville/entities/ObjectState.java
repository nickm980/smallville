package io.github.nickm980.smallville.entities;

import java.util.List;

public class ObjectState {

    private String state;
    private final List<String> possibleStates;

    public ObjectState(String state, List<String> possibleStates) {
	this.state = state;
	this.possibleStates = possibleStates;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getCurrentState() {
	return state;
    }

    public List<String> getPossibleStates() {
	return possibleStates;
    }
}
