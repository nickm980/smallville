package io.github.nickm980.smallville.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.prompts.Prompts;

/**
 * The AgentUpdate class is an abstract class that serves as a base for
 * implementing agent update functionality.
 * <p>
 * It provides methods for setting the next update handler and starting the
 * update process.
 */
public abstract class AgentUpdate {

    private AgentUpdate handler;
    protected AgentUpdate parent;
    protected final Logger LOG = LoggerFactory.getLogger(AgentUpdate.class);

    public AgentUpdate setNext(AgentUpdate handler) {
	this.handler = handler;
	this.handler.parent = this;
	return handler;
    }

    public boolean start(Prompts service, World world, Agent agent, UpdateInfo info) {
	AgentUpdate node = this;

	while (node.parent != null) {
	    node = node.parent;
	}

	return node.update(service, world, agent, info);
    }

    protected abstract boolean update(Prompts converter, World world, Agent agen, UpdateInfo info);

    protected boolean next(Prompts converter, World world, Agent agent, UpdateInfo info) {
	if (handler != null) {
	    return handler.update(converter, world, agent, info);
	} else {
	    return true;
	}
    }
}
