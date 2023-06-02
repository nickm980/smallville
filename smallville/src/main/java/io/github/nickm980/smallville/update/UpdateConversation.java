package io.github.nickm980.smallville.update;

import java.util.List;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.memory.Observation;
import io.github.nickm980.smallville.nlp.LocalNLP;
import io.github.nickm980.smallville.nlp.NLPCoreUtils;

public class UpdateConversation extends AgentUpdate {

    private String observation;
    private static final NLPCoreUtils TOKENIZER = new LocalNLP();

    public UpdateConversation(String observation) {
	this.observation = observation;
    }

    public UpdateConversation() {

    }

    @Override
    public boolean update(IChatService service, World world, Agent agent) {
	LOG.info("[Conversation] Checking for any conversations and initating dialog");

	if (observation == null) {
	    observation = agent.getCurrentActivity();
	}

	String subject = TOKENIZER.extractLastOccurenceOfName(observation);

	if (agent.getFullName().equals(subject)) {
	    LOG.warn("[Conversation] Agent attempted to have a conversation with themself.");
	    return false;
	}

	if (subject == null) {
	    LOG.info("[Conversation] No conversation detected");
	    return true;
	}

	Agent other = world.getAgent(subject).orElseThrow();

	if (other == null) {
	    LOG.error("[Conversation] A conversation was implied but the target {" + subject + "} does not exist.");
	    return false;
	}

	Conversation conversation = service.getConversationIfExists(agent, other);

	List<Observation> memories = conversation
	    .getDialog()
	    .stream()
	    .map(dialog -> new Observation(dialog.asNaturalLanguage()))
	    .toList();

	agent.getMemoryStream().addAll(memories);
	other.getMemoryStream().addAll(memories);

	world.create(conversation);

	return next(service, world, agent);
    }
}
