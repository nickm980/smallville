package io.github.nickm980.smallville;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import io.github.nickm980.smallville.api.server.SmallvilleServer;
import io.github.nickm980.smallville.config.CommandLineArgs;
import io.github.nickm980.smallville.config.Config;
import io.github.nickm980.smallville.llm.ChatGPT;

public class Smallville {

    private static final Logger LOG = LoggerFactory.getLogger(Smallville.class);

    public static void main(String[] args) throws IOException {
	configureLogs();

	CommandLineArgs options = loadArgs(args);

	int port = options.getPort();
	String key = options.getApiKey();

	Settings.setApiKey(key);

	startServer(port);

	LOG.info("Smallville server started on port " + port);

	loadConfig();
    }

    private static CommandLineArgs loadArgs(String[] args) {
	CommandLineArgs result = new CommandLineArgs();
	JCommander commands = JCommander.newBuilder().addObject(result).build();
	commands.parse(args);

	return result;
    }

    private static void loadConfig() {
	Config.getConfig();
	Config.getPrompts();
    }

    private static void configureLogs() {
	String runId = String.valueOf(System.currentTimeMillis());
	System.setProperty("runId", runId);
	PropertyConfigurator.configure(Smallville.class.getClassLoader().getResource("log4j.properties"));
    }

    private static void startServer(int port) {
	SmallvilleServer server = new SmallvilleServer(new ChatGPT(), new World());

	server.start(port);
    }
}
