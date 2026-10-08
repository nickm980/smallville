package io.github.nickm980.smallville.update;

import io.github.nickm980.smallville.Util;
import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.prompts.Prompts;
import io.github.nickm980.smallville.prompts.dto.CurrentActivity;

public class UpdateCurrentActivity extends AgentUpdate {

    @Override
    public boolean update(Prompts service, World world, Agent agent, UpdateInfo info) {
	LOG.info("[Activity] Updating current activity and emoji");

	CurrentActivity activity = service.getCurrentActivity(agent);
	if (activity == null) {
	    LOG.warn("[Activity] No activity response was returned for {}", agent.getFullName());
	    return next(service, world, agent, info);
	}

	LOG.debug(activity.getLocation());
	String nextActivity = activity.getActivity();
	if (nextActivity != null && !nextActivity.isBlank()) {
	    agent.setCurrentActivity(nextActivity);
	}

	String nextEmoji = activity.getEmoji();
	if (nextEmoji != null && !nextEmoji.isBlank()) {
	    agent.setCurrentEmoji(nextEmoji);
	}

	Location nextLocation = world.resolveLocation(activity.getLocation()).orElse(agent.getLocation());
	if (nextLocation == null) {
	    LOG.warn("[Activity] Could not resolve any location for {} from [{}]", agent.getFullName(), activity.getLocation());
	} else {
	    if (activity.getLocation() != null && world.getLocation(activity.getLocation()).isEmpty()) {
		LOG.warn("[Activity] Resolved [{}] to [{}] for {}", activity.getLocation(), nextLocation.getFullPath(), agent.getFullName());
	    }
	    agent.setLocation(nextLocation);
	}

	String lastActivity = activity.getLastActivity();
	if (lastActivity != null && !lastActivity.isBlank()) {
	    agent.getMemoryStream().add(new Observation(lastActivity));
	    agent.getMemoryStream().addWorkingMemory(lastActivity);
	}

	return next(service, world, agent, info);
    }
}
