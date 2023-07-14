package io.github.nickm980.smallville.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.memory.Reflection;
import io.github.nickm980.smallville.prompts.Prompts;

public class UpdateReflection extends AgentUpdate {

    @Override
    public boolean update(Prompts service, World world, Agent agent, UpdateInfo info) {
	/*
	 * The score to cutoff memories by. Fine tune this value to get the desired
	 * result. Relfections should be triggered 2-3 times per day. Fine tune this
	 * value to vary results
	 */
	int cutoff = SmallvilleConfig.getConfig().getReflectionCutoff();

	LOG.info("[Reflections] Memory weight sum: " + agent.getMemoryStream().sumRecency() + " / " + cutoff);
	if (agent.getMemoryStream().sumRecency() > cutoff) {
	    LOG.info("[Reflections] Reflecting on recent memories");
	    Reflection reflection = service.createReflectionFor(agent);
	    agent.getMemoryStream().add(reflection);
	    LOG.info("[Reflections] Reflection: " + reflection.getDescription());
	}

	return next(service, world, agent, info);
    }
}
