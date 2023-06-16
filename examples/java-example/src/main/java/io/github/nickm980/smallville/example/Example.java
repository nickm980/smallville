package io.github.nickm980.smallville.example;

import java.util.ArrayList;
import java.util.List;

import io.github.nickm980.smallville.AgentHandlerCallback;
import io.github.nickm980.smallville.ObjectState;
import io.github.nickm980.smallville.SimulationUpdateEvent;
import io.github.nickm980.smallville.SmallvilleAgent;
import io.github.nickm980.smallville.SmallvilleClient;
import io.github.nickm980.smallville.SmallvilleLocation;

public class Example {

    public static void main(String[] args) {
	SmallvilleClient client = SmallvilleClient.create("http://localhost:8080", new AgentHandlerCallback() {
	    public void handle(SimulationUpdateEvent event) {
		List<SmallvilleAgent> agents = event.getAgents();
		List<SmallvilleLocation> locations = event.getLocations();
	    }
	});
	
	client.createLocation("Red House");
	client.createObject("Red House", "Kitchen", new ObjectState("occupied"));

	List<String> memories = new ArrayList<String>();
	memories.add("Memory1");
	client.createAgent("John", memories, "Red House: Kitchen", "Cooking");

	client.updateState();
    }
}
