package io.github.nickm980.smallville.api;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.nickm980.smallville.LogCache;
import io.github.nickm980.smallville.Util;
import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.api.dto.*;
import io.github.nickm980.smallville.entities.*;
import io.github.nickm980.smallville.entities.memory.Characteristic;
import io.github.nickm980.smallville.entities.memory.Observation;
import io.github.nickm980.smallville.exceptions.AgentNotFoundException;
import io.github.nickm980.smallville.exceptions.LocationNotFoundException;
import io.github.nickm980.smallville.exceptions.SmallvilleException;
import io.github.nickm980.smallville.llm.LLM;
import io.github.nickm980.smallville.update.Progress;
import io.github.nickm980.smallville.update.UpdateService;

public class SimulationService {

    private final ModelMapper mapper;
    private final UpdateService prompts;
    private final World world;
    private int progress;

    public SimulationService(LLM llm, World world) {
	this.world = world;
	this.mapper = new ModelMapper();
	this.prompts = new UpdateService(llm, world);
	this.progress = 0;
    }

    public void createMemory(CreateMemoryRequest request) {
	Agent agent = world.getAgent(request.getName()).orElseThrow();
	Observation observation = new Observation(request.getDescription());
	observation.setReactable(request.isReactable());
	agent.getMemoryStream().add(observation);

	if (observation.isReactable()) {
	    SimulationTime.update();
	    prompts.updateAgent(agent, new Progress() {
		@Override
		public void update() {
		    progress += 1;
		}
	    });
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
	// Location : Object
	String[] names = Util.parseLocation(request.getLocation());

	SimulatedLocation loc = world.getLocation(names[0]).orElseThrow();
	SimulatedObject obj = world.getObjectByName(names[1]);

	AgentLocation location = new AgentLocation(loc, obj);
	Agent agent = new Agent(request.getName(), characteristics, request.getActivity(), location);

	if (world.create(agent)) {
	    String traits = prompts.createTraitsWithCharacteristics(agent);
	    agent.setTraits(traits);
	}
    }

    public void createLocation(CreateLocationRequest request) {
	if (world.getLocation(request.getName()).isPresent()) {
	    throw new SmallvilleException("Location already exists");
	}

	world.create(new SimulatedLocation(request.getName()));
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
	LogCache.refresh();
	if (world.getAgents().size() == 0) {
	    throw new SmallvilleException("Must create an agent before changing the state");
	}

	SimulationTime.update();

	for (Agent agent : world.getAgents()) {
	    prompts.updateAgent(agent, new Progress() {
		@Override
		public void update() {
		    progress += 1;
		}
	    });
	}
    }

    public List<ConversationResponse> getConversations() {
	List<ConversationResponse> result = new ArrayList<ConversationResponse>();
	List<Conversation> conversations = world
	    .getConversationsAfter(SimulationTime.now().minus(SimulationTime.getStepDuration()));

	for (Conversation conversation : conversations) {
	    result.addAll(mapper.fromConversation(conversation));
	}
	
	return result;
    }

    public void createObject(CreateObjectRequest request) {

	if (world.getObjectByName(request.getName()) != null) {
	    throw new SmallvilleException("Object already exists");
	}

	SimulatedLocation parent = world
	    .getLocation(request.getParent())
	    .orElseThrow(() -> new LocationNotFoundException(request.getParent()));

	ObjectState state = new ObjectState(request.getState(), List.of());
	SimulatedObject object = new SimulatedObject(request.getName(), state, parent);

	world.create(object);
    }

    public void setGoal(String name, String goal) {
    }

    public void setTimestep(SetTimestepRequest request) {
	long durationValue = Long.parseLong(request.getNumOfMinutes());
	Duration duration = Duration.ofMinutes(durationValue);
	SimulationTime.setStep(duration);
    }

    public int getProgress() {
	return progress;
    }

    public void setState(String location, String state) {
	world.setState(location, state);
    }
}
