package io.github.nickm980.smallville;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.nickm980.smallville.analytics.Analytics;
import io.github.nickm980.smallville.api.SmallvilleServer;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.llm.ChatGPT;
import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.memory.Observation;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;

/**
 * Integration tests for the GET /world/ledger/export endpoint.
 */
public class LedgerExportIntegrationTest {

    private Javalin app;
    private World world;

    @BeforeEach
    public void setUp() {
	ChatGPT llm = Mockito.mock(ChatGPT.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn("result");
	world = new World();
	app = new SmallvilleServer(new Analytics(), llm, world).server();
    }

    @Test
    public void GET_ledger_export_returns_200_on_empty_world() {
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/world/ledger/export");
	    assertEquals(200, response.code());

	    JSONObject body = new JSONObject(response.body().string());
	    assertNotNull(body.getString("generatedAtUtc"));
	    assertNotNull(body.get("world"));
	    assertNotNull(body.get("governanceLedger"));
	    assertNotNull(body.get("memoryIndex"));
	    assertNotNull(body.get("offlinePolicy"));
	    assertNotNull(body.get("governancePolicy"));
	});
    }

    @Test
    public void GET_ledger_export_contains_memory_index_per_agent() {
	Agent agentA = new Agent("Alice", List.of(new Characteristic("friendly")), "resting", new Location("Blue House: Kitchen"));
	Agent agentB = new Agent("Bob", List.of(new Characteristic("curious")), "working", new Location("Green House: Study"));
	world.create(agentA);
	world.create(agentB);

	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/world/ledger/export");
	    assertEquals(200, response.code());

	    JSONObject body = new JSONObject(response.body().string());
	    JSONObject memoryIndex = body.getJSONObject("memoryIndex");
	    assertTrue(memoryIndex.has("Alice"));
	    assertTrue(memoryIndex.has("Bob"));
	});
    }

    @Test
    public void GET_ledger_export_includes_governance_policy_keys() {
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/world/ledger/export");
	    JSONObject body = new JSONObject(response.body().string());
	    JSONObject policy = body.getJSONObject("governancePolicy");

	    assertNotNull(policy.get("proposalTypes"));
	    assertNotNull(policy.get("pendingStatuses"));
	});
    }

    @Test
    public void GET_ledger_export_includes_offline_policy_keys() {
	JavalinTest.test(app, (server, client) -> {
	    Response response = client.get("/world/ledger/export");
	    JSONObject body = new JSONObject(response.body().string());
	    JSONObject offlinePolicy = body.getJSONObject("offlinePolicy");

	    assertNotNull(offlinePolicy.get("offlineMode"));
	    assertNotNull(offlinePolicy.get("loopbackOnly"));
	});
    }
}
