package io.github.nickm980.smallville.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.prompts.Prompts;

public class UpdateProgress extends AgentUpdate {

    private final Progress progress;

    public UpdateProgress(Progress progress) {
	this.progress = progress;
    }

    @Override
    protected boolean update(Prompts converter, World world, Agent agent) {
	progress.update();
	return next(converter, world, agent);
    }
}
