package io.github.nickm980.smallville.update;

import java.util.List;
import java.util.stream.Collectors;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.Dialog;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.nlp.LocalNLP;
import io.github.nickm980.smallville.nlp.NLPCoreUtils;
import io.github.nickm980.smallville.prompts.Prompts;

public class UpdateConversation extends AgentUpdate {
    private static final NLPCoreUtils TOKENIZER = new LocalNLP();

    @Override
    protected boolean update(Prompts converter, World world, Agent agent, UpdateInfo info) {
	if (info.shouldUpdateConversation()) {
	    updateConversation(agent, converter, world, info.getObservation());
	}
	
	return next(converter, world, agent, info);
    }
    
    private boolean updateConversation(Agent agent, Prompts converter, World world, String observation) {
	String subject = TOKENIZER.extractLastOccurenceOfName(observation);

	if (agent.getFullName().equals(subject)) {
	    LOG.warn("[Conversation] Agent attempted to have a conversation with themself.");
	    return false;
	}

	if (subject == null) {
	    LOG.warn("[Conversation] Conversation detected but no subject found. Attempting isolated dialogue");
	    Dialog dialog =  converter.saySomething(agent, observation);
	    LOG.info(agent.getFullName() + " said " + dialog.getMessage());
	    return true;
	}

	Agent other = world.getAgent(subject).orElse(null);

	if (other == null) {
	    LOG.error("[Conversation] A conversation was attempted but the target {" + subject + "} does not exist.");
	    return false;
	}

	Conversation conversation = converter.getConversationIfExists(agent, other, observation);

	List<Observation> memories = conversation
	    .getDialog()
	    .stream()
	    .map(dialog -> new Observation(dialog.getMessage()))
	    .collect(Collectors.toList());

	agent.getMemoryStream().addAll(memories);
	other.getMemoryStream().addAll(memories);

	world.create(conversation);
	return false;
    }

}
