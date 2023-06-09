package io.github.nickm980.smallville.prompts.dto;

public class ObjectChangeResponse {

    private String state;
    private String object;

    public ObjectChangeResponse(String object, String state) {
	this.state = state;
	this.object = object;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getObject() {
	return object;
    }

    public void setObject(String object) {
	this.object = object;
    }
}
