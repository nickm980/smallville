package io.github.nickm980.smallville.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.memory.Reflection;

public class UpdateReflection extends AgentUpdate {

    @Override
    public boolean update(IChatService service, World world, Agent agent) {
	/*
	 * The score to cutoff memories by. Fine tune this value to get the desired
	 * result. Relfections should be triggered 2-3 times per day. Fine tune this
	 * value to vary results
	 */
	int cutoff = SmallvilleConfig.getConfig().getReflectionCutoff();

	LOG.info("[Reflections] Memory weight sum: " + agent.getMemoryStream().sumRecency() + " / " + cutoff);
	if (agent.getMemoryStream().sumRecency() > cutoff) {
	    LOG.info("[Reflection] Reflecting on recent memories");
	    Reflection reflection = service.reflectOn(agent);
	    agent.getMemoryStream().add(reflection);
	    LOG.info("[Reflection] Reflection: " + reflection.getDescription());
	}

	return next(service, world, agent);
    }
}
