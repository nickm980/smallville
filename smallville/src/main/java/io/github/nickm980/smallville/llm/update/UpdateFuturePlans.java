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
	agent.getMemoryStream().prunePlans();

	if (agent.getPlans().isEmpty() || agent.getPlans().size() < 5) {
	    List<Plan> plans = converter.getPlans(agent);
	    agent.setPlans(plans);
	}

	if (agent.getMemoryStream().getShortTermPlans().size() < 5) {
	    List<Plan> plans = converter.getShortTermPlans(agent);

	    for (Plan plan : plans) {
		plan.convertToShortTermMemory(true);
	    }

	    agent.setShortTermPlans(plans);
	}

	for (Plan plan : agent.getMemoryStream().sortByTime(agent.getPlans()).stream().map(m -> (Plan) m).toList()) {
	    LOG.info("[Plans] " + plan.asNaturalLanguage() + " short term: " + plan.isShortTerm());
	}

	return next(converter, world, agent);
    }
}
