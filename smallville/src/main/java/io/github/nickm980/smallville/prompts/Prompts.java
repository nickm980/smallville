package io.github.nickm980.smallville.prompts;

import java.util.List;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.Dialog;
import io.github.nickm980.smallville.memory.Plan;
import io.github.nickm980.smallville.memory.Reflection;
import io.github.nickm980.smallville.prompts.dto.CurrentActivity;
import io.github.nickm980.smallville.prompts.dto.ObjectChangeResponse;
import io.github.nickm980.smallville.prompts.dto.Reaction;

/**
 * IChatService is responsible for creating prompts, sending them to the LLM,
 * and mapping the responses into objects
 *
 */
public interface Prompts {

    /**
     * Retrieves the objects changed by an agent.
     * 
     * @param agent The agent for which to retrieve the changed objects.
     * @return An array of `ObjectChangeResponse` objects representing the changed
     *         objects.
     */
    ObjectChangeResponse[] getObjectsChangedBy(Agent agent);

    /**
     * Parses plans from input.
     * 
     * @param input The input string containing the plans to parse.
     * @return A list of parsed plans.
     */
    List<Plan> parsePlans(String input);

    /**
     * Retrieves an existing conversation between two agents.
     * 
     * @param agent The agent initiating the conversation.
     * @param other The other agent involved in the conversation.
     * @return The existing conversation between the two agents, if it exists.
     */
    Conversation getConversationIfExists(Agent agent, Agent other, String topic);

    /**
     * Retrieves the current activity of an agent.
     * 
     * @param agent The agent for which to retrieve the current plan.
     * @return The current activity of the agent.
     */
    CurrentActivity getCurrentActivity(Agent agent);

    /**
     * Retrieves the short-term plans of an agent.
     * 
     * @param agent The agent for which to retrieve the short-term plans.
     * @return A list of short-term plans for the agent.
     */
    List<Plan> getShortTermPlans(Agent agent);

    /**
     * Retrieves the plans of an agent.
     * 
     * @param agent The agent for which to retrieve the plans.
     * @return A list of plans for the agent.
     */
    List<Plan> getPlans(Agent agent);

    /**
     * Sends a question to the agent and retrieves the response.
     * 
     * @param agent    The agent to ask the question.
     * @param question The question to ask.
     * @return The response from the agent.
     */
    String ask(Agent agent, String question);

    /**
     * Retrieves the weights associated with an agent's memories.
     * 
     * @param agent The agent to retrieve the weights for.
     * @return An array of weights associated with the agent, in order with the
     *         agent's memory stream.
     */
    int[] getWeights(Agent agent);

    /**
     * Creates a reflection for an agent. A reflection is a higher level thought
     * based on what is inside the agent's memory stream, including past
     * observations, future plans, and other reflections.
     * 
     * @param agent The agent to create the reflection for.
     * @return The reflection.
     */
    Reflection createReflectionFor(Agent agent);

    /**
     * Determines if plans should be updated based on an observation.
     * 
     * @param agent       The agent to check for plan updates.
     * @param observation The observation to base the plan update decision on.
     * @return `true` if the plans should be updated, `false` otherwise.
     */
    Reaction shouldUpdatePlans(Agent agent, String observation);

    Dialog saySomething(Agent agent, String observation);
}
