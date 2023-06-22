package io.github.nickm980.smallville.update;

import java.util.List;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.memory.Observation;
import io.github.nickm980.smallville.entities.memory.Plan;
import io.github.nickm980.smallville.entities.memory.PlanType;

/**
 * The UpdateFuturePlans class is responsible for creating and updating the
 * short and long term plans of an agent based on reactable observations.
 */
public class UpdateFuturePlans extends AgentUpdate {

    @Override
    public boolean update(IChatService converter, World world, Agent agent) {
	LOG.info("[Plans] Updating plans");
	boolean hasPlans = !agent.getMemoryStream().getPlans().isEmpty();
	boolean shouldUpdatePlans = !hasPlans;

	Observation observation = agent.getMemoryStream().getLastObservation();

	if (observation.isReactable()) {
	    LOG.info("Testing if plan should react");
	    shouldUpdatePlans = converter.shouldUpdatePlans(agent, observation.getDescription());
	}

	if (shouldUpdatePlans) {
	    LOG.info("[Plans] Changing plans...");
	    agent.getMemoryStream().prunePlans(PlanType.LONG_TERM);
	    agent.getMemoryStream().prunePlans(PlanType.SHORT_TERM);
	    updatePlans(converter, agent, PlanType.LONG_TERM);
	    updatePlans(converter, agent, PlanType.SHORT_TERM);
	}

	if (agent.getMemoryStream().getPlans(PlanType.SHORT_TERM).isEmpty()) {
	    updatePlans(converter, agent, PlanType.SHORT_TERM);
	}

	LOG.info("[Plans] Plans updated");

	return next(converter, world, agent);
    }

    private void updatePlans(IChatService converter, Agent agent, PlanType type) {
	List<Plan> plans = type == PlanType.LONG_TERM ? converter.getPlans(agent) : converter.getShortTermPlans(agent);

	for (Plan plan : plans) {
	    plan.convert(type);
	    LOG.info("[Plans] " + plan.getType() + " " + plan.asNaturalLanguage());
	}

	agent.getMemoryStream().setPlans(plans, type);

	LOG.info("[Plans] Updated " + type.toString() + " plans");
    }
}
