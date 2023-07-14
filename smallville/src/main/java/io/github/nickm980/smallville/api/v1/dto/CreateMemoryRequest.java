package io.github.nickm980.smallville.api.v1.dto;

public class CreateMemoryRequest {

    private String name;
    private String description;
    private boolean reactable;

    public boolean isReactable() {
	return reactable;
    }

    public void setReactable(boolean reactable) {
	this.reactable = reactable;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

}
