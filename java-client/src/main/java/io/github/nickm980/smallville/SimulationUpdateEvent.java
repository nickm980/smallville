package io.github.nickm980.smallville;

import java.util.List;

public class SimulationUpdateEvent {

    private List<SmallvilleAgent> agents;
    private List<SmallvilleLocation> locations;

    public SimulationUpdateEvent() {
    }

    public List<SmallvilleAgent> getAgents() {
	return agents;
    }

    public void setAgents(List<SmallvilleAgent> agents) {
	this.agents = agents;
    }

    public List<SmallvilleLocation> getLocations() {
	return locations;
    }

    public void setLocations(List<SmallvilleLocation> locations) {
	this.locations = locations;
    }
}
