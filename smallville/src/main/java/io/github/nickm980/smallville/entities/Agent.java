package io.github.nickm980.smallville.entities;

import java.util.List;
import io.github.nickm980.smallville.entities.memory.Characteristic;
import io.github.nickm980.smallville.entities.memory.MemoryStream;

public class Agent {

    private MemoryStream memories;
    private String name;
    private ActionHistory currentAction;
    private AgentLocation location;
    private String goal = "";

    public Agent(String name, List<Characteristic> characteristics, String currentAction, AgentLocation location) {
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

    public SimulatedObject getObject() {
	return location.getObject();
    }

    public SimulatedLocation getLocation() {
	return location.getLocation();
    }

    public void setLocation(AgentLocation location) {
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

    public void setGoal(String goal) {
	this.goal = goal;
    }

    public String getGoal() {
	return goal;
    }
}