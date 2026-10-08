package io.github.nickm980.smallville.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class GeneralConfig {

    private String apiPath;
    private String timeFormat;
    private String fullTimeFormat;
    private String yesterdayFormat;
    private String model;
    private int reflectionCutoff;
    private boolean simulationFile;
    private int maxRetries;
    private boolean askShadowBridgeEnabled;
    private String askShadowBridgeEndpoint;
    private int askShadowBridgeConnectTimeoutMs;
    private int askShadowBridgeCallTimeoutMs;
    private boolean offlineMode;
    private boolean loopbackOnly;
    private Map<String, String> modelRouting = new LinkedHashMap<>();

    public boolean isSimulationFile() {
	return simulationFile;
    }

    public void setSimulationFile(boolean useSimulationFile) {
	this.simulationFile = useSimulationFile;
    }

    public int getReflectionCutoff() {
	return reflectionCutoff;
    }

    public void setReflectionCutoff(int reflectionCutoff) {
	this.reflectionCutoff = reflectionCutoff;
    }

    public String getModel() {
	return model;
    }

    public void setModel(String model) {
	this.model = model;
    }

    public String getYesterdayFormat() {
	return yesterdayFormat;
    }

    public void setYesterdayFormat(String yesterdayFormat) {
	this.yesterdayFormat = yesterdayFormat;
    }

    public String getFullTimeFormat() {
	return fullTimeFormat;
    }

    public void setFullTimeFormat(String fullTimeFormat) {
	this.fullTimeFormat = fullTimeFormat;
    }

    public String getTimeFormat() {
	return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
	this.timeFormat = timeFormat;
    }

    public String getApiPath() {
	return apiPath;
    }

    public void setApiPath(String path) {
	this.apiPath = path;
    }

    public int getMaxRetries() {
	return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
	this.maxRetries = maxRetries;
    }

    public boolean isAskShadowBridgeEnabled() {
	return askShadowBridgeEnabled;
    }

    public void setAskShadowBridgeEnabled(boolean askShadowBridgeEnabled) {
	this.askShadowBridgeEnabled = askShadowBridgeEnabled;
    }

    public String getAskShadowBridgeEndpoint() {
	return askShadowBridgeEndpoint;
    }

    public void setAskShadowBridgeEndpoint(String askShadowBridgeEndpoint) {
	this.askShadowBridgeEndpoint = askShadowBridgeEndpoint;
    }

    public int getAskShadowBridgeConnectTimeoutMs() {
	return askShadowBridgeConnectTimeoutMs;
    }

    public void setAskShadowBridgeConnectTimeoutMs(int askShadowBridgeConnectTimeoutMs) {
	this.askShadowBridgeConnectTimeoutMs = askShadowBridgeConnectTimeoutMs;
    }

    public int getAskShadowBridgeCallTimeoutMs() {
	return askShadowBridgeCallTimeoutMs;
    }

    public void setAskShadowBridgeCallTimeoutMs(int askShadowBridgeCallTimeoutMs) {
	this.askShadowBridgeCallTimeoutMs = askShadowBridgeCallTimeoutMs;
    }

    public boolean isOfflineMode() {
	return offlineMode;
    }

    public void setOfflineMode(boolean offlineMode) {
	this.offlineMode = offlineMode;
    }

    public boolean isLoopbackOnly() {
	return loopbackOnly;
    }

    public void setLoopbackOnly(boolean loopbackOnly) {
	this.loopbackOnly = loopbackOnly;
    }

    public Map<String, String> getModelRouting() {
	return modelRouting;
    }

    public void setModelRouting(Map<String, String> modelRouting) {
	this.modelRouting = modelRouting != null ? modelRouting : new LinkedHashMap<>();
    }

    public String getModelForAspect(String aspect) {
	if (aspect == null || modelRouting == null) {
	    return null;
	}
	return modelRouting.get(aspect);
    }

}
