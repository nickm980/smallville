package io.github.nickm980.smallville;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.AgentLocation;
import io.github.nickm980.smallville.entities.ObjectState;
import io.github.nickm980.smallville.entities.SimulatedLocation;
import io.github.nickm980.smallville.entities.SimulatedObject;
import io.github.nickm980.smallville.entities.memory.Characteristic;
import io.github.nickm980.smallville.entities.memory.MemoryStream;
import io.github.nickm980.smallville.entities.memory.Plan;
import io.github.nickm980.smallville.llm.ChatGPT;
import io.github.nickm980.smallville.update.ChatService;

public class PlansParsingTest {

    @Test
    public void testParsingPlans() {
	ChatService service = new ChatService(new World(), new ChatGPT());

	List<Plan> plans = service.parsePlans("""
		Walk to the farmhouse at 2:00 PM
		\nMeet with the farmer to discuss crops at 2:30 PM
		\nHelp with feeding the animals from 3:00 PM - 4:00 PM
		\nRead a book under the shade of a tree from 4:00 PM - 5:00 PM
		\nHave dinner at home at 6:00 PM.
		""");

	assertTrue(plans.size() == 5);
    }

    @Test
    public void testWithExtraNumbers() {
	ChatService service = new ChatService(new World(), new ChatGPT());

	List<Plan> plans = service.parsePlans("""
		. Walk to the farmhouse at 2:00 PM
		\n- Meet with the farmer to discuss crops at 2:30 PM
		\n- Help with feeding the animals from 3:00 PM - 4:00 PM
		\n- Read a book under the shade of a tree from 4:00 PM - 5:00 PM
		\n- Have dinner at home at 6:00 PM.
		""");

	assertTrue(plans.size() == 5);
    }

    @Test
    public void testTime() {
	ChatService service = new ChatService(new World(), new ChatGPT());
	List<Plan> plans = service.parsePlans("""
		\nI will then go to the Green House and sleep from 12:05 AM.
		\nI will wake up and make breakfast from 11:30 PM - 9:30 AM.
		\nI will then go to the Forest and spend some time gathering branches from 10:00 AM - 11:00 AM.
				""");
	MemoryStream stream = new MemoryStream();

	stream.addAll(plans);

	assertTrue(stream.getPlans().size() == 1);
    }
    
    @Test
    public void testPrunedPlans() {
	World world = new World();
	SimulatedLocation location = new SimulatedLocation("location");
	SimulatedObject obj = new SimulatedObject("obj", new ObjectState("off", List.of()), location);
	
	world.create(obj);
	world.create(location);
	
	Agent agent = new Agent("name", List.of(new Characteristic("desc")), "current", new AgentLocation(location));
	agent.setGoal("Run for president");
	agent.setCurrentActivity("Doing nothing");
	agent.setCurrentActivity("making dinner");
	agent.getMemoryStream().addAll(List.of(new Plan("plan", LocalDateTime.now().plusMinutes(5)), new Plan("plan2", LocalDateTime.now().minusHours(1))));
	
	world.create(agent);
	
	
	assertEquals(1, agent.getMemoryStream().getPlans().size());
    }
}
