package io.github.nickm980.smallville.api;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.analytics.Analytics;
import io.github.nickm980.smallville.api.v1.SimulationController;
import io.github.nickm980.smallville.api.v1.SimulationService;
import io.github.nickm980.smallville.llm.LLM;
import io.javalin.Javalin;
import io.javalin.community.routing.annotations.AnnotatedRoutingPlugin;

public class SmallvilleServer {

    private final SimulationService service;
    private MustacheFactory mf;
    private Analytics analytics;
    private Javalin server;

    public SmallvilleServer(Analytics analytics, LLM llm, World sim) {
	this.service = new SimulationService(llm, sim);
	this.mf = new DefaultMustacheFactory();
	this.analytics = analytics;
	this.server = Javalin.create(config -> {
	    config.showJavalinBanner = false;
	    config.plugins.enableCors(cors -> {
		cors.add(it -> {
		    it.anyHost();
		});
	    });
	    AnnotatedRoutingPlugin routes = new AnnotatedRoutingPlugin();
	    routes.registerEndpoints(new SimulationController(analytics, service, mf));

	    config.plugins.register(routes);
	});

    }

    public SmallvilleServer start() {
	return start(8080);
    }

    public SmallvilleServer start(int port) {
	server.start(port);
	return this;
    }

    public static boolean exists(String s) {
	return s != null && !s.isBlank();
    }

    public Javalin server() {
	return server;
    }
}
