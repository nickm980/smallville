package io.github.nickm980.smallville.api.v1.dto;

import java.util.List;

public class CreateAgentRequest {
    private String name;
    private List<String> memories;
    private String activity;
    private String location;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<String> getMemories() {
	return memories;
    }

    public String getActivity() {
	return activity;
    }

    public void setActivity(String activity) {
	this.activity = activity;
    }

    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    public void setMemories(List<String> memories) {
	this.memories = memories;
    }
}
