package io.github.nickm980.smallville.llm.update;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.prompts.response.Reaction;

public class UpdateReaction extends AgentUpdate {

    private String observation;

    public UpdateReaction(String observation) {
	this.observation = observation;
    }

    @Override
    public boolean update(ChatService service, World world, Agent agent) {
	LOG.info("[Updater / Reaction] Checking if an agent will react to an observation");

	Reaction reaction = service.getReaction(agent, observation);

	if (reaction.willReact()) {
//	    agent.setLocation(world.getLocation(reaction.getLocation()).orElse(null));
	    agent.setCurrentEmoji(reaction.getEmoji());
	    agent.setCurrentActivity(reaction.getCurrentActivity());
	}

	return next(service, world, agent);
    }
}
