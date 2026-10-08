package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.config.GeneralConfig;
import io.github.nickm980.smallville.runtime.RuntimeSettingsService;

public class RuntimeSettingsServiceTest {

    @Test
    public void test_resolve_model_prefers_agent_override_then_agent_model_then_default() {
	GeneralConfig config = new GeneralConfig();
	config.setApiPath("http://localhost:11434/v1/chat/completions");
	config.setModel("llama3.2:3b");

	RuntimeSettingsService runtime = new RuntimeSettingsService(config);
	runtime.setDefaultModel("qwen2.5:0.5b");
	runtime.setAgentModelOverride("Alex", "llama3.2:3b-16k");

	assertEquals("llama3.2:3b-16k", runtime.resolveModel("Alex", "mistral:latest"));
	assertEquals("mistral:latest", runtime.resolveModel("Jamie", "mistral:latest"));
	assertEquals("qwen2.5:0.5b", runtime.resolveModel("Jamie", null));
	assertTrue(runtime.isLocalProvider());
    }

    @Test
    public void test_ask_shadow_bridge_runtime_settings_preserve_safe_defaults_and_effective_normalization() {
	GeneralConfig config = new GeneralConfig();
	config.setApiPath("http://localhost:11434/v1/chat/completions");
	config.setModel("llama3.2:3b");

	RuntimeSettingsService runtime = new RuntimeSettingsService(config);

	assertFalse(runtime.isAskShadowBridgeEnabled());
	assertEquals("http://127.0.0.1:8010/neural/turn", runtime.getAskShadowBridgeEndpoint());
	assertEquals(200, runtime.getAskShadowBridgeConnectTimeoutMs());
	assertEquals(1200, runtime.getAskShadowBridgeCallTimeoutMs());

	config.setAskShadowBridgeEnabled(true);
	config.setAskShadowBridgeEndpoint("  http://127.0.0.1:9000/neural/turn  ");
	config.setAskShadowBridgeConnectTimeoutMs(40000);
	config.setAskShadowBridgeCallTimeoutMs(-5);

	assertTrue(runtime.isAskShadowBridgeEnabled());
	assertEquals("http://127.0.0.1:9000/neural/turn", runtime.getAskShadowBridgeEndpoint());
	assertEquals(30000, runtime.getAskShadowBridgeConnectTimeoutMs());
	assertEquals(1200, runtime.getAskShadowBridgeCallTimeoutMs());
    }

    @Test
    public void test_aspect_routing_returns_configured_model_for_known_aspect() {
	GeneralConfig config = new GeneralConfig();
	config.setApiPath("http://localhost:11434/v1/chat/completions");
	config.setModel("llama3.2:3b");
	config.setModelRouting(Map.of(
	    "memoryRanking", "qwen2.5:0.5b",
	    "longTermPlanning", "llama3.2:3b-16k",
	    "activity", "qwen2.5:0.5b-16k"
	));

	RuntimeSettingsService runtime = new RuntimeSettingsService(config);

	assertEquals("qwen2.5:0.5b", runtime.resolveModel("Alex", null, "memoryRanking"));
	assertEquals("llama3.2:3b-16k", runtime.resolveModel("Alex", null, "longTermPlanning"));
	assertEquals("qwen2.5:0.5b-16k", runtime.resolveModel("Jamie", null, "activity"));
    }

    @Test
    public void test_aspect_routing_falls_through_when_aspect_key_absent() {
	GeneralConfig config = new GeneralConfig();
	config.setApiPath("http://localhost:11434/v1/chat/completions");
	config.setModel("llama3.2:3b");
	config.setModelRouting(Map.of("memoryRanking", "qwen2.5:0.5b"));

	RuntimeSettingsService runtime = new RuntimeSettingsService(config);
	runtime.setDefaultModel("fallback-model");

	// "conversation" is not in the routing map, so falls through to defaultModel
	assertEquals("fallback-model", runtime.resolveModel("Alex", null, "conversation"));
    }

    @Test
    public void test_agent_override_takes_precedence_over_aspect_routing() {
	GeneralConfig config = new GeneralConfig();
	config.setApiPath("http://localhost:11434/v1/chat/completions");
	config.setModel("llama3.2:3b");
	config.setModelRouting(Map.of("reflection", "qwen2.5:0.5b"));

	RuntimeSettingsService runtime = new RuntimeSettingsService(config);
	runtime.setAgentModelOverride("Alex", "llama3.2:3b-16k");

	// Agent override wins over aspect routing
	assertEquals("llama3.2:3b-16k", runtime.resolveModel("Alex", null, "reflection"));
	// Without override, aspect routing wins
	assertEquals("qwen2.5:0.5b", runtime.resolveModel("Jamie", null, "reflection"));
    }

    @Test
    public void test_null_and_blank_aspect_delegates_to_existing_chain() {
	GeneralConfig config = new GeneralConfig();
	config.setApiPath("http://localhost:11434/v1/chat/completions");
	config.setModel("llama3.2:3b");
	config.setModelRouting(Map.of("activity", "qwen2.5:0.5b"));

	RuntimeSettingsService runtime = new RuntimeSettingsService(config);

	// null aspect — skips aspect routing, falls to config.model
	assertEquals("llama3.2:3b", runtime.resolveModel("Alex", null, null));
	// blank aspect — same behavior
	assertEquals("llama3.2:3b", runtime.resolveModel("Alex", null, ""));
	assertEquals("llama3.2:3b", runtime.resolveModel("Alex", null, "   "));
    }

    @Test
    public void test_aspect_routing_beats_preferred_model() {
	GeneralConfig config = new GeneralConfig();
	config.setApiPath("http://localhost:11434/v1/chat/completions");
	config.setModel("llama3.2:3b");
	config.setModelRouting(Map.of("shortTermPlanning", "qwen2.5:0.5b-16k"));

	RuntimeSettingsService runtime = new RuntimeSettingsService(config);

	// Aspect routing should beat agent.model (preferredModel)
	assertEquals("qwen2.5:0.5b-16k", runtime.resolveModel("Alex", "mistral:latest", "shortTermPlanning"));
    }
}
