package io.github.nickm980.smallville.update;

import java.util.List;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.memory.Plan;
import io.github.nickm980.smallville.models.memory.PlanType;

public class UpdateFuturePlans extends AgentUpdate {

    @Override
    public boolean update(ChatService converter, World world, Agent agent) {
	LOG.info("[Plans] Updating future plans");
	agent.getMemoryStream().prunePlans();

	if (agent.getPlans().isEmpty() || agent.getPlans().size() < 3) {
	    List<Plan> plans = converter.getPlans(agent);
	    agent.setPlans(plans);
	}
	LOG.info("[Plans] Updated long term plans");

	if (agent.getMemoryStream().getPlans(PlanType.MID_TERM).isEmpty()) {
	    List<Plan> plans = converter.getMidTermPlans(agent);

	    for (Plan plan : plans) {
		plan.convertToShortTermMemory(true, PlanType.MID_TERM);
	    }

	    agent.getMemoryStream().addPlans(plans);
	}
	LOG.info("[Plans] Recursively updated mid term plans");

	if (agent.getMemoryStream().getPlans(PlanType.SHORT_TERM).isEmpty()) {
	    List<Plan> plans = converter.getShortTermPlans(agent);

	    for (Plan plan : plans) {
		plan.convertToShortTermMemory(true, PlanType.SHORT_TERM);
	    }

	    agent.setShortTermPlans(plans);
	}

	LOG.info("[Plans] Recursively updated short term plans");

	for (Plan plan : agent.getMemoryStream().sortByTime(agent.getPlans()).stream().map(m -> (Plan) m).toList()) {
	    LOG.info("[Plans] " + plan.asNaturalLanguage() + " short term: " + plan.isShortTerm());
	}

	LOG.info("[Plans] Plans updated");

	return next(converter, world, agent);
    }
}
