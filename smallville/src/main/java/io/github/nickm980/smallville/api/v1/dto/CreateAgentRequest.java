package io.github.nickm980.smallville.api.v1.dto;

import java.util.List;

public class CreateAgentRequest {
    private String name;
    private List<String> memories;
    private List<String> workingMemories;
    private List<String> goals;
    private List<String> rituals;
    private String activity;
    private String location;
    private String traits;
    private String model;
    private String socialPreference;
    private boolean canProposeWorldChanges;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<String> getMemories() {
	return memories;
    }

    public List<String> getWorkingMemories() {
	return workingMemories;
    }

    public List<String> getGoals() {
	return goals;
    }

    public List<String> getRituals() {
	return rituals;
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

    public void setWorkingMemories(List<String> workingMemories) {
	this.workingMemories = workingMemories;
    }

    public void setGoals(List<String> goals) {
	this.goals = goals;
    }

    public void setRituals(List<String> rituals) {
	this.rituals = rituals;
    }

    public String getTraits() {
	return traits;
    }

    public void setTraits(String traits) {
	this.traits = traits;
    }

    public String getModel() {
	return model;
    }

    public void setModel(String model) {
	this.model = model;
    }

    public String getSocialPreference() {
	return socialPreference;
    }

    public void setSocialPreference(String socialPreference) {
	this.socialPreference = socialPreference;
    }

    public boolean isCanProposeWorldChanges() {
	return canProposeWorldChanges;
    }

    public void setCanProposeWorldChanges(boolean canProposeWorldChanges) {
	this.canProposeWorldChanges = canProposeWorldChanges;
    }
}
