package io.github.nickm980.smallville.llm.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.llm.response.CurrentPlan;
import io.github.nickm980.smallville.models.Agent;

public class UpdateCurrentActivity extends AgentUpdate {

    @Override
    public boolean update(ChatService service, World world, Agent agent) {
	LOG.info("[Updater / Activity] Updating current activity and emoji");

	CurrentPlan plan = service.getCurrentPlan(agent);
	agent.setCurrentActivity(plan.getCurrentActivity());
	agent.setCurrentEmoji(plan.getEmoji());
	agent.getMemoryStream().remember(plan.getLastActivity());

	return next(service, world, agent);
    }
}
