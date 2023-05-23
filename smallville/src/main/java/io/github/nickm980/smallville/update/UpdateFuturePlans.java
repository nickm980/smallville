package io.github.nickm980.smallville.update;

import java.util.List;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.memory.Plan;
import io.github.nickm980.smallville.entities.memory.PlanType;

public class UpdateFuturePlans extends AgentUpdate {

    @Override
    public boolean update(IChatService converter, World world, Agent agent) {
	LOG.info("[Plans] Updating plans");
	List<Plan> memories = agent.getMemoryStream().prunePlans();

	LOG.info("[Plans] Pruned " + memories.size() + " old plans");

	agent.getMemoryStream().add(converter.convertFuturePlansToMemories(memories));

	if (agent.getPlans().isEmpty() || agent.getPlans().size() < 3) {
	    LOG.info("[Plans] Updating long term plans");

	    List<Plan> plans = converter.getPlans(agent);
	    agent.addPlans(plans);

	    for (Plan plan : plans) {
		LOG.info("[Plans] " + plan.getType() + " " + plan.asNaturalLanguage());
	    }

	    LOG.info("[Plans] Updated long term plans");
	}

	if (agent.getMemoryStream().getPlans(PlanType.SHORT_TERM).isEmpty()) {
	    LOG.info("[Plans] Updating short term plans");

	    List<Plan> plans = converter.getShortTermPlans(agent);

	    for (Plan plan : plans) {
		plan.convertToShortTermMemory(true, PlanType.SHORT_TERM);
		LOG.info("[Plans] " + plan.getType() + " " + plan.asNaturalLanguage());
	    }

	    agent.getMemoryStream().setShortTermPlans(plans);

	    LOG.info("[Plans] Recursively updated short term plans");
	}

	LOG.info("[Plans] Plans updated");

	return next(converter, world, agent);
    }
}
