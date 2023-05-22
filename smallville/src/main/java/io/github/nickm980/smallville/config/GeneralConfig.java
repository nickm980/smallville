package io.github.nickm980.smallville.config;

public class GeneralConfig {

    private String apiPath;
    private String timeFormat;

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
}
