package io.github.nickm980.smallville.llm.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.SimulatedObject;

public class UpdateLocations extends AgentUpdate {

    @Override
    public boolean update(ChatService converter, World world, Agent agent) {
	SimulatedObject[] objects = converter.getObjectsChangedBy(agent);

	if (objects.length > 0) {
	    world.updateAll(objects);
	}

	return next(converter, world, agent);
    }
}
