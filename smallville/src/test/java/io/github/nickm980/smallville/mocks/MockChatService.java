package io.github.nickm980.smallville.mocks;

import java.util.List;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.Dialog;
import io.github.nickm980.smallville.memory.Plan;
import io.github.nickm980.smallville.memory.Reflection;
import io.github.nickm980.smallville.prompts.Prompts;
import io.github.nickm980.smallville.prompts.dto.CurrentActivity;
import io.github.nickm980.smallville.prompts.dto.ObjectChangeResponse;
import io.github.nickm980.smallville.prompts.dto.Reaction;

public class MockChatService implements Prompts {

    @Override
    public ObjectChangeResponse[] getObjectsChangedBy(Agent agent) {
	return null;
    }

    @Override
    public List<Plan> parsePlans(String input) {
	return null;
    }

    @Override
    public CurrentActivity getCurrentActivity(Agent agent) {
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
    public int[] getWeights(Agent agent) {
	return null;
    }

    @Override
    public Reflection createReflectionFor(Agent agent) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Reaction shouldUpdatePlans(Agent agent, String observation) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Conversation getConversationIfExists(Agent agent, Agent other, String topic) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Dialog saySomething(Agent agent, String observation) {
	// TODO Auto-generated method stub
	return null;
    }

}
