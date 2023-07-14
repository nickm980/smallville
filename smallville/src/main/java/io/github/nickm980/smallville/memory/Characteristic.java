package io.github.nickm980.smallville.memory;

/**
 * A Characteristic is a type of memory given to an agent before the simulation
 * starts. This is to help guide the agents initial behavior as the simulation
 * progresses. It also describes the beginning relationships that the agent has.
 */
public class Characteristic extends Memory {

    public Characteristic(String description) {
	super(description);
    }

    @Override
    double getRecency() {
	return .5;
    }
}