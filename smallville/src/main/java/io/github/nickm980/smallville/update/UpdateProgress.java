package io.github.nickm980.smallville.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;

public class UpdateProgress extends AgentUpdate {

    private final Progress progress;

    public UpdateProgress(Progress progress) {
	this.progress = progress;
    }

    @Override
    protected boolean update(IChatService converter, World world, Agent agent) {
	progress.update();
	return next(converter, world, agent);
    }
}
