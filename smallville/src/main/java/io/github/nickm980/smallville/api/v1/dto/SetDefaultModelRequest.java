package io.github.nickm980.smallville.api.v1.dto;

public class SetDefaultModelRequest {
    private String model;
    private String providerMode;

    public String getModel() {
	return model;
    }

    public void setModel(String model) {
	this.model = model;
    }

    public String getProviderMode() {
	return providerMode;
    }

    public void setProviderMode(String providerMode) {
	this.providerMode = providerMode;
    }
}
