package io.github.nickm980.smallville.update;

import java.util.List;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.memory.Observation;
import io.github.nickm980.smallville.entities.memory.Plan;
import io.github.nickm980.smallville.entities.memory.PlanType;

public class UpdateFuturePlans extends AgentUpdate {

    @Override
    public boolean update(IChatService converter, World world, Agent agent) {
	LOG.info("[Plans] Updating plans");
	boolean hasPlans = !agent.getMemoryStream().getPlans().isEmpty();
	boolean shouldUpdatePlans = !hasPlans;

	Observation observation = agent.getMemoryStream().getLastObservation();

	if (observation != null && observation.isReactable()) {
	    shouldUpdatePlans = converter.shouldUpdatePlans(agent, observation.getDescription());
	    if (shouldUpdatePlans) {
		agent.getMemoryStream().prunePlans(PlanType.LONG_TERM);
		agent.getMemoryStream().prunePlans(PlanType.SHORT_TERM);
		LOG.info("[Plans] Changing plans...");
	    } else {
		LOG.info("[Plans] Not changing plans...");
	    }
	}

	// initialize the agent plans if none are found
	if (shouldUpdatePlans) {
	    List<Plan> plans = converter.getPlans(agent);
	    agent.getMemoryStream().setPlans(plans, PlanType.LONG_TERM);

	    for (Plan plan : plans) {
		LOG.info("[Plans] " + plan.getType() + " " + plan.asNaturalLanguage());
	    }

	    LOG.info("[Plans] Updated long term plans");
	}

	if (agent.getMemoryStream().getPlans(PlanType.SHORT_TERM).isEmpty() || shouldUpdatePlans) {
	    List<Plan> plans = converter.getShortTermPlans(agent);

	    for (Plan plan : plans) {
		plan.convert(PlanType.SHORT_TERM);
		LOG.info("[Plans] " + plan.getType() + " " + plan.asNaturalLanguage());
	    }

	    agent.getMemoryStream().setPlans(plans, PlanType.SHORT_TERM);

	    LOG.info("[Plans] Updated short term plans");
	}

	LOG.info("[Plans] Plans updated");

	return next(converter, world, agent);
    }
}
