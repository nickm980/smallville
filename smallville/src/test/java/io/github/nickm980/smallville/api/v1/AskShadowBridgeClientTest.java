package io.github.nickm980.smallville.api.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import io.github.nickm980.smallville.api.v1.dto.WorldSnapshotResponse;
import io.github.nickm980.smallville.config.GeneralConfig;
import io.github.nickm980.smallville.runtime.RuntimeSettingsService;

public class AskShadowBridgeClientTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private HttpServer server;

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void test_shadow_ask_accepts_valid_local_root_response_and_emits_answer_only_request() throws Exception {
        AtomicReference<String> capturedBody = new AtomicReference<String>();
        startServer(exchange -> {
            String body = readBody(exchange);
            capturedBody.set(body);

            JsonNode request = MAPPER.readTree(body);
            String requestId = request.get("request_id").asText();

            writeJson(exchange, 200, """
                {
                  "request_id": "%s",
                  "status": "ok",
                  "decision": {
                    "decision_type": "answer",
                    "summary": "Stub answer prepared for jamie.",
                    "content": "[shadow-only local_root stub] Jamie would keep the morning calm.",
                    "confidence": 0.42,
                    "grounding_facts": ["tick=0", "location=Blue House: Kitchen"],
                    "policy_flags": ["shadow_only", "ask_only", "host_validation_required"],
                    "mutation_intent": "none",
                    "host_validation_required": true,
                    "requires_human_review": true
                  },
                  "route_taken": {
                    "selected_adapter": "local_root",
                    "selected_model": "gpt-oss-20b",
                    "default_route": true,
                    "escalation_occurred": false,
                    "enabled": true,
                    "policy_reason": "Default local-root-first policy for shadow ask path."
                  },
                  "latency_ms": 4,
                  "warnings": ["Local root adapter is a deterministic scaffold stub; no model call was made."],
                  "fallback_used": false,
                  "audit_ref": "audit:%s"
                }
                """.formatted(requestId, requestId));
        });

        AskShadowBridgeClient client = AskShadowBridgeClient.fromRuntimeSettings(buildRuntimeSettings(true, serverUrl(), 200, 500));
        AskShadowBridgeClient.ShadowAskResult result = client.shadowAsk(buildSnapshot(), "Jamie", "How is the morning going?");

        assertTrue(result.isAccepted());
        assertEquals("accepted", result.getReason());
        assertEquals("local_root", result.getSelectedAdapter());
        assertEquals("gpt-oss-20b", result.getSelectedModel());
        assertEquals(serverUrl(), client.getEndpoint());
        assertEquals(200, client.getConnectTimeoutMs());
        assertEquals(500, client.getCallTimeoutMs());
        assertTrue(result.getBridgeContent().contains("shadow-only local_root stub"));
        assertTrue(result.getAuditRef().startsWith("audit:ask-"));

        JsonNode sent = MAPPER.readTree(capturedBody.get());
        assertEquals("shadow", sent.get("mode").asText());
        assertEquals("ask", sent.get("turn_kind").asText());
        assertEquals(1, sent.get("allowed_decision_types").size());
        assertEquals("answer", sent.get("allowed_decision_types").get(0).asText());
        assertEquals("none", sent.get("requested_mutation_intent").asText());
        assertFalse(sent.get("route_budget").get("allow_remote_escalation").asBoolean());
        assertEquals("java-host", sent.get("trace").get("source").asText());
        assertEquals("Jamie is organizing the tea tray at Blue House: Kitchen. Recent context: Keep the kettle warm", sent.get("observation").asText());
    }

    @Test
    public void test_shadow_ask_times_out_cleanly() throws Exception {
        startServer(exchange -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            writeJson(exchange, 200, "{\"status\":\"ok\"}");
        });

        AskShadowBridgeClient client = AskShadowBridgeClient.fromRuntimeSettings(buildRuntimeSettings(true, serverUrl(), 50, 100));
        AskShadowBridgeClient.ShadowAskResult result = client.shadowAsk(buildSnapshot(), "Jamie", "How is the morning going?");

        assertFalse(result.isAccepted());
        assertEquals("timeout", result.getReason());
    }

    @Test
    public void test_shadow_ask_skips_cleanly_when_disabled() throws Exception {
        AtomicReference<String> capturedBody = new AtomicReference<String>();
        startServer(exchange -> {
            capturedBody.set(readBody(exchange));
            writeJson(exchange, 200, "{\"status\":\"ok\"}");
        });

        AskShadowBridgeClient client = AskShadowBridgeClient.fromRuntimeSettings(buildRuntimeSettings(false, serverUrl(), 200, 500));
        AskShadowBridgeClient.ShadowAskResult result = client.shadowAsk(buildSnapshot(), "Jamie", "How is the morning going?");

        assertFalse(result.isAccepted());
        assertEquals("disabled", result.getReason());
        assertFalse(client.isEnabled());
        assertEquals(null, capturedBody.get());
    }

    @Test
    public void test_shadow_ask_rejects_malformed_envelope() throws Exception {
        startServer(exchange -> {
            String body = readBody(exchange);
            String requestId = MAPPER.readTree(body).get("request_id").asText();
            writeJson(exchange, 200, """
                {
                  "request_id": "%s",
                  "status": "ok"
                }
                """.formatted(requestId));
        });

        AskShadowBridgeClient client = AskShadowBridgeClient.fromRuntimeSettings(buildRuntimeSettings(true, serverUrl(), 200, 500));
        AskShadowBridgeClient.ShadowAskResult result = client.shadowAsk(buildSnapshot(), "Jamie", "How is the morning going?");

        assertFalse(result.isAccepted());
        assertEquals("invalid_payload", result.getReason());
    }

    @Test
    public void test_shadow_ask_rejects_non_shadow_compatible_route() throws Exception {
        startServer(exchange -> {
            String body = readBody(exchange);
            String requestId = MAPPER.readTree(body).get("request_id").asText();
            writeJson(exchange, 200, """
                {
                  "request_id": "%s",
                  "status": "ok",
                  "decision": {
                    "decision_type": "answer",
                    "summary": "Stub answer prepared for jamie.",
                    "content": "Bridge content",
                    "confidence": 0.42,
                    "grounding_facts": [],
                    "policy_flags": ["shadow_only", "ask_only", "host_validation_required"],
                    "mutation_intent": "none",
                    "host_validation_required": true,
                    "requires_human_review": true
                  },
                  "route_taken": {
                    "selected_adapter": "remote_nano",
                    "selected_model": "GPT-5.4 nano",
                    "default_route": false,
                    "escalation_occurred": true,
                    "enabled": true,
                    "policy_reason": "Unexpected remote route."
                  },
                  "latency_ms": 4,
                  "warnings": [],
                  "fallback_used": false,
                  "audit_ref": "audit:%s"
                }
                """.formatted(requestId, requestId));
        });

        AskShadowBridgeClient client = AskShadowBridgeClient.fromRuntimeSettings(buildRuntimeSettings(true, serverUrl(), 200, 500));
        AskShadowBridgeClient.ShadowAskResult result = client.shadowAsk(buildSnapshot(), "Jamie", "How is the morning going?");

        assertFalse(result.isAccepted());
        assertEquals("adapter_blocked", result.getReason());
    }

    private void startServer(ExchangeHandler handler) throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/neural/turn", exchange -> {
            try {
                handler.handle(exchange);
            } finally {
                exchange.close();
            }
        });
        server.start();
    }

    private String serverUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/neural/turn";
    }

    private String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void writeJson(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    private WorldSnapshotResponse buildSnapshot() {
        WorldSnapshotResponse snapshot = new WorldSnapshotResponse();
        snapshot.setStep(1);
        snapshot.setTick(0);
        snapshot.setTime("08:00 AM");

        WorldSnapshotResponse.WorldBuildRulesResponse worldBuilding = new WorldSnapshotResponse.WorldBuildRulesResponse();
        worldBuilding.setSummary("A calm two-house morning routine.");
        worldBuilding.setRules(List.of("Java host remains authoritative."));
        worldBuilding.setAllowedLocationStates(List.of("ready", "settled"));
        snapshot.setWorldBuilding(worldBuilding);

        WorldSnapshotResponse.WorldLocationResponse location = new WorldSnapshotResponse.WorldLocationResponse();
        location.setName("Blue House: Kitchen");
        location.setState("ready");
        location.setAgents(List.of("Jamie"));
        snapshot.setLocations(List.of(location));

        WorldSnapshotResponse.WorldAgentResponse agent = new WorldSnapshotResponse.WorldAgentResponse();
        agent.setName("Jamie");
        agent.setLocation("Blue House: Kitchen");
        agent.setAction("organizing the tea tray");
        agent.setTraits("steady, helpful, quiet");
        agent.setModel("gpt-oss-20b");
        agent.setSocialPreference("balanced");
        agent.setCanProposeWorldChanges(false);
        agent.setPersona(List.of("Speaks calmly."));
        agent.setWorkingMemories(List.of("Keep the kettle warm"));
        agent.setRecentMemories(List.of("Set out the cups"));
        agent.setShortPlans(List.of("Bring tea to Alex"));
        agent.setLongPlans(List.of("Keep the household calm"));
        agent.setGoals(List.of("Keep the kitchen orderly"));
        agent.setRituals(List.of("Morning tea setup"));
        snapshot.setAgents(List.of(agent));

        WorldSnapshotResponse.ActionLogResponse action = new WorldSnapshotResponse.ActionLogResponse();
        action.setTick(0);
        action.setSummary("Jamie organized the tea tray.");
        snapshot.setActionLog(List.of(action));

        snapshot.setPendingProposals(List.of());
        return snapshot;
    }

    private RuntimeSettingsService buildRuntimeSettings(boolean enabled, String endpoint, int connectTimeoutMs, int callTimeoutMs) {
        GeneralConfig config = new GeneralConfig();
        config.setApiPath("http://localhost:11434/v1/chat/completions");
        config.setModel("llama3.2:3b-16k");
        config.setAskShadowBridgeEnabled(enabled);
        config.setAskShadowBridgeEndpoint(endpoint);
        config.setAskShadowBridgeConnectTimeoutMs(connectTimeoutMs);
        config.setAskShadowBridgeCallTimeoutMs(callTimeoutMs);
        return new RuntimeSettingsService(config);
    }

    @FunctionalInterface
    private interface ExchangeHandler {
        void handle(HttpExchange exchange) throws IOException;
    }
}
