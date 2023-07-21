package io.github.nickm980.smallville;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * Represents a client for interacting with the Smallville application backend.
 * Must start the smallville server before the client can start.
 */
public class SmallvilleClient {
    /**
     * The host
     */
    private String host;

    /**
     * Function which is called whenever {@link #updateState()} is called to update
     * the simulation
     */
    private AgentHandlerCallback stateHandler;

    private SmallvilleClient() {
    }

    /**
     * Creates a new instance of the SmallvilleClient on the default host
     * http://localhost:8080/. Intended to make getting started easier by provided
     * default functionality. Must still start the server to use Smallville.
     * 
     * @param stateHandler The callback handler for agent state updates.
     * @return The created {@link #SmallvilleClient()} instance.
     * @throws IllegalArgumentException if stateHandler is null or empty.
     */
    public static SmallvilleClient create(AgentHandlerCallback stateHandler) {
	return SmallvilleClient.create("http://localhost:8080/", stateHandler);
    }

    /**
     * Creates a new instance of the SmallvilleClient.
     * 
     * @param host         The host URL of the Smallville application backend.
     * @param stateHandler The callback handler for agent state updates.
     * @return The created {@link #SmallvilleClient()} instance.
     * @throws IllegalArgumentException if the host or stateHandler is null or
     *                                  empty.
     */
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

    /**
     * Adds a new agent to the simulation. Cannot have two agents with the same
     * name.
     * 
     * @param name     The unique name of the agent.
     * @param memories The list of initial memories associated with the agent.
     * @param location The initial location of the agent.
     * @param activity The initial activity of the agent.
     * @return True if the agent creation is successful (the agent was created
     *         because the data is valid and agents of the same name do not exist),
     *         false otherwise.
     */
    public boolean createAgent(String name, List<String> memories, String location, String activity) {
	String url = host + "/agents";
	try {
	    JSONObject requestBody = new JSONObject();
	    requestBody.put("name", name);
	    requestBody.put("memories", new JSONArray(memories));
	    requestBody.put("location", location);
	    requestBody.put("activity", activity);

	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest
		.newBuilder()
		.uri(URI.create(url))
		.header("Content-Type", "application/json")
		.POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
		.build();

	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	    if (response.statusCode() == 200) {
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

    /**
     * Creates a new location
     * 
     * @param name The unique name of the location.
     * @param state The state of the location.
     * @return True if the location creation is successful, false otherwise.
     */
    public boolean createLocation(String name, String state) {
	String url = host + "/locations";
	try {
	    JSONObject requestBody = new JSONObject();
	    requestBody.put("name", name);
	    requestBody.put("state", state);

	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest
		.newBuilder()
		.uri(URI.create(url))
		.header("Content-Type", "application/json")
		.POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
		.build();

	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	    if (response.statusCode() == 200) {
		System.out.println(response.body());
		return true;
	    } else {
		System.out.println("Location creation failed. Status code: " + response.statusCode());
		return false;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
    }

    /**
     * Creates a new location
     * 
     * @param name The unique name of the location.
     * @return True if the location creation is successful, false otherwise.
     */
    public boolean createLocation(String name) {
	String url = host + "/locations";
	try {
	    JSONObject requestBody = new JSONObject();
	    requestBody.put("name", name);

	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest
		.newBuilder()
		.uri(URI.create(url))
		.header("Content-Type", "application/json")
		.POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
		.build();

	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	    if (response.statusCode() == 200) {
		System.out.println(response.body());
		return true;
	    } else {
		System.out.println("Location creation failed. Status code: " + response.statusCode());
		return false;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
    }

    /**
     * Asks a question to the specified agent as the role of an interviewer. No
     * memories will be created as a result of this question because the operation
     * is stateless. To add the result to the memory stream use
     * {@link #addObservation(String, String, boolean)}
     * 
     * @param name     The name of the agent.
     * @param question The question to ask.
     * @return The answer received from the agent.
     */
    public String ask(String name, String question) {
	String url = host + "/agents/" + name + "/ask";
	try {
	    JSONObject requestBody = new JSONObject();
	    requestBody.put("question", question);

	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest
		.newBuilder()
		.uri(URI.create(url))
		.header("Content-Type", "application/json")
		.POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
		.build();

	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	    if (response.statusCode() == 200) {
		JSONObject responseBody = new JSONObject(response.body());
		return responseBody.getString("answer");
	    } else {
		System.out.println("Failed to ask the question. Status code: " + response.statusCode());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return null;
    }

    /**
     * Updates the state of the simulation. When the game updates the user provided
     * state handler function will be called with the updated dataThis occurs in
     * several main steps:
     * <p>
     * 1) Save observation from the game into the agent's memory stream
     * <p>
     * 2) Retrieve relevant memories based on observations
     * <p>
     * 3a) Create or change plans if necessary and save them in memory stream
     * <p>
     * 3b) Reflect on the retrieved memories if the importance of the memories is
     * greater than a predefined value and save the results back into the memory
     * stream. This is to help the agent generalize memories.
     * <p>
     * 4) Act on the retrieved memories and new plans
     */
    public void updateState() {
	String url = host + "/state";
	try {
	    // Send the POST request to update the state
	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest
		.newBuilder()
		.uri(URI.create(url))
		.POST(HttpRequest.BodyPublishers.noBody())
		.build();

	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	    if (response.statusCode() == 200) {
		String responseBody = response.body();
		Gson gson = new Gson();
		SimulationUpdateEvent event = gson.fromJson(responseBody, SimulationUpdateEvent.class);

		stateHandler.handle(event);
	    } else {
		System.out.println("State update failed. Status code: " + response.statusCode());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * Adds an observation to the specified agent in the Smallville application.
     * 
     * @param name        The name of the agent.
     * @param observation The observation to add.
     * @param reactable   Indicates if the observation is reactable. Reactable
     *                    observations will re-prompt agents to change their current
     *                    activity and possibly their plans before
     *                    {@link #updateState()} is called.
     * @return True if the observation addition is successful, false otherwise.
     */
    public boolean addObservation(String name, String observation, boolean reactable) {
	String url = host + "/memories";
	try {
	    JSONObject requestBody = new JSONObject();
	    requestBody.put("name", name);
	    requestBody.put("description", observation);
	    requestBody.put("reactable", reactable);

	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest
		.newBuilder()
		.uri(URI.create(url))
		.header("Content-Type", "application/json")
		.POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
		.build();

	    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	    if (response.statusCode() == 200) {
		System.out.println(response.body());
		return true;
	    } else {
		System.out.println("Observation addition failed. Status code: " + response.statusCode());
		return false;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
    }
}
