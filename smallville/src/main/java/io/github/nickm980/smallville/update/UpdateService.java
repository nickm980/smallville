package io.github.nickm980.smallville.update;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.config.simulation.SimulationFile;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.entities.SimulationTime;
import io.github.nickm980.smallville.events.EventBus;
import io.github.nickm980.smallville.events.agent.AgentUpdateEvent;
import io.github.nickm980.smallville.llm.LLM;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.prompts.ChatService;
import io.github.nickm980.smallville.prompts.dto.WorldProposalCandidate;
import io.github.nickm980.smallville.runtime.RuntimeSettingsService;

/**
 * 
 * This class represents a service that handles the updating of agents
 * 
 * It provides methods for updating an agent's state based on different types of
 * observations as well as a method for asking a question to an agent
 */
public class UpdateService {

    private final World world;
    private final ChatService chatService;
    private final Logger LOG = LoggerFactory.getLogger(UpdateService.class);
    private final EventBus events = EventBus.getEventBus();
    
    public UpdateService(LLM chat, World world) {
	this(chat, world, null, new RuntimeSettingsService(SmallvilleConfig.getConfig()));
    }

    public UpdateService(LLM chat, World world, SimulationFile simulationSeed, RuntimeSettingsService runtimeSettings) {
	this.world = world;
	this.chatService = new ChatService(world, chat, simulationSeed, runtimeSettings);
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
	LOG
	    .info("Starting update for " + agent.getFullName() + " at time "
		    + SimulationTime
			.now()
			.format(DateTimeFormatter.ofPattern(SmallvilleConfig.getConfig().getTimeFormat())));
	
	Location oldLocation = agent.getLocation();
	
	AgentUpdate update = new UpdateMemoryWeights()
	    .setNext(new UpdatePlans())
	    .setNext(new UpdateCurrentActivity())
	    .setNext(new UpdateConversation())
	    .setNext(new UpdateReflection());

	update.start(chatService, world, agent, new UpdateInfo());

	events.postEvent(new AgentUpdateEvent(agent, oldLocation, agent.getLocation()));
	LOG.info("Agent updated");
    }

    public void react(Agent agent, String observation) {
	LOG.info("Starting reaction for " + agent.getFullName());

	UpdateInfo info = new UpdateInfo();
	info.setObservation(observation);
	Location oldLocation = agent.getLocation();

	AgentUpdate update = new UpdatePlans().setNext(new UpdateConversation());

	update.start(chatService, world, agent, info);

	if (info.isPlansUpdated()) {
	    update = new UpdateCurrentActivity();
	    update.start(chatService, world, agent, info);
	}

	events.postEvent(new AgentUpdateEvent(agent, oldLocation, agent.getLocation()));
	LOG.info("Agent updated");
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

    public String createTraitsWithCharacteristics(Agent agent) {
	return chatService.createTraits(agent);
    }

    public boolean startAmbientConversation(Agent agent, Agent other, String topic) {
	Conversation conversation = chatService.getConversationIfExists(agent, other, topic);
	if (conversation == null || conversation.size() == 0) {
	    return false;
	}

	List<Observation> memories = conversation
	    .getDialog()
	    .stream()
	    .map(dialog -> new Observation(dialog.getMessage()))
	    .collect(Collectors.toList());

	agent.getMemoryStream().addAll(memories);
	other.getMemoryStream().addAll(memories);
	if (!memories.isEmpty()) {
	    Observation latestDialog = memories.get(memories.size() - 1);
	    agent.getMemoryStream().addWorkingMemory(latestDialog);
	    other.getMemoryStream().addWorkingMemory(latestDialog);
	}
	world.create(conversation);
	return true;
    }

    public WorldProposalCandidate createWorldProposal(Agent agent) {
	return chatService.createWorldProposal(agent);
    }
}
