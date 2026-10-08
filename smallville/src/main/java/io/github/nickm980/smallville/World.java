package io.github.nickm980.smallville;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.exceptions.SmallvilleException;
import io.github.nickm980.smallville.repository.Repository;

/**
 * Storage for agents locations objects and conversations
 */
public class World {
    private Repository<Location> locations;
    private Repository<Conversation> conversations;
    private Repository<Agent> agents;
    private final Logger LOG = LoggerFactory.getLogger(World.class);

    public World() {
	this.locations = new Repository<>();
	this.agents = new Repository<>();
	this.conversations = new Repository<>();
    }

    public void create(Conversation conversation) {
	if (conversation.size() == 0) {
	    throw new SmallvilleException("Cannot have an empty conversation");
	}

	if (conversation.getTalker().equals(conversation.getTalkee())) {
	    throw new SmallvilleException("Agents cannot have conversations with themselves");
	}

	conversations.save(UUID.randomUUID().toString(), conversation);
    }

    public boolean create(Agent agent) {
	return agents.save(agent.getFullName(), agent);
    }

    public void create(Location location) {
	locations.save(location.getFullPath(), location);
    }

    public List<Agent> getAgents() {
	return agents.all();
    }

    public List<Location> getLocations() {
	return locations.all();
    }

    public Optional<Location> getLocation(String locationName) {
	return Optional.ofNullable(locations.getById(locationName));
    }

    public Optional<Location> resolveLocation(String locationName) {
	if (locationName == null || locationName.isBlank()) {
	    return Optional.empty();
	}

	String candidate = sanitizeLocationName(locationName);
	Optional<Location> exactMatch = getLocation(candidate);
	if (exactMatch.isPresent()) {
	    return exactMatch;
	}

	String normalizedCandidate = normalizeLocationName(candidate);
	Optional<Location> prefixMatch = locations
	    .all()
	    .stream()
	    .filter(location -> {
		String normalizedLocation = normalizeLocationName(location.getFullPath());
		return normalizedCandidate.equals(normalizedLocation)
		    || normalizedCandidate.startsWith(normalizedLocation + " ");
	    })
	    .sorted((Location first, Location second) -> Integer.compare(second.getFullPath().length(), first.getFullPath().length()))
	    .findFirst();

	if (prefixMatch.isPresent()) {
	    return prefixMatch;
	}

	List<Location> leafMatches = locations
	    .all()
	    .stream()
	    .filter(location -> {
		List<String> parts = location.getAll();
		String leaf = parts.get(parts.size() - 1);
		return normalizeLocationName(leaf).equals(normalizedCandidate);
	    })
	    .toList();

	if (leafMatches.size() == 1) {
	    return Optional.of(leafMatches.get(0));
	}

	return Optional.empty();
    }

    public Optional<Agent> getAgent(String name) {
	return Optional.ofNullable(agents.getById(name));
    }

    public List<Conversation> getConversationsAfter(LocalDateTime time) {
	if (time == null) {
	    return conversations.all();
	}

	return conversations
	    .all()
	    .stream()
	    .filter(conversation -> conversation.getCreatedAt() == null || !conversation.getCreatedAt().isBefore(time))
	    .toList();
    }

    public void setState(String object, String state) {
	Location obj = getLocation(object).orElseThrow();

	LOG.info("Changing state. " + object + ": " + state);
	obj.setState(state);
    }

    private String sanitizeLocationName(String locationName) {
	String sanitized = locationName.trim();
	sanitized = sanitized.replaceAll("^[\"']+", "");
	sanitized = sanitized.replaceAll("[\"']+$", "");
	sanitized = sanitized.replaceAll("\\s*\\([^)]*\\)\\s*$", "");
	sanitized = sanitized.replaceAll("[\\s\\.,;!]+$", "");
	return sanitized.trim();
    }

    private String normalizeLocationName(String locationName) {
	return sanitizeLocationName(locationName)
	    .replaceAll("\\s+", " ")
	    .toLowerCase(Locale.ROOT);
    }
}
