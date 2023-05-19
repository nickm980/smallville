package io.github.nickm980.smallville.llm.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.prompts.response.ObjectChangeResponse;

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
