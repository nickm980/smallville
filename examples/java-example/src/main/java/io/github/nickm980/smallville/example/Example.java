package io.github.nickm980.smallville.example;

import java.util.ArrayList;
import java.util.List;

import io.github.nickm980.smallville.AgentHandlerCallback;
import io.github.nickm980.smallville.ObjectState;
import io.github.nickm980.smallville.SimulationUpdateEvent;
import io.github.nickm980.smallville.SmallvilleAgent;
import io.github.nickm980.smallville.SmallvilleClient;

public class Example {

    public static void main(String[] args) {
	SmallvilleClient client = SmallvilleClient.create("http://localhost:8080", new AgentHandlerCallback() {
	    public void handle(SimulationUpdateEvent event) {
		List<SmallvilleAgent> agents = event.getAgents();
		
		for (SmallvilleAgent agent : agents) {
		    System.out.println(agent.getName() + " : " + agent.getAction());
		}
	    }
	});
	
	client.createLocation("Ice Cream shop");
	client.createObject("Ice Cream Shop", "Seat", new ObjectState("test"));

	List<String> memories = new ArrayList<String>();
	memories.add("Father of Toni");
	
	List<String> toniMemories = new ArrayList<String>();
	memories.add("son of John");
	
	client.createAgent("John", memories, "Red House: Kitchen", "Cooking");
	client.createAgent("Toni", toniMemories, "Red House: Bedroom", "Playing video games");

	client.addObservation("John", "The Red House: Bedroom is on fire", true);
    }
}
