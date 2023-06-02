package io.github.nickm980.smallville.update;

import java.util.List;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.memory.Memory;
import io.github.nickm980.smallville.entities.memory.Plan;
import io.github.nickm980.smallville.entities.memory.Reflection;
import io.github.nickm980.smallville.prompts.dto.CurrentActivity;
import io.github.nickm980.smallville.prompts.dto.ObjectChangeResponse;
import io.github.nickm980.smallville.prompts.dto.Reaction;

/**
 * IChatService is responsible for creating prompts, sending them to the LLM,
 * and mapping the responses into objects
 * 
 * @author nicho
 *
 */
public interface IChatService {

    String getExactLocation(Agent agent);

    List<Memory> convertFuturePlansToMemories(List<Plan> plans);

    ObjectChangeResponse[] getObjectsChangedBy(Agent agent);

    List<Plan> parsePlans(String input);

    Conversation getConversationIfExists(Agent agent, Agent other);

    CurrentActivity getCurrentPlan(Agent agent);

    List<Plan> getShortTermPlans(Agent agent);

    List<Plan> getPlans(Agent agent);

    String ask(Agent agent, String question);

    Reaction getReaction(Agent agent, String observation);

    int[] getWeights(Agent agent);

    Reflection createReflectionFor(Agent agent);

    boolean shouldUpdatePlans(Agent agent, String observation);
}
