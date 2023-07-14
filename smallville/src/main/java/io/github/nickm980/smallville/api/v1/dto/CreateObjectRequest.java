package io.github.nickm980.smallville.api.v1.dto;

public class CreateObjectRequest {

    private String name;
    private String parent;
    private String state;

    public String getParent() {
	return parent;
    }

    public void setParent(String parent) {
	this.parent = parent;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
