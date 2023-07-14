package io.github.nickm980.smallville.update;

import java.util.List;
import java.util.stream.Collectors;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.memory.Plan;
import io.github.nickm980.smallville.memory.PlanType;
import io.github.nickm980.smallville.nlp.LocalNLP;
import io.github.nickm980.smallville.nlp.NLPCoreUtils;
import io.github.nickm980.smallville.prompts.ChatService;
import io.github.nickm980.smallville.prompts.Prompts;
import io.github.nickm980.smallville.prompts.dto.Reaction;

/**
 * The UpdateFuturePlans class is responsible for creating and updating the
 * short and long term plans of an agent based on reactable observations.
 */
public class UpdatePlans extends AgentUpdate {

    @Override
    public boolean update(Prompts converter, World world, Agent agent, UpdateInfo info) {
	LOG.info("[Plans] Updating plans...");
	boolean hasPlans = !agent.getMemoryStream().getPlans().isEmpty();
	boolean shouldUpdatePlans = !hasPlans;
	String observation = info.getObservation();
	
	if (observation != null && !observation.isEmpty()) {
	    LOG.info("starting reaction to an observation");
	    Reaction reaction = converter.shouldUpdatePlans(agent, observation);
	    shouldUpdatePlans = reaction.getAnswer().toLowerCase().contains("yes");
	    info.setShouldUpdateConversation(reaction.getConversation().toLowerCase().contains("yes"));
	}

	if (shouldUpdatePlans) {
	    LOG.info("[Plans] Reacting to observation [" + info.getObservation() + "]");
	    agent.getMemoryStream().prunePlans(PlanType.LONG_TERM);
	    agent.getMemoryStream().prunePlans(PlanType.SHORT_TERM);
	    updatePlans(converter, agent, PlanType.LONG_TERM);
	    updatePlans(converter, agent, PlanType.SHORT_TERM);
	}

	if (agent.getMemoryStream().getPlans(PlanType.LONG_TERM).isEmpty()) {
	    updatePlans(converter, agent, PlanType.LONG_TERM);
	}
	
	if (agent.getMemoryStream().getPlans(PlanType.SHORT_TERM).isEmpty()) {
	    updatePlans(converter, agent, PlanType.SHORT_TERM);
	}

	LOG.info("[Plans] Plans updated");

	info.setPlansUpdated(shouldUpdatePlans);
	return next(converter, world, agent, info);
    }

    private void updatePlans(Prompts converter, Agent agent, PlanType type) {
	List<Plan> plans = type == PlanType.LONG_TERM ? converter.getPlans(agent) : converter.getShortTermPlans(agent);

	for (Plan plan : plans) {
	    plan.convert(type);
	    LOG.debug("[Plans] " + plan.getType() + " " + plan.getDescription());
	}

	agent.getMemoryStream().setPlans(plans, type);

	LOG.info("[Plans] Updated " + type.toString() + " plans");
    }
}
