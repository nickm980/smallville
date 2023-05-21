package io.github.nickm980.smallville.config;

import java.util.List;

import okhttp3.HttpUrl;

public class GeneralConfig {

    private List<String> updateOrder;
    private String apiPath;

    public List<String> getUpdateOrder() {
	return updateOrder;
    }

    public void setUpdateOrder(List<String> updateOrder) {
	this.updateOrder = updateOrder;
    }

    public String getApiPath() {
	return apiPath;
    }

    public void setApiPath(String path) {
	this.apiPath = path;
    }
}
