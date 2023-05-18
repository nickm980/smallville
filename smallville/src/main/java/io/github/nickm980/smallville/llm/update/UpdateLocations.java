package io.github.nickm980.smallville.llm.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.llm.response.ObjectChangeResponse;
import io.github.nickm980.smallville.models.Agent;

public class UpdateLocations extends AgentUpdate {

    @Override
    public boolean update(ChatService converter, World world, Agent agent) {
	ObjectChangeResponse[] objects = converter.getObjectsChangedBy(agent);

	if (objects.length > 0) {
	    for (ObjectChangeResponse response : objects) {
		world.changeObject(response.getObject(), response.getState());
	    }
	}

	return next(converter, world, agent);
    }
}
