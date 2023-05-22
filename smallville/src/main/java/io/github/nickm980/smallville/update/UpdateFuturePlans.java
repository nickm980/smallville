package io.github.nickm980.smallville.update;

import java.util.List;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.memory.Memory;
import io.github.nickm980.smallville.models.memory.Observation;
import io.github.nickm980.smallville.models.memory.Plan;
import io.github.nickm980.smallville.models.memory.PlanType;

public class UpdateFuturePlans extends AgentUpdate {

    @Override
    public boolean update(ChatService converter, World world, Agent agent) {
	LOG.info("[Plans] Updating plans");
	List<Plan> memories = agent.getMemoryStream().prunePlans();

	List<Observation> observations = memories.stream().map(memory -> {
	    int importance = (int) memory.getImportance();
	    if (importance == 0) {
		importance = 1;
	    }
	    return new Observation(memory.asNaturalLanguage(), memory.getTime(), importance);
	}).toList();

	agent.getMemoryStream().add(observations);

	if (agent.getPlans().isEmpty() || agent.getPlans().size() < 3) {
	    List<Plan> plans = converter.getPlans(agent);
	    agent.setPlans(plans);

	    for (Plan plan : plans) {
		LOG.info("[Plans] " + plan.getType() + " " + plan.asNaturalLanguage());
	    }

	    LOG.info("[Plans] Updated long term plans");
	}

	if (agent.getMemoryStream().getPlans(PlanType.SHORT_TERM).isEmpty()) {
	    List<Plan> plans = converter.getShortTermPlans(agent);

	    for (Plan plan : plans) {
		plan.convertToShortTermMemory(true, PlanType.SHORT_TERM);
		LOG.info("[Plans] " + plan.getType() + " " + plan.asNaturalLanguage());
	    }

	    agent.setShortTermPlans(plans);

	    LOG.info("[Plans] Recursively updated short term plans");
	}

	LOG.info("[Plans] Plans updated");

	return next(converter, world, agent);
    }
}
