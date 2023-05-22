package io.github.nickm980.smallville.update;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.Conversation;
import io.github.nickm980.smallville.models.memory.Memory;
import io.github.nickm980.smallville.models.memory.Plan;
import io.github.nickm980.smallville.prompts.response.CurrentActivity;
import io.github.nickm980.smallville.prompts.response.ObjectChangeResponse;
import io.github.nickm980.smallville.prompts.response.Reaction;

interface IChatService {

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

}
