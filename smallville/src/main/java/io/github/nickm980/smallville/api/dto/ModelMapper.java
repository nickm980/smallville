package io.github.nickm980.smallville.api.dto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.Dialog;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.entities.SimulatedObject;
import io.github.nickm980.smallville.entities.memory.Characteristic;
import io.github.nickm980.smallville.entities.memory.Memory;
import io.github.nickm980.smallville.entities.memory.Plan;
import io.github.nickm980.smallville.entities.memory.Reflection;
import io.github.nickm980.smallville.entities.memory.TemporalMemory;

public class ModelMapper {

    public AgentStateResponse fromAgent(Agent agent) {
	AgentStateResponse result = new AgentStateResponse();
	result.setAction(agent.getCurrentActivity());
	result.setEmoji(agent.getEmoji());
	if (agent.getLocation() != null) {
	    result.setLocation(agent.getLocation().getName());
	}
	result.setName(agent.getFullName());
	if (agent.getObject() != null) {
	    result.setObject(agent.getObject().asNaturalLanguage());
	}
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

	if (memory instanceof Reflection) {
	    result.setType("Reflection");
	}

	if (memory instanceof TemporalMemory) {
	    result.setTime(((TemporalMemory) memory).getTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
	}

	return result;
    }

    public List<ConversationResponse> fromConversation(Conversation conversation) {
	List<ConversationResponse> result = new ArrayList<ConversationResponse>();

	for (Dialog dialog : conversation.getDialog()) {
	    ConversationResponse response = new ConversationResponse();

	    response.setName(dialog.getName());
	    response.setMessage(dialog.getMessage());
	    result.add(response);
	}

	return result;
    }
}
