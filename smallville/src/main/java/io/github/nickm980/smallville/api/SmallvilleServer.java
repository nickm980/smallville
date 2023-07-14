package io.github.nickm980.smallville.api;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.api.v1.EndpointsV1;
import io.github.nickm980.smallville.api.v1.SimulationService;
import io.github.nickm980.smallville.llm.LLM;
import io.javalin.Javalin;

public class SmallvilleServer {

    private final SimulationService service;
    private MustacheFactory mf;

    public SmallvilleServer(LLM llm, World sim) {
	this.service = new SimulationService(llm, sim);
	this.mf = new DefaultMustacheFactory();
    }

    public void start() {
	start(8080);
    }

    public void start(int port) {
	Javalin app = Javalin.create(config -> {
	    config.showJavalinBanner = false;
	    config.staticFiles.add("./");
	    config.plugins.enableCors(cors -> {
		cors.add(it -> {
		    it.anyHost();
		});
	    });
	});

	EndpointsV1.register(service, mf, app);
	ExceptionRoutes.registerExceptions(app);

	app.start(port);
    }

    public static boolean exists(String s) {
	return s != null && !s.isBlank();
    }
}
