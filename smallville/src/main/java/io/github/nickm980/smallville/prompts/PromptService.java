package io.github.nickm980.smallville.prompts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.exceptions.SmallvilleException;
import io.github.nickm980.smallville.llm.LLM;
import io.github.nickm980.smallville.llm.update.AgentUpdate;
import io.github.nickm980.smallville.llm.update.ChatService;
import io.github.nickm980.smallville.llm.update.UpdateConversation;
import io.github.nickm980.smallville.llm.update.UpdateCurrentActivity;
import io.github.nickm980.smallville.llm.update.UpdateFuturePlans;
import io.github.nickm980.smallville.llm.update.UpdateLocations;
import io.github.nickm980.smallville.llm.update.UpdateMemoryWeights;
import io.github.nickm980.smallville.llm.update.UpdateReaction;
import io.github.nickm980.smallville.models.Agent;

/**
 * 
 * This class represents a service that handles the updating of agents
 * 
 * It provides methods for updating an agent's state based on different types of
 * observations as well as a method for asking a question to an agent
 */
public class PromptService {

    private final World world;
    private final ChatService chatService;
    private final Logger LOG = LoggerFactory.getLogger(PromptService.class);
    private boolean updateInProgress;

    public PromptService(LLM chat, World world) {
	this.updateInProgress = false;
	this.world = world;
	this.chatService = new ChatService(world, chat);
    }

    /**
     * Update the agents future plans, memory weights, and current activity /
     * location / emoji. Everything that needs to be done when updating an agent
     * automatically is done here
     * <p>
     * 
     * @param agent
     */
    public void updateAgent(Agent agent) {
	LOG.info("Starting update for " + agent.getFullName());

	if (updateInProgress) {
	    throw new SmallvilleException("Cannot update agetns at same time");
	}

	updateInProgress = true;

	AgentUpdate update = new UpdateMemoryWeights()
	    .setNext(new UpdateFuturePlans())
	    .setNext(new UpdateCurrentActivity())
	    .setNext(new UpdateMemoryWeights())
	    .setNext(new UpdateLocations());

	update.start(chatService, world, agent);

	LOG.info("Agent updated");
	updateInProgress = false;
    }

    /**
     * 
     * Updates an agent's memory weights, conversation, and reaction based on the
     * given observation string.
     * 
     * @param agent       The agent to update
     * 
     * @param observation The observation string that contains information about the
     *                    agent's state
     * 
     */
    public void updateAgent(Agent agent, String observation) {
	if (updateInProgress) {
	    throw new SmallvilleException("Cannot update agetns at same time");
	}

	updateInProgress = true;

	AgentUpdate updater = new UpdateMemoryWeights()
	    .setNext(new UpdateReaction(observation))
	    .setNext(new UpdateConversation(observation))
	    .setNext(new UpdateMemoryWeights());

	updater.start(chatService, world, agent);

	LOG.info("Agent updated");
	updateInProgress = false;
    }

    /**
     * 
     * Asks a question to an agent and returns the response.
     * 
     * @param agent    The agent to ask the question to
     * @param question The question to ask
     * @return The response from the agent
     */
    public String ask(Agent agent, String question) {
	return chatService.ask(agent, question);
    }
}
