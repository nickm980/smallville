package io.github.nickm980.smallville.entities;

import java.util.List;

import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.memory.MemoryStream;

public class Agent {

    private MemoryStream memories;
    private String name;
    private ActionHistory currentAction;
    private Location location;
    private String traits;
    
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
    
}