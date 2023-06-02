package io.github.nickm980.smallville;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.SimulatedLocation;
import io.github.nickm980.smallville.entities.SimulatedObject;
import io.github.nickm980.smallville.repository.Repository;

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
    private Repository<SimulatedLocation> locations;
    private Repository<SimulatedObject> objects;
    private Repository<Conversation> conversations;
    private Repository<Agent> agents;
    private final Logger LOG = LoggerFactory.getLogger(World.class);

    public World() {
	this.locations = new Repository<>();
	this.agents = new Repository<>();
	this.objects = new Repository<>();
	this.conversations = new Repository<>();
    }

    public void create(Conversation conversation) {
	if (conversation.size() == 0) {
	    return;
	}

	conversations.save(UUID.randomUUID().toString(), conversation);
    }

    public void create(Agent agent) {
	agents.save(agent.getFullName(), agent);
    }

    public void create(SimulatedLocation location) {
	locations.save(location.getName(), location);
    }

    public void create(SimulatedObject object) {
	objects.save(object.getName(), object);
    }

    public List<Agent> getAgents() {
	return agents.all();
    }

    public List<SimulatedLocation> getLocations() {
	return locations.all();
    }

    public Optional<SimulatedLocation> getLocation(String locationName) {
	return Optional.ofNullable(locations.getById(locationName));
    }

    public List<SimulatedObject> getObjects() {
	return objects.all();
    }

    public Optional<Agent> getAgent(String name) {
	return Optional.ofNullable(agents.getById(name));
    }

    public List<Conversation> getConversationsAfter(LocalDateTime time) {
	return conversations.after(time).stream().toList();
    }

    public SimulatedObject getExactLocation(String location) {
	String[] parts = location.split(":");

	String locationName = parts[0].trim();
	String objectName = parts[1].trim();

	SimulatedLocation loc = getLocation(locationName).orElseThrow();
	SimulatedObject result = loc.getObject(objectName).orElseThrow();

	return result;
    }

    public SimulatedObject getObjectByName(String name) {
	return objects.getById(name);
    }

    public void setState(String object, String state) {
	SimulatedObject obj = getObjectByName(object);

	if (obj != null) {
	    LOG.info("Changing state. " + object + ": " + state);
	    obj.setState(state);
	}
    }
}
