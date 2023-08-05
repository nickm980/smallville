package io.github.nickm980.smallville;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import io.github.nickm980.smallville.analytics.Analytics;
import io.github.nickm980.smallville.analytics.AnalyticsListener;
import io.github.nickm980.smallville.api.SmallvilleServer;
import io.github.nickm980.smallville.config.CommandLineArgs;
import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.events.EventBus;
import io.github.nickm980.smallville.events.agent.AgentUpdateEvent;
import io.github.nickm980.smallville.llm.ChatGPT;
import io.github.nickm980.smallville.math.SmallvilleMath;
import io.github.nickm980.smallville.nlp.LocalNLP;

public class Smallville {

    private static final Logger LOG = LoggerFactory.getLogger(Smallville.class);

    public static void main(String[] args) throws IOException {
	configureLogs();

	CommandLineArgs options = loadArgs(args);

	int port = options.getPort();
	String key = options.getApiKey();

	Settings.setApiKey(key);

	loadConfig();
	Updater.checkLatestVersion();
	
	LOG.info("Starting server...");

	LocalNLP.preLoad();
	SmallvilleMath.loadBert();

	startServer(port);

	LOG.info("Smallville server started on port " + port);
    }

    private static CommandLineArgs loadArgs(String[] args) {
	CommandLineArgs result = new CommandLineArgs();
	JCommander commands = JCommander.newBuilder().addObject(result).build();
	commands.parse(args);

	return result;
    }

    private static void loadConfig() {
	SmallvilleConfig.getConfig();
	SmallvilleConfig.getPrompts();
    }

    private static void configureLogs() {
	String runId = String.valueOf(System.currentTimeMillis());
	System.setProperty("runId", runId);
	PropertyConfigurator.configure(Smallville.class.getClassLoader().getResource("log4j.properties"));
    }

    private static void startServer(int port) {
        EventBus eventBus = EventBus.getEventBus();

        Analytics analytics = new Analytics();
        eventBus.registerListener(new AnalyticsListener(analytics));
        
	new SmallvilleServer(analytics, new ChatGPT(), new World()).start(port);
    }
}
