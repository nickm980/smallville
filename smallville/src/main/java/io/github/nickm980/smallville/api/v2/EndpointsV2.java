package io.github.nickm980.smallville.api.v2;

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

import io.github.nickm980.smallville.api.v1.dto.*;
import io.github.nickm980.smallville.entities.SimulationTime;
import io.javalin.Javalin;
import static io.github.nickm980.smallville.api.SmallvilleServer.*;

/**
 * Breaking changes to v1 should be placed with these endpoints. The clients
 * will not be updated until the server releases on a major version
 *
 */
public class EndpointsV2 {

    public static void register(SimulationService service, MustacheFactory mf, Javalin app) {
	app.get("/v2/dashboard", (ctx) -> {
	    Map<String, Object> model = new HashMap<>();
	    model.put("agents", service.getAgents());
	    model.put("objects", service.getChangedLocations());
	    Mustache mustache = mf.compile("index.mustache");
	    String output = mustache.execute(new StringWriter(), model).toString();
	    ctx.html(output);
	});

	app.get("/v2/dashboard/{name}", (ctx) -> {
	    Map<String, Object> model = new HashMap<>();
	    model.put("memories", service.getMemories(ctx.pathParam("name")));

	    Mustache mustache = mf.compile("agent.mustache");
	    String output = mustache.execute(new StringWriter(), model).toString();
	    ctx.html(output);
	});

	app.get("/v2/", (ctx) -> {
	    ctx.redirect("/dashboard");
	});

	app.get("/v2/info", (ctx) -> {
	    String time = SimulationTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));

//	    ctx
//		.json(Map
//		    .of("time", time, "step", (int) SimulationTime.getStepDuration().getSeconds() / 60, "prompts",
//			    AnalyticsListener.getPrompts()));
	});

	app.get("/v2/ping", (ctx) -> {
	    ctx.result("pong");
	});

	app.get("/v2/agents", (ctx) -> {
	    ctx.json(Map.of("agents", service.getAgents()));
	});

	app.get("/v2/agents/{name}", (ctx) -> {
	    AgentStateResponse res = service.getAgentState(ctx.pathParam("name"));
	    ctx.json(res);
	});

	app.post("/v2/agents/{name}/ask", (ctx) -> {
	    AskQuestionRequest request = ctx
		.bodyValidator(AskQuestionRequest.class)
		.check((req) -> exists(req.getQuestion()), "{question} cannot be blank")
		.get();

	    String res = service.askQuestion(ctx.pathParam("name"), request.getQuestion());

	    ctx.json(Map.of("answer", res));
	});

	app.post("/v2/agents/{name}/goal", (ctx) -> {
	    SetGoalRequest request = ctx
		.bodyValidator(SetGoalRequest.class)
		.check((req) -> exists(req.getGoal()), "{goal} cannot be blank")
		.get();

	    service.setGoal(ctx.pathParam("name"), request.getGoal());

	    ctx.json(Map.of("success", true));
	});

	app.post("/v2/agents", (ctx) -> {
	    CreateAgentRequest request = ctx
		.bodyValidator(CreateAgentRequest.class)
		.check((req) -> exists(req.getName()), "{name} cannot be missing")
		.check((req) -> exists(req.getActivity()), "{activity} cannot be missing")
		.check((req) -> exists(req.getLocation()), "{location} cannot be missing")
		.check((req) -> req.getMemories() != null && !req.getMemories().isEmpty(),
			"{memories} cannot be missing")
		.get();

	    service.createPerson(request);
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

	app.post("/v2/locations/{name}", (ctx) -> {
	    String location = ctx.pathParam("name");
	    ObjectMapper objectMapper = new ObjectMapper();

	    JsonNode rootNode = objectMapper.readTree(ctx.body());
	    String state = rootNode.get("state").asText();

	    service.setState(location, state);
	    ctx.json(Map.of("success", true));
	});

	app.get("/v2/locations", (ctx) -> {
	    List<LocationStateResponse> request = service.getChangedLocations();

	    ctx.json(Map.of("locations", request));
	});

	app.post("/v2/memories", (ctx) -> {
	    CreateMemoryRequest request = ctx.bodyAsClass(CreateMemoryRequest.class);
	    service.createMemory(request);

	    ctx.json(Map.of("success", true));
	});

	app.post("/v2/state", (ctx) -> {
	    service.updateState();
	    List<AgentStateResponse> agents = service.getAgents();
	    List<LocationStateResponse> locations = service.getChangedLocations();
	    List<ConversationResponse> conversations = service.getConversations();

	    ctx.json(Map.of("agents", agents, "location_states", locations, "conversations", conversations));
	});

	app.get("/v2/state", (ctx) -> {
	    List<AgentStateResponse> agents = service.getAgents();
	    List<LocationStateResponse> locations = service.getChangedLocations();
	    List<ConversationResponse> conversations = service.getConversations();

	    ctx.json(Map.of("agents", agents, "location_states", locations, "conversations", conversations));
	});

	app.post("/v2/timestep", (ctx) -> {
	    SetTimestepRequest request = ctx.bodyAsClass(SetTimestepRequest.class);
	    int minutes = Integer.valueOf(request.getNumOfMinutes());
	    SimulationTime.setStep(Duration.ofMinutes(minutes));
	    ctx.json(Map.of("success", true));
	});

	app.get("/v2/progress", (ctx) -> {
	    int progress = service.getProgress();
	    ctx.json(Map.of("max", 100, "current", progress));
	});
    }
}
