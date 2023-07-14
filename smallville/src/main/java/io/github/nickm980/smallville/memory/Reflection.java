package io.github.nickm980.smallville.memory;

public class Reflection extends Memory {

    public Reflection(String description) {
	super(description);
    }

    @Override
    double getRecency() {
	return 1;
    }
}
