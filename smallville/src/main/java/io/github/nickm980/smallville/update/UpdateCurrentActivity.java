package io.github.nickm980.smallville.update;

import io.github.nickm980.smallville.Util;
import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.prompts.Prompts;
import io.github.nickm980.smallville.prompts.dto.CurrentActivity;

public class UpdateCurrentActivity extends AgentUpdate {

    @Override
    public boolean update(Prompts service, World world, Agent agent, UpdateInfo info) {
	LOG.info("[Activity] Updating current activity and emoji");

	CurrentActivity activity = service.getCurrentActivity(agent);
	LOG.debug(activity.getLocation());
	agent.setCurrentActivity(activity.getActivity());
	agent.setCurrentEmoji(activity.getEmoji());

	agent.setLocation(world.getLocation(activity.getLocation()).orElseThrow());
	agent.getMemoryStream().add(new Observation(activity.getLastActivity()));

	return next(service, world, agent, info);
    }
}
