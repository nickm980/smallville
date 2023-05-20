package io.github.nickm980.smallville.llm.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.AgentLocation;
import io.github.nickm980.smallville.models.SimulatedLocation;
import io.github.nickm980.smallville.prompts.response.CurrentPlan;

public class UpdateCurrentActivity extends AgentUpdate {

    @Override
    public boolean update(ChatService service, World world, Agent agent) {
	LOG.info("[Updater / Activity] Updating current activity and emoji");

	CurrentPlan plan = service.getCurrentPlan(agent);
	SimulatedLocation location = world.getLocation(plan.getLocation()).orElse(agent.getLocation());

	agent.setLocation(new AgentLocation(location));
	agent.setCurrentActivity(plan.getCurrentActivity());
	agent.setCurrentEmoji(plan.getEmoji());
	agent.getMemoryStream().remember(plan.getLastActivity());

	return next(service, world, agent);
    }
}
