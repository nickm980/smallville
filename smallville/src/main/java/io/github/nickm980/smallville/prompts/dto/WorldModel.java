package io.github.nickm980.smallville.prompts.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.config.simulation.SimulationFile;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Location;

public class WorldModel {
    private static final Set<String> OBJECT_LIKE_ADD_LOCATION_PARENT_TOKENS = Set.of(
	"bench",
	"barrel",
	"table",
	"tray",
	"gate",
	"tool",
	"tools",
	"shelf",
	"shelves",
	"pot",
	"pots",
	"planter",
	"planters",
	"rack",
	"racks"
    );

    private String description;
    private String validAddLocationParents;
    private String invalidAddLocationParents;

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getValidAddLocationParents() {
	return validAddLocationParents;
    }

    public void setValidAddLocationParents(String validAddLocationParents) {
	this.validAddLocationParents = validAddLocationParents;
    }

    public String getInvalidAddLocationParents() {
	return invalidAddLocationParents;
    }

    public void setInvalidAddLocationParents(String invalidAddLocationParents) {
	this.invalidAddLocationParents = invalidAddLocationParents;
    }

    public static WorldModel fromWorld(String name, World world) {
	return fromWorld(name, world, null);
    }

    public static WorldModel fromWorld(String name, World world, SimulationFile simulation) {
	WorldModel result = new WorldModel();
	StringBuilder description = new StringBuilder("Available Locations: ");

	for (Location location : world.getLocations()) {
	    description.append(location.getFullPath());
	    if (location.getState() != null && !location.getState().isBlank()) {
		description.append(" (").append(location.getState()).append(")");
	    }
	    description.append("; ");
	}

	for (Agent agent : world.getAgents()) {
	    if (agent.getFullName().equals(name)) {
		continue;
	    }
	    description.append(agent.getFullName())
		.append(" is ")
		.append(agent.getCurrentActivity())
		.append(" at ")
		.append(agent.getLocation() == null ? "Unknown" : agent.getLocation().getFullPath())
		.append(". ");
	}

	if (simulation != null && simulation.getWorldBuilding() != null) {
	    description.append("World summary: ").append(simulation.getWorldBuilding().getSummary()).append(". ");
	    if (simulation.getWorldBuilding().getRules() != null && !simulation.getWorldBuilding().getRules().isEmpty()) {
		description.append("World-building rules: ")
		    .append(String.join("; ", simulation.getWorldBuilding().getRules()))
		    .append(". ");
	    }
	}

	if (simulation != null && simulation.getDailyRhythm() != null) {
	    SimulationFile.DailyRhythmSeed rhythm = simulation.getDailyRhythm();
	    description.append("Daily rhythm: breakfast ")
		.append(rhythm.getBreakfast())
		.append(", lunch ")
		.append(rhythm.getLunch())
		.append(", dinner ")
		.append(rhythm.getDinner())
		.append(", morning tea ")
		.append(rhythm.getMorningTea())
		.append(", afternoon tea ")
		.append(rhythm.getAfternoonTea())
		.append(", snack ")
		.append(rhythm.getSnack())
		.append(". ");
	}

	result.setDescription(description.toString().trim());
	result.setValidAddLocationParents(buildAddLocationParents(world, true));
	result.setInvalidAddLocationParents(buildAddLocationParents(world, false));

	return result;
    }

    private static String buildAddLocationParents(World world, boolean validParents) {
	List<String> parents = new ArrayList<String>();

	for (Location location : world.getLocations()) {
	    if (isValidAddLocationParent(world, location) == validParents) {
		parents.add(location.getFullPath());
	    }
	}

	if (parents.isEmpty()) {
	    return "none";
	}

	return String.join("; ", parents);
    }

    private static boolean isValidAddLocationParent(World world, Location parent) {
	if (parent == null) {
	    return false;
	}

	if (parent.getAll().size() <= 1) {
	    return true;
	}

	if (hasDirectChildLocation(world, parent)) {
	    return true;
	}

	return !isObjectLikeLeaf(parent);
    }

    private static boolean hasDirectChildLocation(World world, Location parent) {
	String parentPath = parent.getFullPath();
	for (Location location : world.getLocations()) {
	    if (isDirectChildLocation(parentPath, location.getFullPath())) {
		return true;
	    }
	}
	return false;
    }

    private static boolean isDirectChildLocation(String parentPath, String candidatePath) {
	if (candidatePath == null || parentPath == null || candidatePath.equals(parentPath) || !candidatePath.startsWith(parentPath + ":")) {
	    return false;
	}

	String remainder = candidatePath.substring(parentPath.length() + 1).trim();
	return !remainder.isBlank() && !remainder.contains(":");
    }

    private static boolean isObjectLikeLeaf(Location location) {
	if (location == null || location.getAll().isEmpty()) {
	    return false;
	}

	String leaf = location.getAll().get(location.getAll().size() - 1).toLowerCase(Locale.ROOT);
	String normalizedLeaf = leaf.replaceAll("[^a-z0-9]+", " ").trim();
	if (normalizedLeaf.isBlank()) {
	    return false;
	}

	for (String token : normalizedLeaf.split("\\s+")) {
	    if (OBJECT_LIKE_ADD_LOCATION_PARENT_TOKENS.contains(token)) {
		return true;
	    }
	}

	return false;
    }
}
