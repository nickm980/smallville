package io.github.nickm980.smallville.api.server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.api.AgentStateResponse;
import io.github.nickm980.smallville.api.ConversationResponse;
import io.github.nickm980.smallville.api.CreateAgentRequest;
import io.github.nickm980.smallville.api.CreateLocationRequest;
import io.github.nickm980.smallville.api.CreateMemoryRequest;
import io.github.nickm980.smallville.api.CreateObjectRequest;
import io.github.nickm980.smallville.api.LocationStateResponse;
import io.github.nickm980.smallville.api.MemoryResponse;
import io.github.nickm980.smallville.api.ModelMapper;
import io.github.nickm980.smallville.exceptions.AgentNotFoundException;
import io.github.nickm980.smallville.exceptions.LocationNotFoundException;
import io.github.nickm980.smallville.exceptions.SmallvilleException;
import io.github.nickm980.smallville.llm.PromptService;
import io.github.nickm980.smallville.llm.api.LLM;
import io.github.nickm980.smallville.models.AccessTime;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.Conversation;
import io.github.nickm980.smallville.models.ObjectState;
import io.github.nickm980.smallville.models.SimulatedLocation;
import io.github.nickm980.smallville.models.SimulatedObject;
import io.github.nickm980.smallville.models.memory.Characteristic;

public class SimulationService {

    private final ModelMapper mapper;
    private final PromptService prompts;
    private final World world;
    private final AccessTime time;

    public SimulationService(LLM llm, World world) {
	this.world = world;
	this.mapper = new ModelMapper();
	this.time = new AccessTime();
	this.prompts = new PromptService(llm, world);
    }

    public void createMemory(CreateMemoryRequest request) {
	if (request.isReactable()) {
	    react(request.getName(), request.getDescription());
	} else {
	    Agent agent = world.getAgent(request.getName()).orElseThrow();
	    agent.getMemoryStream().remember(request.getDescription());
	}
    }

    public AgentStateResponse getAgentState(String name) {
	Agent agent = world.getAgent(name).orElseThrow(() -> new AgentNotFoundException(name));
	return mapper.fromAgent(agent);
    }

    public List<AgentStateResponse> getAgents() {
	List<Agent> agents = world.getAgents();

	return agents.stream().map(mapper::fromAgent).toList();
    }

    public List<LocationStateResponse> getChangedLocations() {
	List<LocationStateResponse> result = new ArrayList<LocationStateResponse>();

	for (SimulatedObject location : world.getObjects()) {
	    result.add(mapper.fromLocation(location));
	}

	return result;
    }

    public void createPerson(CreateAgentRequest request) {
	List<Characteristic> characteristics = request.getMemories().stream().map(c -> new Characteristic(c)).toList();

	SimulatedLocation location = world
	    .getLocation(request.getLocation())
	    .orElseThrow(() -> new LocationNotFoundException(request.getLocation()));

	world.save(new Agent(request.getName(), characteristics, request.getActivity(), location));
    }

    public void createLocation(CreateLocationRequest request) {
	if (world.getLocation(request.getName()).isPresent()) {
	    throw new SmallvilleException("Location already exists");
	}

	world.save(new SimulatedLocation(request.getName()));
    }

    public List<MemoryResponse> getMemories(String pathParam) {
	List<MemoryResponse> result = world
	    .getAgents()
	    .stream()
	    .flatMap(agent -> agent.getMemoryStream().getMemories().stream())
	    .map(mapper::fromMemory)
	    .sorted(Comparator.comparing(MemoryResponse::getTime, Comparator.nullsLast(Comparator.naturalOrder())))
	    .collect(Collectors.toList());

	return result;
    }

    public String askQuestion(String name, String question) {
	Agent agent = world.getAgent(name).orElseThrow(() -> new AgentNotFoundException(name));
	String result = prompts.ask(agent, question);

	return result;
    }

    public void updateState() throws SmallvilleException {
	if (world.getAgents().size() == 0) {
	    throw new SmallvilleException("Must create an agent before changing the state");
	}

	time.update();
	for (Agent agent : world.getAgents()) {
	    prompts.updateAgent(agent);
	}
    }

    private void react(String name, String observation) {
	time.update();
	Agent agent = world.getAgent(name).orElseThrow(() -> new AgentNotFoundException(name));
	prompts.updateAgent(agent, observation);
    }

    public List<ConversationResponse> getConversations() {
	List<Conversation> conversations = world.getConversationsAfter(time.getLastAccessed());

	return conversations.stream().map(mapper::fromConversation).toList();
    }

    public void createObject(CreateObjectRequest request) {
	SimulatedLocation parent = world
	    .getLocation(request.getParent())
	    .orElseThrow(() -> new LocationNotFoundException(request.getParent()));

	ObjectState state = new ObjectState(request.getState(), List.of());
	SimulatedObject object = new SimulatedObject(request.getName(), state, parent);

	world.save(object);
    }
}
