package io.github.nickm980.smallville.api.v1;

import static io.github.nickm980.smallville.api.SmallvilleServer.exists;

import java.io.StringWriter;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.github.nickm980.smallville.analytics.Analytics;
import io.github.nickm980.smallville.api.v1.dto.*;
import io.github.nickm980.smallville.entities.SimulationTime;
import io.javalin.community.routing.annotations.Endpoints;
import io.javalin.community.routing.annotations.Get;
import io.javalin.community.routing.annotations.Param;
import io.javalin.community.routing.annotations.Post;
import io.javalin.http.Context;

@Endpoints("/")
public final class SimulationController {

    private MustacheFactory mf;
    private Analytics analytics;
    private SimulationService service;
    private Gson gson = new Gson();

    public SimulationController(Analytics analytics, SimulationService service, MustacheFactory mf) {
	this.mf = mf;
	this.analytics = analytics;
	this.service = service;
    }

    
    @Get("/ping")
    public void ping(Context ctx) {
	ctx.status(200).json(Map.of("success", true, "ping", "pong"));
    }
    
    @Post("/memories/stream")
    public void createMemoryStream(Context ctx) {
	UUID uuid = service.createMemoryStream();
	ctx.json(Map.of("success", true, "uuid", uuid));
    }

    @Post("/memories/stream/{uuid}")
    public void saveMemory(Context ctx, @Param String uuidStr) {
	UUID uuid = UUID.fromString(uuidStr);

	Map<String, String> dataMap = gson.fromJson(ctx.body(), new TypeToken<Map<String, String>>() {
	}.getType());

	String query = (String) dataMap.get("query");

	List<String> result = service.getMemories(uuid, query);
	ctx.status(200).json(Map.of("success", true, "memories", result));
    }

    @Get("/memories/{name}")
    public void getMemoryByName(Context ctx, @Param String name) {
	Map<String, Object> model = new HashMap<>();
	model.put("memories", service.getMemoriesOfAgent(name));

	Mustache mustache = mf.compile("agent.mustache");
	String output = mustache.execute(new StringWriter(), model).toString();
	ctx.html(output);
    }

    @Get("/info")
    public void getInfo(Context ctx) {
	String time = SimulationTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));

	ctx
	    .json(Map
		.of("time", time, "step", SimulationTime.getStepDurationInMinutes(), "prompts",
			analytics.getPromptHistory(), "locationVisits", analytics.getVisits()));
    }

    @Get("/agents")
    public void getAgents(Context ctx) {
	ctx.json(Map.of("agents", service.getAgents()));
    }

    @Get("/agents/{name}")
    public void getAgentsByName(Context ctx, @Param String name) {
	AgentStateResponse res = service.getAgentState(name);
	ctx.json(res);
    }

    @Post("/agents/{name}/ask")
    public void askAgentQuestion(Context ctx) {
	AskQuestionRequest request = ctx
	    .bodyValidator(AskQuestionRequest.class)
	    .check((req) -> exists(req.getQuestion()), "{question} cannot be blank")
	    .get();

	String res = service.askQuestion(ctx.pathParam("name"), request.getQuestion());

	ctx.json(Map.of("answer", res));
    }

    @Post("/agents")
    public void createAgent(Context ctx) {
	CreateAgentRequest request = ctx
	    .bodyValidator(CreateAgentRequest.class)
	    .check((req) -> exists(req.getName()), "{name} cannot be missing")
	    .check((req) -> exists(req.getActivity()), "{activity} cannot be missing")
	    .check((req) -> exists(req.getLocation()), "{location} cannot be missing")
	    .check((req) -> req.getMemories() != null && !req.getMemories().isEmpty(), "{memories} cannot be missing")
	    .get();

	service.createAgent(request);
	ctx.json(Map.of("success", true));
    }

    @Post("/locations")
    public void createLocation(Context ctx) {
	CreateLocationRequest request = ctx
	    .bodyValidator(CreateLocationRequest.class)
	    .check((req) -> exists(req.getName()), "{name} cannot be missing")
	    .get();

	service.createLocation(request);
	ctx.json(Map.of("success", true));
    }

    @Post("/locations/{name}")
    public void changeLocationState(Context ctx) throws JsonMappingException, JsonProcessingException {
	String location = ctx.pathParam("name");
	ObjectMapper objectMapper = new ObjectMapper();

	JsonNode rootNode = objectMapper.readTree(ctx.body());
	String state = rootNode.get("state").asText();

	service.setState(location, state);
	ctx.json(Map.of("success", true));
    }

    @Get("/locations")
    public void getLocations(Context ctx) {
	List<LocationStateResponse> request = service.getAllLocations();

	ctx.json(Map.of("locations", request));
    }

    @Post("/memories")
    public void saveAgentMemory(Context ctx) {
	CreateMemoryRequest request = ctx.bodyAsClass(CreateMemoryRequest.class);
	service.createMemory(request);

	ctx.json(Map.of("success", true));
    }

    @Post("/state")
    public void updateState(Context ctx) {
	service.updateState();
	List<AgentStateResponse> agents = service.getAgents();
	List<LocationStateResponse> locations = service.getAllLocations();
	List<ConversationResponse> conversations = service.getConversations();

	ctx.json(Map.of("agents", agents, "location_states", locations, "conversations", conversations));
    }

    @Get("/state")
    public void getState(Context ctx) {
	List<AgentStateResponse> agents = service.getAgents();
	List<LocationStateResponse> locations = service.getAllLocations();
	List<ConversationResponse> conversations = service.getConversations();

	ctx.json(Map.of("agents", agents, "location_states", locations, "conversations", conversations));
    }

    @Post("/timestep")
    public void setTimestep(Context ctx) {
	SetTimestepRequest request = ctx.bodyAsClass(SetTimestepRequest.class);
	int minutes = Integer.valueOf(request.getNumOfMinutes());
	SimulationTime.setStep(Duration.ofMinutes(minutes));
	ctx.json(Map.of("success", true, "message", "Timestep updated to " + minutes + " per update"));
    }
}
