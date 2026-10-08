package io.github.nickm980.smallville.api.v1.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ModelsResponse {
    private String providerMode;
    private String defaultModel;
    private List<String> availableModels = new ArrayList<String>();
    private Map<String, String> agentModelOverrides = new TreeMap<String, String>();
    private AskShadowBridgeResponse askShadowBridge = new AskShadowBridgeResponse();

    public String getProviderMode() {
	return providerMode;
    }

    public void setProviderMode(String providerMode) {
	this.providerMode = providerMode;
    }

    public String getDefaultModel() {
	return defaultModel;
    }

    public void setDefaultModel(String defaultModel) {
	this.defaultModel = defaultModel;
    }

    public List<String> getAvailableModels() {
	return availableModels;
    }

    public void setAvailableModels(List<String> availableModels) {
	this.availableModels = availableModels;
    }

    public Map<String, String> getAgentModelOverrides() {
	return agentModelOverrides;
    }

    public void setAgentModelOverrides(Map<String, String> agentModelOverrides) {
	this.agentModelOverrides = agentModelOverrides;
    }

    public AskShadowBridgeResponse getAskShadowBridge() {
	return askShadowBridge;
    }

    public void setAskShadowBridge(AskShadowBridgeResponse askShadowBridge) {
	this.askShadowBridge = askShadowBridge == null ? new AskShadowBridgeResponse() : askShadowBridge;
    }

    public static class AskShadowBridgeResponse {
	private boolean enabled;
	private String endpoint;
	private int connectTimeoutMs;
	private int callTimeoutMs;

	public boolean isEnabled() {
	    return enabled;
	}

	public void setEnabled(boolean enabled) {
	    this.enabled = enabled;
	}

	public String getEndpoint() {
	    return endpoint;
	}

	public void setEndpoint(String endpoint) {
	    this.endpoint = endpoint;
	}

	public int getConnectTimeoutMs() {
	    return connectTimeoutMs;
	}

	public void setConnectTimeoutMs(int connectTimeoutMs) {
	    this.connectTimeoutMs = connectTimeoutMs;
	}

	public int getCallTimeoutMs() {
	    return callTimeoutMs;
	}

	public void setCallTimeoutMs(int callTimeoutMs) {
	    this.callTimeoutMs = callTimeoutMs;
	}
    }
}
