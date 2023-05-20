package io.github.nickm980.smallville.models;

import javax.annotation.Nullable;

public class AgentLocation {

    private SimulatedObject object;
    private SimulatedLocation location;

    public AgentLocation(SimulatedLocation location) {
	this.location = location;
    }

    public AgentLocation(SimulatedLocation location, SimulatedObject object) {
	this(location);
	this.object = object;
    }

    public @Nullable SimulatedObject getObject() {
	return object;
    }

    public void setObject(SimulatedObject object) {
	this.object = object;
    }

    public SimulatedLocation getLocation() {
	return location;
    }

    public void setLocation(SimulatedLocation location) {
	this.location = location;
    }
}
