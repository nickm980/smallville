package io.github.nickm980.smallville.config;

import com.beust.jcommander.Parameter;

public class CommandLineArgs {

    @Parameter(names = "--port", description = "Port to run server on", required = false)
    private int port = 8080;

    @Parameter(names = "--api-key", description = "Open AI private key for chat completions", required = true)
    private String apiKey;

    @Parameter(names = "--python-server-port", description = "Python server port", required = false)
    private String pythonServerPort;

    public String getPythonServerPort() {
	return pythonServerPort;
    }

    public int getPort() {
	return port;
    }

    public String getApiKey() {
	return apiKey;
    }
}
