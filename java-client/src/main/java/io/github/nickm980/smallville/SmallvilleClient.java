package io.github.nickm980.smallville;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SmallvilleClient {
    private String host;
    private AgentHandlerCallback stateHandler;

    private SmallvilleClient() {
    }

    public static SmallvilleClient create(String host, AgentHandlerCallback stateHandler) {
	SmallvilleClient client = new SmallvilleClient();

	if (host == null || host.isEmpty()) {
	    throw new IllegalArgumentException(
		    "Missing host option in initialization. If running locally with default options try http://localhost:8080/");
	}

	if (stateHandler == null) {
	    throw new IllegalArgumentException("Missing agentsHandler");
	}

	client.host = host;
	client.stateHandler = stateHandler;

	return client;
    }

    public void sync() {
	// Implementation
    }

    public boolean init() {
	// Implementation
	return false;
    }

    public boolean createAgent(String name, List<String> memories, String location, String activity) {
	String url = host + "/agents";
	try {
	    // Create the request body
	    JSONObject requestBody = new JSONObject();
	    requestBody.put("name", name);
	    requestBody.put("memories", new JSONArray(memories));
	    requestBody.put("location", location);
	    requestBody.put("activity", activity);

	    // Send the POST request
	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest
		.newBuilder()
		.uri(URI.create(url))
		.header("Content-Type", "application/json")
		.POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
		.build();

	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	    if (response.statusCode() == 200) {
		// Agent creation successful
		System.out.println(response.body());
		return true;
	    } else {
		System.out.println("Agent creation failed. Status code: " + response.statusCode());
		return false;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
    }

    public boolean createObject(String parent, String name, String state) {
	// Implementation
	return false;
    }

    public boolean createLocation(String name) {
	// Implementation
	return false;
    }

    public void updateState() {	
	stateHandler.handle(null, null);
    }

    public boolean addObservation(String name, String observation, boolean reactable) {
	// Implementation
	return false;
    }

    public void whisper(String name, String goal) {
    }

    public String ask(String name, String question) {
	return null;
    }
}
