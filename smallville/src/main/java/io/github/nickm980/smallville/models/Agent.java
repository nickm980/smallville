package io.github.nickm980.smallville.models;

import java.util.List;
import io.github.nickm980.smallville.models.memory.Characteristic;
import io.github.nickm980.smallville.models.memory.MemoryStream;
import io.github.nickm980.smallville.models.memory.Plan;

public class Agent {

    private MemoryStream memories;
    private String name;
    private ActionHistory currentAction;
    private AgentLocation location;

    public Agent(String name, List<Characteristic> characteristics, String currentAction, AgentLocation location) {
	this.name = name;
	this.memories = new MemoryStream();
	this.memories.add(characteristics);
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

    public List<Characteristic> getCharacteristics() {
	return memories.getCharacteristics();
    }

    public void setPlans(List<Plan> plans) {
	memories.setPlans(plans);
    }

    public List<Plan> getPlans() {
	return memories.getPlans();
    }

    public void setShortTermPlans(List<Plan> plans) {
	this.getMemoryStream().setShortTermPlans(plans);
    }
}