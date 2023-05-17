package io.github.nickm980.smallville.api;

import java.util.HashMap;
import java.util.Map;

import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.Conversation;
import io.github.nickm980.smallville.models.Dialog;
import io.github.nickm980.smallville.models.Location;
import io.github.nickm980.smallville.models.SimulatedObject;
import io.github.nickm980.smallville.models.memory.Characteristic;
import io.github.nickm980.smallville.models.memory.Memory;
import io.github.nickm980.smallville.models.memory.Plan;
import io.github.nickm980.smallville.models.memory.TemporalMemory;

public class ModelMapper {

    public AgentStateResponse fromAgent(Agent agent) {
	AgentStateResponse result = new AgentStateResponse();
	result.setAction(agent.getCurrentActivity());
	result.setEmoji(agent.getEmoji());
	if (agent.getLocation() != null) {
	    result.setLocation(agent.getLocation().getName());
	}
	result.setName(agent.getFullName());
	return result;
    }

    public LocationStateResponse fromLocation(Location location) {
	LocationStateResponse result = new LocationStateResponse();
	result.setName(location.getName());
	if (location instanceof SimulatedObject) {
	    result.setState(((SimulatedObject) location).getState());
	}
	return result;
    }

    public MemoryResponse fromMemory(Memory memory) {
	MemoryResponse result = new MemoryResponse();

	result.setDescription(memory.getDescription());
	result.setType("Observation");
	result.setImportance(memory.getImportance());

	if (memory instanceof Plan) {
	    result.setType("Plan");
	}

	if (memory instanceof Characteristic) {
	    result.setType("Characteristic");
	}

	if (memory instanceof TemporalMemory) {
	    result.setTime(((TemporalMemory) memory).getTime());
	}

	return result;
    }

    public ConversationResponse fromConversation(Conversation conversation) {
	ConversationResponse result = new ConversationResponse();
	Map<String, String> messages = new HashMap<String, String>();

	for (Dialog dialog : conversation.getDialog()) {
	    messages.put(dialog.getName(), dialog.getMessage());
	}

	result.setMessages(messages);
	return result;
    }
}
