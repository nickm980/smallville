package io.github.nickm980.smallville.config;

public class GeneralConfig {

    private String apiPath;
    private String timeFormat;
    private String fullTimeFormat;
    private String yesterdayFormat;
    
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
}
