package io.github.nickm980.smallville;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
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
	assertTrue(client.createLocation("Yellow House"));
	assertTrue(client.createObject("Yellow House", "Ball", new ObjectState("deflated")));
    }

    @Test
    @Order(2)
    public void testAgentCreation() {
	List<String> memories = List.of("memory 1", "memory 2");
	boolean success = client.createAgent("Joe", memories, "Yellow House: Ball", "cooking food");

	assertEquals(true, success);
    }

    @Test
    @Order(3)
    public void testAskQuestion() {
	String response = client.ask("Joe", "what is your name?");
	System.out.println(response);
	assertNotNull(response);
    }

    @Test
    @Order(4)
    public void testAddObservationWithoutReacting() {
	assertTrue(client.addObservation("Joe", "The house is on fire", false));
    }

    @Test
    @Order(5)
    public void testReactToObservation() {
	assertTrue(client.addObservation("Joe", "The house is on fire", true));
    }

    @Test
    @Order(6)
    public void testGameUpdates() {
	client.updateState();
    }
}
