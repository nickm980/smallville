package io.github.nickm980.smallville.models;

public class SimulatedObject implements NaturalLanguageConvertible, Location {
    private ObjectState state;
    private String name;

    public SimulatedObject(String name, ObjectState state, SimulatedLocation parent) {
	this.state = state;
	this.name = name;
	parent.children.add(this);
    }

    public String getState() {
	return state.getCurrentState();
    }

    public void setState(String state) {
	this.state.setState(state);
    }

    @Override
    public String asNaturalLanguage() {
	return name + " (" + state.getCurrentState() + ")";
    }

    @Override
    public String getName() {
	return name;
    }
}
