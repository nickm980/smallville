package io.github.nickm980.smallville.mocks;

import java.util.List;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.memory.Memory;
import io.github.nickm980.smallville.entities.memory.Plan;
import io.github.nickm980.smallville.prompts.dto.CurrentActivity;
import io.github.nickm980.smallville.prompts.dto.ObjectChangeResponse;
import io.github.nickm980.smallville.prompts.dto.Reaction;
import io.github.nickm980.smallville.update.IChatService;

public class MockChatService implements IChatService {

    @Override
    public String getExactLocation(Agent agent) {
	return agent.getLocation().getName();
    }

    @Override
    public List<Memory> convertFuturePlansToMemories(List<Plan> plans) {
	return null;
    }

    @Override
    public ObjectChangeResponse[] getObjectsChangedBy(Agent agent) {
	return null;
    }

    @Override
    public List<Plan> parsePlans(String input) {
	return null;
    }

    @Override
    public Conversation getConversationIfExists(Agent agent, Agent other) {
	return null;
    }

    @Override
    public CurrentActivity getCurrentPlan(Agent agent) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<Plan> getShortTermPlans(Agent agent) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<Plan> getPlans(Agent agent) {
	return null;
    }

    @Override
    public String ask(Agent agent, String question) {
	return null;
    }

    @Override
    public Reaction getReaction(Agent agent, String observation) {
	return null;
    }

    @Override
    public int[] getWeights(Agent agent) {
	return null;
    }

}
