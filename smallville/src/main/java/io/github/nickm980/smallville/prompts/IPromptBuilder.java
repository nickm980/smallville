package io.github.nickm980.smallville.prompts;

import java.util.List;

import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.Conversation;
import io.github.nickm980.smallville.models.Location;
import io.github.nickm980.smallville.models.SimulatedLocation;

/**
 * This interface represents a builder for creating prompts.
 */
public interface IPromptBuilder {

    /**
     * Sets the agent for whom the prompt will be built.
     * 
     * @param agent the agent for whom the prompt will be built
     * @return the prompt builder instance
     */
    PromptBuilder withAgent(Agent agent);

    /**
     * Sets the list of locations relevant to the prompt.
     * 
     * @param locations the list of locations relevant to the prompt
     * @return the prompt builder instance
     */
    PromptBuilder withLocations(List<SimulatedLocation> locations);

    /**
     * Sets the conversation relevant to the prompt.
     * 
     * @param conversation the conversation relevant to the prompt
     * @return the prompt builder instance
     */
    PromptBuilder withConversation(Conversation conversation);

    /**
     * Creates a new conversation with another agent.
     * 
     * @param other the other agent participating in the conversation
     * @param topic the topic of the conversation
     * @return the prompt builder instance
     */
    PromptBuilder createConversationWith(Agent other);

    /**
     * Creates a suggestion for how to react to a given observation.
     * 
     * @param observation the observation to react to
     * @return the prompt builder instance
     */
    PromptBuilder createReactionSuggestion(String observation);

    /**
     * Adds the current plan of the agent to the prompt.
     * 
     * @return the prompt builder instance
     */
    PromptBuilder createCurrentPlanPrompt();

    /**
     * Ranks the memories of the agent and adds them to the prompt.
     * 
     * @return the prompt builder instance
     */
    PromptBuilder createMemoryRankPrompt();

    /**
     * Adds the future plans of the agent, given a time frame, to the prompt.
     * 
     * @param time the time frame for the future plans
     * @return the prompt builder instance
     */
    PromptBuilder createFuturePlansPrompt(TimePhrase time);

    /**
     * 
     * Builds and returns a Prompt object based on the previously specified
     * properties. Must call a create method before building. Global parameters, as
     * indicated by brackets [] are filled in
     * 
     * @return The created Prompt object with all global placeholders formatted.
     */
    Prompt build();

    /**
     * Ask an agent a question and get the result back as just the answer (not in
     * JSON format)
     * <p>
     * Example: chat.send(createAskQuestionPrompt("What do you have planned for
     * tomorrow?")) should return "Tomorrow I plan on..."
     * 
     * @param question
     * @return the prompt builder instance
     */
    PromptBuilder createAskQuestionPrompt(String question);
}
