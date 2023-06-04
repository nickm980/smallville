package io.github.nickm980.smallville.config;

public class GeneralConfig {

    private String apiPath;
    private String timeFormat;
    private String fullTimeFormat;
    private String yesterdayFormat;
    private String model;
    private int reflectionCutoff;
    private boolean simulationFile;
    private int maxRetries;

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

}
