package io.github.nickm980.smallville.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.models.Agent;

public abstract class AgentUpdate {

    private AgentUpdate handler;
    protected AgentUpdate parent;
    protected final Logger LOG = LoggerFactory.getLogger(AgentUpdate.class);

    public AgentUpdate setNext(AgentUpdate handler) {
	this.handler = handler;
	this.handler.parent = this;
	return handler;
    }

    public boolean start(ChatService service, World world, Agent agent) {
	AgentUpdate node = this;

	while (node.parent != null) {
	    node = node.parent;
	}

	return node.update(service, world, agent);
    }

    protected abstract boolean update(ChatService converter, World world, Agent agent);

    protected boolean next(ChatService converter, World world, Agent agent) {
	if (handler != null) {
	    return handler.update(converter, world, agent);
	} else {
	    return false;
	}
    }
}
