package io.github.nickm980.smallville.entities;

import java.util.ArrayList;
import java.util.List;

import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.memory.MemoryStream;

public class Agent {

    private MemoryStream memories;
    private String name;
    private AffectState affect = AffectState.neutral(0);
    private ActionHistory currentAction;
    private Location location;
    private String traits;
    private String model;
    private String socialPreference = "balanced";
    private boolean canProposeWorldChanges;
    private List<String> goals = new ArrayList<String>();
    private List<String> rituals = new ArrayList<String>();
    
    public Agent(String name, List<Characteristic> characteristics, String currentAction, Location location) {
	this.name = name;
	this.memories = new MemoryStream();
	this.memories.addAll(characteristics);
	this.location = location;
	this.currentAction = new ActionHistory(currentAction);
    }

    public String getFullName() {
	return name;
    }

    public String getCurrentActivity() {
	return currentAction.getActivity();
    }

    public String getLastActivity() {
	return currentAction.getLastActivity();
    }

    public void setCurrentActivity(String description) {
	this.currentAction.setActivity(description);
    }

    public Location getLocation() {
	return location;
    }

    public void setLocation(Location location) {
	this.location = location;
    }

    public void setCurrentEmoji(String emoji) {
	this.currentAction.setEmoji(emoji);
    }

    public String getEmoji() {
	return currentAction.getEmoji();
    }

    public MemoryStream getMemoryStream() {
	return memories;
    }

    public void setTraits(String goal) {
	this.traits = goal;
    }

    public String getTraits() {
	return traits;
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

    public boolean canProposeWorldChanges() {
	return canProposeWorldChanges;
    }

    public void setCanProposeWorldChanges(boolean canProposeWorldChanges) {
	this.canProposeWorldChanges = canProposeWorldChanges;
    }

    public List<String> getGoals() {
	return goals;
    }

    public void setGoals(List<String> goals) {
	this.goals = goals == null ? new ArrayList<String>() : new ArrayList<String>(goals);
    }

    public List<String> getRituals() {
	return rituals;
    }

    public void setRituals(List<String> rituals) {
	this.rituals = rituals == null ? new ArrayList<String>() : new ArrayList<String>(rituals);
    }

    public AffectState getAffect() {
	return affect;
    }

    public void setAffect(AffectState affect) {
	this.affect = affect == null ? AffectState.neutral(0) : affect;
    }
}
