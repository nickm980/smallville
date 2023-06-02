package io.github.nickm980.smallville.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.AgentLocation;
import io.github.nickm980.smallville.entities.SimulatedLocation;
import io.github.nickm980.smallville.entities.SimulatedObject;
import io.github.nickm980.smallville.entities.memory.Observation;
import io.github.nickm980.smallville.prompts.dto.CurrentActivity;

public class UpdateCurrentActivity extends AgentUpdate {

    @Override
    public boolean update(IChatService service, World world, Agent agent) {
	LOG.info("[Activity] Updating current activity and emoji");

	CurrentActivity activity = service.getCurrentPlan(agent);
	SimulatedLocation location = world.getLocation(activity.getLocation()).orElse(agent.getLocation());
	SimulatedObject object = world.getObjectByName(activity.getObject());

	if (object != null) {
	    LOG.info("[Activity] Moving to: " + location.getName() + " " + object.getName());
	} else {
	    LOG.info("[Activity] Moving to: " + location.getName() + " no object");
	}
	
	agent.setLocation(new AgentLocation(location, object));
	agent.setCurrentActivity(activity.getCurrentActivity());
	agent.setCurrentEmoji(activity.getEmoji());
	agent.getMemoryStream().add(new Observation(activity.getLastActivity()));

	return next(service, world, agent);
    }
}
