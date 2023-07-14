package io.github.nickm980.smallville.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.prompts.Prompts;
import io.github.nickm980.smallville.prompts.dto.ObjectChangeResponse;

public class UpdateLocations extends AgentUpdate {

    @Override
    public boolean update(Prompts converter, World world, Agent agent, UpdateInfo info) {
	LOG.info("[Locations] Updating location states");

	ObjectChangeResponse[] objects = converter.getObjectsChangedBy(agent);

	if (objects.length > 0) {
	    for (ObjectChangeResponse response : objects) {
		if (response != null) {
		    world.setState(response.getObject(), response.getState());
		}
	    }
	}

	LOG.info("[Locations] Location states updated");

	return next(converter, world, agent, info);
    }
}
