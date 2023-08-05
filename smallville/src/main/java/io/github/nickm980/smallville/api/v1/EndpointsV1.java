package io.github.nickm980.smallville.api.v1;

import java.io.StringWriter;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import io.github.nickm980.smallville.analytics.Analytics;
import io.github.nickm980.smallville.api.v1.dto.*;
import io.github.nickm980.smallville.entities.SimulationTime;
import io.javalin.Javalin;
import static io.github.nickm980.smallville.api.SmallvilleServer.*;

public class EndpointsV1 {

    public static void register(Analytics analytics, SimulationService service, MustacheFactory mf, Javalin app) {
	app.updateConfig(config -> {
	    config.staticFiles.add("./");
	});

	app.get("/memories/{name}", (ctx) -> {
	    Map<String, Object> model = new HashMap<>();
	    model.put("memories", service.getMemories(ctx.pathParam("name")));

	    Mustache mustache = mf.compile("agent.mustache");
	    String output = mustache.execute(new StringWriter(), model).toString();
	    ctx.html(output);
	});

	app.get("/info", (ctx) -> {
	    String time = SimulationTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));

	    ctx
		.json(Map
		    .of("time", time, "step", SimulationTime.getStepDurationInMinutes(), "prompts",
			    analytics.getPromptHistory(), "locationVisits", analytics.getVisits()));
	});

	app.get("/ping", (ctx) -> {
	    ctx.result("pong");
	});

	app.get("/agents", (ctx) -> {
	    ctx.json(Map.of("agents", service.getAgents()));
	});

	app.get("/agents/{name}", (ctx) -> {
	    AgentStateResponse res = service.getAgentState(ctx.pathParam("name"));
	    ctx.json(res);
	});

	app.post("/agents/{name}/ask", (ctx) -> {
	    AskQuestionRequest request = ctx
		.bodyValidator(AskQuestionRequest.class)
		.check((req) -> exists(req.getQuestion()), "{question} cannot be blank")
		.get();

	    String res = service.askQuestion(ctx.pathParam("name"), request.getQuestion());

	    ctx.json(Map.of("answer", res));
	});

	app.post("/agents", (ctx) -> {
	    CreateAgentRequest request = ctx
		.bodyValidator(CreateAgentRequest.class)
		.check((req) -> exists(req.getName()), "{name} cannot be missing")
		.check((req) -> exists(req.getActivity()), "{activity} cannot be missing")
		.check((req) -> exists(req.getLocation()), "{location} cannot be missing")
		.check((req) -> req.getMemories() != null && !req.getMemories().isEmpty(),
			"{memories} cannot be missing")
		.get();

	    service.createAgent(request);
	    ctx.json(Map.of("success", true));
	});

	app.post("/locations", (ctx) -> {
	    CreateLocationRequest request = ctx
		.bodyValidator(CreateLocationRequest.class)
		.check((req) -> exists(req.getName()), "{name} cannot be missing")
		.get();

	    service.createLocation(request);
	    ctx.json(Map.of("success", true));
	});

	app.post("/locations/{name}", (ctx) -> {
	    String location = ctx.pathParam("name");
	    ObjectMapper objectMapper = new ObjectMapper();

	    JsonNode rootNode = objectMapper.readTree(ctx.body());
	    String state = rootNode.get("state").asText();

	    service.setState(location, state);
	    ctx.json(Map.of("success", true));
	});

	app.get("/locations", (ctx) -> {
	    List<LocationStateResponse> request = service.getAllLocations();

	    ctx.json(Map.of("locations", request));
	});

	app.post("/memories", (ctx) -> {
	    CreateMemoryRequest request = ctx.bodyAsClass(CreateMemoryRequest.class);
	    service.createMemory(request);

	    ctx.json(Map.of("success", true));
	});

	app.post("/state", (ctx) -> {
	    service.updateState();
	    List<AgentStateResponse> agents = service.getAgents();
	    List<LocationStateResponse> locations = service.getAllLocations();
	    List<ConversationResponse> conversations = service.getConversations();

	    ctx.json(Map.of("agents", agents, "location_states", locations, "conversations", conversations));
	});

	app.get("/state", (ctx) -> {
	    List<AgentStateResponse> agents = service.getAgents();
	    List<LocationStateResponse> locations = service.getAllLocations();
	    List<ConversationResponse> conversations = service.getConversations();

	    ctx.json(Map.of("agents", agents, "location_states", locations, "conversations", conversations));
	});

	// Set how many minutes each state update will increment time by
	app.post("/timestep", (ctx) -> {
	    SetTimestepRequest request = ctx.bodyAsClass(SetTimestepRequest.class);
	    int minutes = Integer.valueOf(request.getNumOfMinutes());
	    SimulationTime.setStep(Duration.ofMinutes(minutes));
	    ctx.json(Map.of("success", true, "message", "Timestep updated to " + minutes + " per update"));
	});

	app.get("/progress", (ctx) -> {
	    int progress = service.getProgress();
	    ctx.json(Map.of("max", 100, "current", progress));
	});

	app.get("/conversations", (ctx) -> {
	    ctx.json(Map.of("messages", service.getConversations()));
	});
    }
}
