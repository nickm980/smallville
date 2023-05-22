package io.github.nickm980.smallville.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.AgentLocation;
import io.github.nickm980.smallville.models.SimulatedObject;

public class UpdateAgentExactLocation extends AgentUpdate {

    @Override
    public boolean update(ChatService converter, World world, Agent agent) {
	String objectName = converter.getExactLocation(agent);
	SimulatedObject object = world.getObjectByName(objectName);
	agent.setLocation(new AgentLocation(agent.getLocation(), object));
	
	return next(converter, world, agent);
    }
}
