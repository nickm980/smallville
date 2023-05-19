package io.github.nickm980.smallville;

import java.io.IOException;
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
