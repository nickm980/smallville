package io.github.nickm980.smallville;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.Dialog;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.entities.SimulatedLocation;
import io.github.nickm980.smallville.entities.SimulatedObject;
import io.github.nickm980.smallville.exceptions.LocationNotFoundException;
import io.github.nickm980.smallville.exceptions.SmallvilleException;

/**
 * Creates an interactive Simulation for Generative Agents
 * 
 * Steps:
 * <ul>
 * 
 * <li>World creation. Create prompts describing the world. Locations and their
 * descriptions, People and their first "memory" or brief description about
 * themselves including relationships with others, profession, and character
 * traits</li>
 * 
 * <li>Planning. For each character, ask what their goals are for the day and
 * put this into a plan</li>
 * 
 * <li>Convert each planning step into past tense and rank each memories
 * poignancy</li>
 * 
 * <li>Action Phase. Convert the first plan into first tense, create an emoji
 * based on first tense sentence, and get location to travel</li>
 * </ul>
 * 
 * <p>
 * Some work still needs to be done
 * 
 * <ul>
 * <li>Protecting prompts from prompt injection by using combination of system
 * and user prompts</li>
 * 
 * <li>Improving memory retrieval through better token embeddings and optimize
 * decay function</li>
 * 
 *
 * <li>Add support for conversations between two (or more) agents</li>
 * </ul>
 *
 */
public class World {

    private List<SimulatedLocation> locations;
    private List<SimulatedObject> objects;
    private List<Conversation> conversations;
    private Set<Agent> agents;
    private final Logger LOG = LoggerFactory.getLogger(World.class);

    public World() {
	this.locations = new ArrayList<>();
	this.agents = new HashSet<>();
	this.objects = new ArrayList<>();
	this.conversations = new ArrayList<>();
    }

    /**
     * Saves an agent.
     * 
     * @param agent - the agent to save
     */
    public void save(Agent agent) {
	if (getAgent(agent.getFullName()).isPresent()) {
	    throw new SmallvilleException("Agent already exists. Use a differet name");
	}

	LOG.info("Creating a new person: " + agent.getFullName());
	agents.add(agent);
    }

    public void save(SimulatedLocation location) {
	if (locations.size() >= 10) {
	    throw new SmallvilleException(
		    "Cannot have more than 10 locations (this is to save room for other information)");
	}

	if (getLocation(location.getName()).isPresent()) {
	    throw new SmallvilleException("Location already exists");
	}

	locations.add(location);
    }

    public Set<Agent> getAgents() {
	return agents;
    }

    public List<SimulatedLocation> getLocations() {
	List<SimulatedLocation> objects = new ArrayList<SimulatedLocation>();

	for (Location location : locations) {
	    if (location instanceof SimulatedLocation) {
		objects.add((SimulatedLocation) location);
	    }
	}

	return Collections.unmodifiableList(objects);
    }

    public Optional<SimulatedLocation> getLocation(String locationName) {
	return locations.stream().filter(location -> location.getName().equals(locationName)).findFirst();
    }

    public void save(SimulatedObject object) {
	for (SimulatedObject obj : objects) {
	    if (obj.getName().equals(object.getName())) {
		throw new SmallvilleException("Object already exists, try using a different name");
	    }
	}

	objects.add(object);
    }

    public List<SimulatedObject> getObjects() {
	return objects;
    }

    public Optional<Agent> getAgent(String name) {
	return agents.stream().filter(agent -> agent.getFullName().equals(name)).findFirst();
    }

    public List<Conversation> getAllConversations(String agent) {
	return conversations.stream().filter(conversation -> conversation.isPartOfConversation(agent)).toList();
    }

    public Conversation getLatestConversation(String agent) {
	return conversations
	    .stream()
	    .filter(conversation -> conversation.isPartOfConversation(agent))
	    .sorted(new Comparator<Conversation>() {
		@Override
		public int compare(Conversation o1, Conversation o2) {
		    return o1.createdAt().compareTo(o2.createdAt());
		}
	    })
	    .toList()
	    .get(0);
    }

    public void createConversation(String name, String other, List<Dialog> messages) {
	save(new Conversation(name, other, messages));
    }

    public void save(Conversation conversation) {
	if (conversation.size() == 0) {
	    return;
	}

	conversations.add(conversation);
    }

    public List<Conversation> getConversationsAfter(LocalDateTime time) {
	return conversations.stream().filter(conversation -> conversation.createdAt().compareTo(time) < 0).toList();
    }

    /**
     * Get the exact Simulated Object given a parent
     * 
     * @param location String formatted as Parent: Object
     * @return
     */
    public SimulatedObject getExactLocation(String location) {
	String[] parts = location.split(":");

	String locationName = parts[0].trim();
	String objectName = parts[1].trim();

	SimulatedLocation loc = (SimulatedLocation) getLocation(locationName)
	    .orElseThrow(() -> new LocationNotFoundException(locationName));

	SimulatedObject object = loc.getObject(objectName).orElseThrow(() -> new LocationNotFoundException(objectName));

	return object;
    }

    public SimulatedObject getObjectByName(String name) {
	return objects.stream().filter(obj -> obj.getName().equals(name)).findFirst().orElse(null);
    }

    public void changeObject(String object, String state) {
	SimulatedObject obj = getObjectByName(object);

	if (obj != null) {
	    LOG.info("Changing state of " + object + " from " + obj.getState() + " to " + state);
	    obj.setState(state);
	}
    }
}
