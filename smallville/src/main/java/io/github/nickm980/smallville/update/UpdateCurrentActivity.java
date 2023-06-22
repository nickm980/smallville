package io.github.nickm980.smallville.update;

import io.github.nickm980.smallville.Util;
import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.AgentLocation;
import io.github.nickm980.smallville.entities.memory.Observation;
import io.github.nickm980.smallville.prompts.Prompts;
import io.github.nickm980.smallville.prompts.dto.CurrentActivity;

public class UpdateCurrentActivity extends AgentUpdate {

    @Override
    public boolean update(Prompts service, World world, Agent agent) {
	LOG.info("[Activity] Updating current activity and emoji");

	CurrentActivity activity = service.getCurrentActivity(agent);
	LOG.debug(activity.getLocation());
	agent.setCurrentActivity(activity.getActivity());
	agent.setCurrentEmoji(activity.getEmoji());
	
	String[] location = Util.parseLocation(activity.getLocation());

	agent
	    .setLocation(new AgentLocation(world.getLocation(location[0]).orElseThrow(),
		    world.getObjectByName(location[1])));
	agent.getMemoryStream().add(new Observation(activity.getLastActivity()));

	return next(service, world, agent);
    }
}
