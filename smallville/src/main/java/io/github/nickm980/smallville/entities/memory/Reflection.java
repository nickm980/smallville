package io.github.nickm980.smallville.entities.memory;

public class Reflection extends Memory {

    public Reflection(String description) {
	super(description);
    }

    @Override
    double getRecency() {
	return 1;
    }
}
