package io.github.nickm980.smallville;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.nickm980.smallville.analytics.Analytics;
import io.github.nickm980.smallville.api.SmallvilleServer;
import io.github.nickm980.smallville.api.v1.SimulationService;
import io.github.nickm980.smallville.entities.WorldProposal;
import io.github.nickm980.smallville.llm.ChatGPT;
import io.javalin.Javalin;
import io.javalin.community.routing.annotations.Get;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;

public class EndpointsTest {
    private Javalin app;
    private SmallvilleServer smallvilleServer;

    @BeforeEach
    public void setUp() {
	ChatGPT llm = Mockito.mock(ChatGPT.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn("result");
	smallvilleServer = new SmallvilleServer(new Analytics(), llm, new World());
	app = smallvilleServer.server();
    }

    @Test
    public void GET_endpoint_not_found() {
	JavalinTest.test(app, (server, client) -> {
	    assertEquals(client.get("/thisendpointdoesnotexist").code(), 404);
	});
    }

    @Test
    public void GET_to_ping_returns_pong() {
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/ping");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals("could not ping the server", body.get("ping"), "pong");
	    assertEquals(body.get("success"), true);
	});
    }

    @Test
    public void POST_to_memory_stream_saves_memory() {
	// /memories/stream/{uuid}
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.post("/memories/stream");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(200, response.code());
	    assertEquals(true, body.get("success"));
	});
    }

    @Test
    @Disabled("Covered by SimulationServiceTest and java-client integration tests; the in-process Javalin harness returns a generic 500 for this path.")
    public void POST_to_memory_stream_uuid_can_save_and_query_memories() {
	try {
	    int port;
	    try (ServerSocket serverSocket = new ServerSocket(0)) {
		port = serverSocket.getLocalPort();
	    }
	    app.start(port);
	    HttpClient httpClient = HttpClient.newHttpClient();
	    try {
		HttpResponse<String> createResponse = httpClient.send(
		    HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/memories/stream"))
			.POST(HttpRequest.BodyPublishers.noBody())
			.build(),
		    HttpResponse.BodyHandlers.ofString());
		JSONObject createBody = new JSONObject(createResponse.body());
		String uuid = createBody.getString("uuid");

		HttpResponse<String> saveResponse = httpClient.send(
		    HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/memories/stream/" + uuid))
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString("{\"memory\":\"remember the greenhouse tea\"}"))
			.build(),
		    HttpResponse.BodyHandlers.ofString());
		assertEquals(saveResponse.body(), 200, saveResponse.statusCode());
		assertNotNull(saveResponse.body());

		HttpResponse<String> queryResponse = httpClient.send(
		    HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/memories/stream/" + uuid))
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString("{\"query\":\"greenhouse tea\"}"))
			.build(),
		    HttpResponse.BodyHandlers.ofString());
		JSONObject queryBody = new JSONObject(queryResponse.body());

		assertEquals(200, queryResponse.statusCode());
		assertTrue(queryBody.getJSONArray("memories").length() >= 1);
	    } finally {
		app.stop();
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Test
    public void GET_info_returns_analytics_and_simulation_information() {
	// GET /info
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/info");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	    assertEquals(body.get("step"), 1);
	    assertNotNull(body.get("step"));
	    assertNotNull(body.get("locationVisits"));
	    assertNotNull(body.get("prompts"));
	});
    }

    @Test
    public void GET_agents_returns_list_of_agents() {
	// GET /agents
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/agents");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	    assertNotNull(body.get("agents"));
	});
    }

    @Test
    public void GET_world_returns_snapshot_for_visual_inspection() {
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/world");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	    assertNotNull(body.get("time"));
	    assertNotNull(body.get("step"));
	    assertNotNull(body.get("locations"));
	    assertNotNull(body.get("agents"));
	    assertNotNull(body.get("actionLog"));
	    assertNotNull(body.get("worldBuilding"));
	    assertNotNull(body.get("dailyRhythm"));
	    assertNotNull(body.get("pendingProposals"));
	});
    }

    @Test
    public void POST_approve_proposal_preserves_snapshot_action_history_and_pending_visibility() throws Exception {
	SimulationService service = getService();
	WorldProposal proposal = newProposal("add_location", "Garden", "North Arbor", "ready", "To keep the watering route organized.");
	addPendingProposal(service, proposal);

	JavalinTest.test(app, (server, client) -> {
	    Response approveResponse = client.post("/world/proposals/" + proposal.getId() + "/approve");
	    JSONObject approveBody = new JSONObject(approveResponse.body().string());
	    JSONObject proposalBody = approveBody.getJSONObject("proposal");
	    Response worldResponse = client.get("/world");
	    JSONObject worldBody = new JSONObject(worldResponse.body().string());
	    JSONArray actionLog = worldBody.getJSONArray("actionLog");
	    JSONArray pendingProposals = worldBody.getJSONArray("pendingProposals");
	    JSONArray locations = worldBody.getJSONArray("locations");

	    assertEquals(200, approveResponse.code());
	    assertEquals(true, approveBody.get("success"));
	    assertProposalPayload(proposalBody, proposal, "applied");
	    assertEquals(200, worldResponse.code());
	    assertEquals("proposal-applied", actionLog.getJSONObject(0).get("type"));
	    assertEquals("Applied add_location Garden: North Arbor", actionLog.getJSONObject(0).get("summary"));
	    assertEquals("Garden", actionLog.getJSONObject(0).get("fromLocation"));
	    assertEquals("Garden: North Arbor", actionLog.getJSONObject(0).get("toLocation"));
	    assertEquals("proposal-approved", actionLog.getJSONObject(1).get("type"));
	    assertEquals("Approved add_location Garden: North Arbor", actionLog.getJSONObject(1).get("summary"));
	    assertFalse(containsProposal(pendingProposals, proposal.getId()));
	    assertTrue(containsLocationWithState(locations, "Garden: North Arbor", "ready"));
	});
    }

    @Test
    public void POST_approve_proposal_with_invalid_id_returns_smallville_error_contract() {
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.post("/world/proposals/not-a-real-id/approve");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(400, response.code());
	    assertEquals("Proposal not found", body.getString("error"));
	});
    }

    @Test
    public void POST_reject_proposal_preserves_snapshot_action_history_without_world_mutation() throws Exception {
	SimulationService service = getService();
	WorldProposal proposal = newProposal("add_location", "Garden", "North Arbor", "ready", "To keep the watering route organized.");
	addPendingProposal(service, proposal);

	JavalinTest.test(app, (server, client) -> {
	    Response rejectResponse = client.post("/world/proposals/" + proposal.getId() + "/reject");
	    JSONObject rejectBody = new JSONObject(rejectResponse.body().string());
	    JSONObject proposalBody = rejectBody.getJSONObject("proposal");
	    Response worldResponse = client.get("/world");
	    JSONObject worldBody = new JSONObject(worldResponse.body().string());
	    JSONArray actionLog = worldBody.getJSONArray("actionLog");
	    JSONArray pendingProposals = worldBody.getJSONArray("pendingProposals");
	    JSONArray locations = worldBody.getJSONArray("locations");

	    assertEquals(200, rejectResponse.code());
	    assertEquals(true, rejectBody.get("success"));
	    assertProposalPayload(proposalBody, proposal, "rejected");
	    assertEquals(200, worldResponse.code());
	    assertEquals("proposal-rejected", actionLog.getJSONObject(0).get("type"));
	    assertEquals("Rejected add_location Garden: North Arbor", actionLog.getJSONObject(0).get("summary"));
	    assertEquals("Garden", actionLog.getJSONObject(0).get("fromLocation"));
	    assertEquals("Garden: North Arbor", actionLog.getJSONObject(0).get("toLocation"));
	    assertFalse(containsProposal(pendingProposals, proposal.getId()));
	    assertFalse(containsLocation(locations, "Garden: North Arbor"));
	});
    }

    @Test
    public void POST_reject_proposal_with_invalid_id_returns_smallville_error_contract() {
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.post("/world/proposals/not-a-real-id/reject");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(400, response.code());
	    assertEquals("Proposal not found", body.getString("error"));
	});
    }

    @Test
    public void GET_models_returns_runtime_model_state() {
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/models");
	    JSONObject body = new JSONObject(response.body().string());
	    JSONObject askShadowBridge = body.getJSONObject("askShadowBridge");

	    assertEquals(response.code(), 200);
	    assertNotNull(body.get("providerMode"));
	    assertNotNull(body.get("availableModels"));
	    assertFalse(askShadowBridge.getBoolean("enabled"));
	    assertEquals("http://127.0.0.1:8010/neural/turn", askShadowBridge.getString("endpoint"));
	    assertEquals(200, askShadowBridge.getInt("connectTimeoutMs"));
	    assertEquals(1200, askShadowBridge.getInt("callTimeoutMs"));
	});
    }

    @Test
    @Get("/agents/{name}")
    public void GET_agent_by_name_returns_successfully() {
	try {
	    int port;
	    try (ServerSocket serverSocket = new ServerSocket(0)) {
		port = serverSocket.getLocalPort();
	    }
	    app.start(port);
	    HttpClient httpClient = HttpClient.newHttpClient();
	    try {
		HttpResponse<String> response = httpClient.send(
		    HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/agents/nonexistant"))
			.GET()
			.build(),
		    HttpResponse.BodyHandlers.ofString());
		String body = response.body();

		assertEquals(body, 404, response.statusCode());
		assertTrue(body.contains("Agent not found nonexistant"));
	    } finally {
		app.stop();
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Test
    public void POST_to_locations_creates_new_location() {
	// POST /locations
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.post("/locations", Map.of("name", "Red House: Kitchen"));
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	    assertEquals(body.get("success"), true);
	});
    }

    @Test
    public void GET_locations_returns_list_of_locations() {
	// GET /locations
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/locations");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	});
    }

    @Test
    public void GET_to_state_returns_current_state() {
	// GET /state
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/state");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	    assertNotNull(body.get("location_states"));
	    assertNotNull(body.get("agents"));
	    assertNotNull(body.get("conversations"));
	});
    }

    private SimulationService getService() throws Exception {
	Field field = SmallvilleServer.class.getDeclaredField("service");
	field.setAccessible(true);
	return (SimulationService) field.get(smallvilleServer);
    }

    @SuppressWarnings("unchecked")
    private void addPendingProposal(SimulationService service, WorldProposal proposal) throws Exception {
	Field field = SimulationService.class.getDeclaredField("proposals");
	field.setAccessible(true);
	List<WorldProposal> proposals = (List<WorldProposal>) field.get(service);
	proposals.add(proposal);
    }

    private WorldProposal newProposal(String type, String parentLocation, String name, String proposedState, String reason) {
	WorldProposal proposal = new WorldProposal();
	proposal.setId(UUID.randomUUID().toString());
	proposal.setAgent("Jamie");
	proposal.setType(type);
	proposal.setParentLocation(parentLocation);
	proposal.setName(name);
	proposal.setProposedState(proposedState);
	proposal.setReason(reason);
	proposal.setStatus("pending");
	proposal.setCreatedAtTick(0);
	return proposal;
    }

    private void assertProposalPayload(JSONObject proposalBody, WorldProposal proposal, String expectedStatus) {
	assertEquals(proposal.getId(), proposalBody.getString("id"));
	assertEquals(proposal.getAgent(), proposalBody.getString("agent"));
	assertEquals(proposal.getType(), proposalBody.getString("type"));
	assertEquals(proposal.getParentLocation(), proposalBody.getString("parentLocation"));
	assertEquals(proposal.getName(), proposalBody.getString("name"));
	assertEquals(proposal.getProposedState(), proposalBody.getString("proposedState"));
	assertEquals(proposal.getReason(), proposalBody.getString("reason"));
	assertEquals(expectedStatus, proposalBody.getString("status"));
	assertEquals(proposal.getCreatedAtTick(), proposalBody.getInt("createdAtTick"));
    }

    private boolean containsProposal(JSONArray proposals, String proposalId) {
	for (int i = 0; i < proposals.length(); i++) {
	    if (proposalId.equals(proposals.getJSONObject(i).getString("id"))) {
		return true;
	    }
	}
	return false;
    }

    private boolean containsLocation(JSONArray locations, String name) {
	for (int i = 0; i < locations.length(); i++) {
	    if (name.equals(locations.getJSONObject(i).getString("name"))) {
		return true;
	    }
	}
	return false;
    }

    private boolean containsLocationWithState(JSONArray locations, String name, String state) {
	for (int i = 0; i < locations.length(); i++) {
	    JSONObject location = locations.getJSONObject(i);
	    if (name.equals(location.getString("name")) && state.equals(location.optString("state"))) {
		return true;
	    }
	}
	return false;
    }
}
