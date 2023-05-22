package io.github.nickm980.smallville.api.server;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.api.AgentStateResponse;
import io.github.nickm980.smallville.api.AskQuestionRequest;
import io.github.nickm980.smallville.api.ConversationResponse;
import io.github.nickm980.smallville.api.CreateAgentRequest;
import io.github.nickm980.smallville.api.CreateLocationRequest;
import io.github.nickm980.smallville.api.CreateMemoryRequest;
import io.github.nickm980.smallville.api.CreateObjectRequest;
import io.github.nickm980.smallville.api.LocationStateResponse;
import io.github.nickm980.smallville.llm.LLM;
import io.javalin.Javalin;

public class SmallvilleServer {

    private final SimulationService service;
    private MustacheFactory mf = new DefaultMustacheFactory();

    public SmallvilleServer(LLM llm, World sim) {
	this.service = new SimulationService(llm, sim);
    }

    public void start() {
	start(8080);
    }

    public void start(int port) {
	Javalin app = Javalin.create(config -> {
	    config.showJavalinBanner = false;

	    config.plugins.enableCors(cors -> {
		cors.add(it -> {
		    it.anyHost();
		});
	    });
	});

	ExceptionRoutes.registerExceptions(app);

	app.get("/dashboard", (ctx) -> {
	    Map<String, Object> model = new HashMap<>();
	    model.put("agents", service.getAgents());
	    model.put("objects", service.getChangedLocations());
	    Mustache mustache = mf.compile("index.mustache");
	    String output = mustache.execute(new StringWriter(), model).toString();
	    ctx.html(output);
	});

	app.get("/dashboard/{name}", (ctx) -> {
	    Map<String, Object> model = new HashMap<>();
	    model.put("memories", service.getMemories(ctx.pathParam("name")));

	    Mustache mustache = mf.compile("agent.mustache");
	    String output = mustache.execute(new StringWriter(), model).toString();
	    ctx.html(output);
	});

	app.get("/", (ctx) -> {
	    ctx.redirect("/dashboard");
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

	app.post("/objects", (ctx) -> {
	    CreateObjectRequest request = ctx
		.bodyValidator(CreateObjectRequest.class)
		.check((req) -> exists(req.getName()), "{name} cannot be missing")
		.check((req) -> exists(req.getParent()), "{parent} location cannot be missing")
		.check((req) -> exists(req.getState()), "{state} cannot be missing")
		.get();

	    service.createObject(request);
	    ctx.json(Map.of("success", true));
	});

	app.get("/locations", (ctx) -> {
	    List<LocationStateResponse> request = service.getChangedLocations();

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
	    List<LocationStateResponse> locations = service.getChangedLocations();
	    List<ConversationResponse> conversations = service.getConversations();

	    ctx.json(Map.of("agents", agents, "location_states", locations, "conversations", conversations));
	});

	app.get("/state", (ctx) -> {
	    List<AgentStateResponse> agents = service.getAgents();
	    List<LocationStateResponse> locations = service.getChangedLocations();
	    List<ConversationResponse> conversations = service.getConversations();

	    ctx.json(Map.of("agents", agents, "location_states", locations, "conversations", conversations));
	});

	app.start(port);
    }

    private boolean exists(String s) {
	return s != null && !s.isBlank();
    }
}
