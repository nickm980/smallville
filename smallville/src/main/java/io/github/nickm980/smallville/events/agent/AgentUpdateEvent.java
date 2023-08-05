package io.github.nickm980.smallville.events.agent;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.events.SmallvilleEvent;

public class AgentUpdateEvent extends SmallvilleEvent {
    private Agent agent;
    private Location oldLocation;
    private Location moveTo;
    
    public AgentUpdateEvent(Agent agent, Location oldLocation, Location moveTo) {
	this.agent = agent;
	this.oldLocation = oldLocation;
	this.moveTo = moveTo;
    }

    public Agent getAgent() {
        return agent;
    }

    public Location getOldLocation() {
        return oldLocation;
    }

    public Location getMoveTo() {
        return moveTo;
    }
}
