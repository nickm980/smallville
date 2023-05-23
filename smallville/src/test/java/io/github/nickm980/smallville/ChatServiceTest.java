package io.github.nickm980.smallville;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.AgentLocation;
import io.github.nickm980.smallville.entities.ObjectState;
import io.github.nickm980.smallville.entities.SimulatedLocation;
import io.github.nickm980.smallville.entities.SimulatedObject;
import io.github.nickm980.smallville.entities.memory.Characteristic;
import io.github.nickm980.smallville.llm.ChatGPT;
import io.github.nickm980.smallville.update.ChatService;

public class ChatServiceTest {

    @Test
    public void testLongTermPlans() {
	SimulatedLocation location = new SimulatedLocation("Eddy's House");
	SimulatedObject obj = new SimulatedObject("Stove", new ObjectState("on", List.of()), location);

	World world = new World();
	world.save(location);
	world.save(obj);

	ChatService service = new ChatService(world, new ChatGPT());
//	
//	service
//	    .getPlans(new Agent("Eddy", List.of(new Characteristic("Eddy is a nice person")),
//		    "Cooking dinner in the kitchen", new AgentLocation(location)));
    }
}
