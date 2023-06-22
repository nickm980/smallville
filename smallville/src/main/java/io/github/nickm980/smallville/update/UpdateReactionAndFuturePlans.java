package io.github.nickm980.smallville.update;

import java.util.List;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.memory.Observation;
import io.github.nickm980.smallville.entities.memory.Plan;
import io.github.nickm980.smallville.entities.memory.PlanType;
import io.github.nickm980.smallville.nlp.LocalNLP;
import io.github.nickm980.smallville.nlp.NLPCoreUtils;
import io.github.nickm980.smallville.prompts.Prompts;
import io.github.nickm980.smallville.prompts.dto.Reaction;

/**
 * The UpdateFuturePlans class is responsible for creating and updating the
 * short and long term plans of an agent based on reactable observations.
 */
public class UpdateReactionAndFuturePlans extends AgentUpdate {
    private static final NLPCoreUtils TOKENIZER = new LocalNLP();
    
    @Override
    public boolean update(Prompts converter, World world, Agent agent) {
	LOG.info("[Plans] Updating plans...");
	boolean hasPlans = !agent.getMemoryStream().getPlans().isEmpty();
	boolean shouldUpdatePlans = !hasPlans;

	Observation observation = agent.getMemoryStream().getLastObservation();

	if (observation.isReactable()) {
	    Reaction reaction = converter.shouldUpdatePlans(agent, observation.getDescription());
	    shouldUpdatePlans = reaction.getAnswer().toLowerCase().contains("yes");
	    boolean shouldUpdateConversation = reaction.getConversation().toLowerCase().contains("yes");

	    if (shouldUpdateConversation) {
		String subject = TOKENIZER.extractLastOccurenceOfName(observation.getDescription());

		if (agent.getFullName().equals(subject)) {
		    LOG.warn("[Conversation] Agent attempted to have a conversation with themself.");
		    return false;
		}

		if (subject == null) {
		    LOG.info("[Conversation] No conversation detected");
		    return true;
		}

		Agent other = world.getAgent(subject).orElse(null);

		if (other == null) {
		    LOG
			.error("[Conversation] A conversation was attempted but the target {" + subject
				+ "} does not exist.");
		    return false;
		}

		Conversation conversation = converter.getConversationIfExists(agent, other, observation.getDescription());

		List<Observation> memories = conversation
		    .getDialog()
		    .stream()
		    .map(dialog -> new Observation(dialog.asNaturalLanguage()))
		    .toList();

		agent.getMemoryStream().addAll(memories);
		other.getMemoryStream().addAll(memories);

		world.create(conversation);
	    }
	}

	if (shouldUpdatePlans) {
	    LOG.info("[Plans] Reacting to observation...");
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

    private void updatePlans(Prompts converter, Agent agent, PlanType type) {
	List<Plan> plans = type == PlanType.LONG_TERM ? converter.getPlans(agent) : converter.getShortTermPlans(agent);

	for (Plan plan : plans) {
	    plan.convert(type);
	    LOG.debug("[Plans] " + plan.getType() + " " + plan.asNaturalLanguage());
	}

	agent.getMemoryStream().setPlans(plans, type);

	LOG.info("[Plans] Updated " + type.toString() + " plans");
    }
}
