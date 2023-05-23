package io.github.nickm980.smallville.prompts.dto;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Location;

public class WorldModel {

    private String description;

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public static WorldModel fromWorld(World world) {
	WorldModel result = new WorldModel();
	String description = "Available Locations: ";

	for (Location location : world.getLocations()) {
	    description += location.asNaturalLanguage() + "; ";
	}

	result.setDescription(description);

	return result;
    }
}
