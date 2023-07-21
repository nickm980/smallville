package io.github.nickm980.smallville;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class SmallvilleClientTest {
    private static SmallvilleClient client;

    @BeforeAll
    public static void setUp() {
	client = SmallvilleClient.create("http://localhost:8080", new AgentHandlerCallback() {
	    @Override
	    public void handle(SimulationUpdateEvent event) {
		System.out.println("Finished updating");
		for (SmallvilleAgent agent : event.getAgents()) {
		    System.out.println(agent.getName() + " : " + agent.getAction());
		}
	    }
	});
    }

    @Test
    @Order(1)
    public void testLocationCreation() {
	assertTrue(client.createLocation("Yellow House: Bedroom"));
    }

    @Test
    @Order(2)
    public void testAgentCreation() {
	List<String> memories = List.of("Joe loves to play video games", "Joe lives in the yellow house");
	boolean success = client.createAgent("Joe", memories, "Yellow House: Bedroom", "sleeping");

	assertEquals(true, success);
    }

    @Test
    @Order(3)
    @Disabled
    public void testAskQuestion() {
	String response = client.ask("Joe", "what is your name?");
	System.out.println(response);
	assertNotNull(response);
    }


    @Test
    @Order(4)
    public void testReactToObservation() {
	assertTrue(client.addObservation("Joe", "The yellow house is on fire", true));
    }

    @Test
    @Order(6)
    @Disabled
    public void testGameUpdates() {
	client.updateState();
    }
}
