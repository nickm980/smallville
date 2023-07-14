package io.github.nickm980.smallville.entities;

import java.util.ArrayList;
import java.util.List;

public class Location {

    /**
     * Seperates the location name by the colon delimitted parts. For example, 'Red
     * House: Bedroom: Bed' would all be included as individual elements in this array
     */
    private List<String> parts;
    /**
     * The location name without any seperation. Includes all colons.
     */
    private String original;
    private String state;

    public Location(String name) {
	if (name.length() > 50) {
	    throw new IllegalArgumentException("Cannot have a location name greater than 50 characters");
	}

	parts = new ArrayList<String>();

	for (String s : name.split(":")) {
	    parts.add(s.trim());
	}
	
	this.original = name;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getState() {
	return state;
    }
    
    public String getFullPath() {
	return original;
    }
    
    public List<String> getAll() {
	return parts;
    }
}
