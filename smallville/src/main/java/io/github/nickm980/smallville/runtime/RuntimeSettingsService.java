package io.github.nickm980.smallville.runtime;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nickm980.smallville.config.GeneralConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RuntimeSettingsService {
    private static final Logger LOG = LoggerFactory.getLogger(RuntimeSettingsService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String DEFAULT_ASK_SHADOW_BRIDGE_ENDPOINT = "http://127.0.0.1:8010/neural/turn";
    private static final int DEFAULT_ASK_SHADOW_BRIDGE_CONNECT_TIMEOUT_MS = 200;
    private static final int DEFAULT_ASK_SHADOW_BRIDGE_CALL_TIMEOUT_MS = 1200;
    private static final int MAX_ASK_SHADOW_TIMEOUT_MS = 30000;

    private final GeneralConfig config;
    private final Map<String, String> agentModelOverrides = new ConcurrentHashMap<String, String>();
    private volatile String defaultModel;
    private volatile String providerMode;

    public RuntimeSettingsService(GeneralConfig config) {
	this.config = config;
	this.defaultModel = config.getModel();
	this.providerMode = determineProviderMode(config.getApiPath());
	enforceIsolationPolicy();
    }

    private void enforceIsolationPolicy() {
	if (config.isLoopbackOnly()) {
	    if (!isLoopbackApiPath(config.getApiPath())) {
		throw new IllegalStateException(
		    "loopbackOnly is true but apiPath is not loopback: " + config.getApiPath());
	    }
	    if (config.isAskShadowBridgeEnabled() && !isLoopbackApiPath(getAskShadowBridgeEndpoint())) {
		throw new IllegalStateException(
		    "loopbackOnly is true but askShadowBridgeEndpoint is not loopback: " + getAskShadowBridgeEndpoint());
	    }
	    LOG.info("Loopback-only mode enforced — all external endpoints verified as loopback.");
	}

	if (config.isOfflineMode()) {
	    LOG.info("Offline mode enabled — cloud support disabled.");
	}
    }

    public String getDefaultModel() {
	return defaultModel;
    }

    public void setDefaultModel(String defaultModel) {
	if (defaultModel != null && !defaultModel.isBlank()) {
	    this.defaultModel = defaultModel.trim();
	}
    }

    public String getProviderMode() {
	return providerMode;
    }

    public void setProviderMode(String providerMode) {
	if (providerMode != null && !providerMode.isBlank()) {
	    this.providerMode = providerMode;
	}
    }

    public Map<String, String> getAgentModelOverrides() {
	return new ConcurrentHashMap<String, String>(agentModelOverrides);
    }

    public void setAgentModelOverride(String agentName, String model) {
	if (agentName == null || agentName.isBlank()) {
	    return;
	}

	if (model == null || model.isBlank()) {
	    agentModelOverrides.remove(agentName);
	    return;
	}

	agentModelOverrides.put(agentName, model.trim());
    }

    public String getAgentModelOverride(String agentName) {
	return agentModelOverrides.get(agentName);
    }

    public String resolveModel(String agentName, String preferredModel) {
	return resolveModel(agentName, preferredModel, null);
    }

    public String resolveModel(String agentName, String preferredModel, String aspect) {
	String override = getAgentModelOverride(agentName);
	if (override != null && !override.isBlank()) {
	    return override;
	}

	if (aspect != null && !aspect.isBlank()) {
	    String aspectModel = config.getModelForAspect(aspect);
	    if (aspectModel != null && !aspectModel.isBlank()) {
		LOG.debug("Aspect-routed model for [{}]: {} -> {}", agentName, aspect, aspectModel);
		return aspectModel;
	    }
	}

	if (preferredModel != null && !preferredModel.isBlank()) {
	    return preferredModel;
	}

	if (defaultModel != null && !defaultModel.isBlank()) {
	    return defaultModel;
	}

	return config.getModel();
    }

    public boolean isLocalProvider() {
	return "local-ollama".equals(providerMode);
    }

    public List<String> discoverModels() {
	LinkedHashSet<String> models = new LinkedHashSet<String>();
	if (config.getModel() != null && !config.getModel().isBlank()) {
	    models.add(config.getModel());
	}
	if (defaultModel != null && !defaultModel.isBlank()) {
	    models.add(defaultModel);
	}
	models.addAll(agentModelOverrides.values());

	String tagsUrl = deriveOllamaTagsUrl(config.getApiPath());
	if (tagsUrl == null) {
	    return new ArrayList<String>(models);
	}

	OkHttpClient client = new OkHttpClient();
	Request request = new Request.Builder().url(tagsUrl).get().build();

	try (Response response = client.newCall(request).execute()) {
	    if (!response.isSuccessful() || response.body() == null) {
		LOG.warn("Could not fetch local Ollama models from {}", tagsUrl);
		return new ArrayList<String>(models);
	    }

	    JsonNode root = MAPPER.readTree(response.body().string());
	    JsonNode entries = root.get("models");
	    if (entries != null && entries.isArray()) {
		for (JsonNode entry : entries) {
		    JsonNode name = entry.get("name");
		    if (name != null && !name.asText().isBlank()) {
			models.add(name.asText());
		    }
		}
	    }
	} catch (IOException e) {
	    LOG.warn("Could not fetch local Ollama models from {}", tagsUrl, e);
	}

	return new ArrayList<String>(models);
    }

    public boolean isAskShadowBridgeEnabled() {
	return config.isAskShadowBridgeEnabled();
    }

    public String getAskShadowBridgeEndpoint() {
	String endpoint = config.getAskShadowBridgeEndpoint();
	if (endpoint == null || endpoint.isBlank()) {
	    return DEFAULT_ASK_SHADOW_BRIDGE_ENDPOINT;
	}

	return endpoint.trim();
    }

    public int getAskShadowBridgeConnectTimeoutMs() {
	return normalizeTimeout(
	    config.getAskShadowBridgeConnectTimeoutMs(),
	    DEFAULT_ASK_SHADOW_BRIDGE_CONNECT_TIMEOUT_MS);
    }

    public Duration getAskShadowBridgeConnectTimeout() {
	return Duration.ofMillis(getAskShadowBridgeConnectTimeoutMs());
    }

    public int getAskShadowBridgeCallTimeoutMs() {
	return normalizeTimeout(
	    config.getAskShadowBridgeCallTimeoutMs(),
	    DEFAULT_ASK_SHADOW_BRIDGE_CALL_TIMEOUT_MS);
    }

    public Duration getAskShadowBridgeCallTimeout() {
	return Duration.ofMillis(getAskShadowBridgeCallTimeoutMs());
    }

    private String determineProviderMode(String apiPath) {
	if (isLoopbackApiPath(apiPath)) {
	    return "local-ollama";
	}

	return "hosted-compatible";
    }

    public boolean isLoopbackApiPath(String apiPath) {
	try {
	    URI uri = new URI(apiPath);
	    String host = uri.getHost();
	    if (host == null || host.isBlank()) {
		return false;
	    }

	    if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "::1".equals(host)) {
		return true;
	    }

	    InetAddress address = InetAddress.getByName(host);
	    return address.isLoopbackAddress() || address.isAnyLocalAddress();
	} catch (URISyntaxException | UnknownHostException e) {
	    return false;
	}
    }

    private String deriveOllamaTagsUrl(String apiPath) {
	if (!isLocalProvider()) {
	    return null;
	}

	try {
	    URI uri = new URI(apiPath);
	    String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
	    String host = uri.getHost();
	    int port = uri.getPort();
	    if (host == null || host.isBlank()) {
		return null;
	    }

	    StringBuilder builder = new StringBuilder();
	    builder.append(scheme).append("://").append(host);
	    if (port != -1) {
		builder.append(":").append(port);
	    }
	    builder.append("/api/tags");
	    return builder.toString();
	} catch (URISyntaxException e) {
	    LOG.warn("Could not derive Ollama tags URL from {}", apiPath, e);
	    return null;
	}
    }

    private int normalizeTimeout(int configuredValue, int defaultValue) {
	if (configuredValue <= 0) {
	    return defaultValue;
	}

	return Math.min(configuredValue, MAX_ASK_SHADOW_TIMEOUT_MS);
    }
}
