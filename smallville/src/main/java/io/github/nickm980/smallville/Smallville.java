package io.github.nickm980.smallville;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import io.github.nickm980.smallville.api.server.SmallvilleServer;
import io.github.nickm980.smallville.config.CommandLineArgs;
import io.github.nickm980.smallville.llm.ChatGPT;

public class Smallville {

    private static final Logger LOG = LoggerFactory.getLogger(Smallville.class);

    public static void main(String[] args) throws IOException {
	String runId = String.valueOf(System.currentTimeMillis());
	System.setProperty("runId", runId);
	PropertyConfigurator.configure(Smallville.class.getClassLoader().getResource("log4j.properties"));

	CommandLineArgs jArgs = new CommandLineArgs();
	JCommander commands = JCommander.newBuilder().addObject(jArgs).build();
	commands.parse(args);

	int port = jArgs.getPort();
	String key = jArgs.getApiKey();

	Settings.setApiKey(key);

	SmallvilleServer server = new SmallvilleServer(new ChatGPT(), new World());

	server.start(port);

	LOG.info("Smallville server started on port " + port);
    }
}
