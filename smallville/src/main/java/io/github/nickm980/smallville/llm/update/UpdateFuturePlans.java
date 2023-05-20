package io.github.nickm980.smallville.llm.update;

import java.util.List;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.memory.Plan;
import io.github.nickm980.smallville.prompts.TimePhrase;

public class UpdateFuturePlans extends AgentUpdate {

    @Override
    public boolean update(ChatService converter, World world, Agent agent) {
	LOG.info("[Plans] Updating future plans");

	if (agent.getPlans().isEmpty() || agent.getPlans().size() < 5) {
	    List<Plan> future = converter.getPlans(agent, TimePhrase.DAY);
	    agent.setPlans(future);
	}

	// TODO: iterate for finer grain plans that are closer to the present

	for (Plan plan : agent.getPlans()) {
	    LOG.info("[Plans] " + agent.getFullName() + ": " + plan.asNaturalLanguage());
	}

	return next(converter, world, agent);
    }

}
