package io.github.nickm980.smallville.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimulatedLocation implements Location {

    private String name;
    private SimulatedLocation parent;
    protected List<Location> children;

    public SimulatedLocation(String name) {
	if (name.length() > 50) {
	    throw new IllegalArgumentException("Cannot have a location name greater than 10 tokens");
	}

	this.name = name;
	this.parent = null;
	this.children = new ArrayList<Location>();
    }

    public SimulatedLocation(SimulatedLocation parent, String name) {
	this(name);
	if (parent == null) {
	    throw new IllegalArgumentException("Must have a valid parent");
	}

	this.parent = parent;
	this.parent.children.add(this);
    }

    public String getName() {
	return name;
    }

    @Override
    public String asNaturalLanguage() {
	if (children.isEmpty()) {
	    return name;
	}

	return name + " has " + String.join(";", children.stream().map(c -> c.getName()).toList());
    }

    public List<SimulatedObject> getObjects() {
	List<SimulatedObject> objects = new ArrayList<SimulatedObject>();

	for (Location location : children) {
	    if (location instanceof SimulatedObject) {
		objects.add((SimulatedObject) location);
	    }
	}

	return objects;
    }

    public Optional<SimulatedObject> getObject(String objectName) {
	return getObjects().stream().filter(obj -> obj.getName().equals(objectName)).findFirst();
    }
}
