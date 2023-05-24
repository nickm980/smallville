package io.github.nickm980.smallville.api;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.nickm980.smallville.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.Util;
import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.api.dto.*;
import io.github.nickm980.smallville.entities.AccessTime;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.AgentLocation;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.ObjectState;
import io.github.nickm980.smallville.entities.SimulatedLocation;
import io.github.nickm980.smallville.entities.SimulatedObject;
import io.github.nickm980.smallville.entities.SimulationTime;
import io.github.nickm980.smallville.entities.memory.Characteristic;
import io.github.nickm980.smallville.exceptions.AgentNotFoundException;
import io.github.nickm980.smallville.exceptions.LocationNotFoundException;
import io.github.nickm980.smallville.exceptions.SmallvilleException;
import io.github.nickm980.smallville.llm.LLM;
import io.github.nickm980.smallville.update.UpdateService;

import java.util.concurrent.TimeUnit;

public class SimulationService {

    private final ModelMapper mapper;
    private final UpdateService prompts;
    private final World world;
    private final AccessTime time;
    private final Logger LOG = LoggerFactory.getLogger(SimulationService.class);

    private SimulationTime timekeeper;

    public SimulationService(LLM llm, World world) {
	this.world = world;
	this.mapper = new ModelMapper();
	this.time = new AccessTime();
	this.prompts = new UpdateService(llm, world);
	this.timekeeper = new SimulationTime();
    }

    public void createMemory(CreateMemoryRequest request) {
	if (request.isReactable()) {
	    react(request.getName(), request.getDescription());
	} else {
	    Agent agent = world.getAgent(request.getName()).orElseThrow();
	    agent.getMemoryStream().addObservation(request.getDescription());
	}
    }

    public AgentStateResponse getAgentState(String name) {
	Agent agent = world.getAgent(name).orElseThrow(() -> new AgentNotFoundException(name));
	return mapper.fromAgent(agent);
    }

    public List<AgentStateResponse> getAgents() {
	Set<Agent> agents = world.getAgents();

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
	// Location : Object
	String[] names = Util.parseLocation(request.getLocation());

	SimulatedLocation loc = world.getLocation(names[0]).orElseThrow();
	SimulatedObject obj = world.getObjectByName(names[1]);

	AgentLocation location = new AgentLocation(loc, obj);

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
	    .getAgent(pathParam)
	    .orElseThrow(() -> new AgentNotFoundException(pathParam))
	    .getMemoryStream()
	    .getMemories()
	    .stream()
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

    public void setGoal(String name, String goal) {
	world.getAgent(name).orElseThrow().setGoal(goal);
    }

    public SimulationTime getTimekeeper() {
	return timekeeper;
    }

    public void setTimestep(SetTimestepRequest request) {
	long durationValue = Long.parseLong(request.getNumOfMinutes());
	Duration duration = Duration.ofMinutes(durationValue);
	timekeeper.setTimestepDuration(duration);
    }
}
