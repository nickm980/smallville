package io.github.nickm980.smallville;

import com.beust.jcommander.Parameter;

public class CommandLineArgs {

    @Parameter(names = "--port", description = "Port to run server on", required = false)
    private int port = 8080;

    
    @Parameter(names = "--api-key", description = "Open AI private key for chat completions", required = true)
    private String apiKey;
    
    
    public int getPort() {
	return port;
    }
    
    public String getApiKey() {
	return apiKey;
    }
}
