package io.github.nickm980.smallville.prompts.dto;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.entities.SimulatedObject;

public class WorldModel {

    private String description;

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public static WorldModel fromWorld(String name, World world) {
	WorldModel result = new WorldModel();
	String description = "Available Locations: ";

	for (Location location : world.getLocations()) {
	    description += location.asNaturalLanguage() + "; ";
	}

	description += "Object States: ";

	for (SimulatedObject obj : world.getObjects()) {
	    description += obj.getName() + " is " + obj.getState() + " ";
	    description += System.lineSeparator();
	}

	for (Agent agent : world.getAgents()) {
	    if (agent.getFullName().equals(name)) {
		continue;
	    }
	    description += agent.getFullName() + " is " + agent.getCurrentActivity() + " at "
		    + agent.getLocation().getName() + " ";
	}

	result.setDescription(description);

	return result;
    }
}
