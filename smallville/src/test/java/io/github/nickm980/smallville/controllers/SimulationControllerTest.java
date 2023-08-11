package io.github.nickm980.smallville.controllers;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.analytics.Analytics;
import io.github.nickm980.smallville.api.SmallvilleServer;
import io.github.nickm980.smallville.api.v1.dto.*;
import io.github.nickm980.smallville.entities.SimulationTime;
import io.github.nickm980.smallville.mocks.MockLLM;
import io.javalin.Javalin;
import io.javalin.community.routing.annotations.Get;
import io.javalin.community.routing.annotations.Param;
import io.javalin.http.Context;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.time.Duration;
import java.util.Map;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class SimulationControllerTest {
    Javalin app = new SmallvilleServer(new Analytics(), new MockLLM(), new World()).server();

    @Test
    public void GET_endpoint_not_found() {
	JavalinTest.test(app, (server, client) -> {
	    assertEquals(client.get("/thisendpointdoesnotexist").code(), 404);
	});
    }

    @Test
    public void GET_to_ping() {
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/ping");

	    assertEquals(response.code(), 200);
	    assertEquals(new JSONObject(response.body().string()).get("ping"), "pong");
	});
    }

    @Test
    public void POST_to_memories_creates_stream() {
	// POST /memories/stream
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.post("/memories/stream");
	    System.out.println(response.body().string());
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	    assertEquals("success", true);
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
    @Get("/agents/{name}")
    public void GET_agent_by_name_returns_successfully() {
	// GET /agents/{name}
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/agents/name_of_an_agent_which_doesnt_exist");
	    assertEquals(response.body().string(), "test");
	    JSONObject body = new JSONObject(response.body().string());

	    assertEquals(response.code(), 200);
	});
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
}