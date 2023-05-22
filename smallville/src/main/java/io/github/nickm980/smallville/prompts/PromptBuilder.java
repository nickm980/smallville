package io.github.nickm980.smallville.prompts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import io.github.nickm980.smallville.exceptions.SmallvilleException;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.Conversation;
import io.github.nickm980.smallville.models.SimulatedLocation;

public class PromptBuilder implements IPromptBuilder {

    private PromptData data;
    private AtomicPromptBuilder atomicBuilder;
    private String prompt;

    public PromptBuilder() {
	this.atomicBuilder = new AtomicPromptBuilder();
	this.data = new PromptData();
    }

    @Override
    public PromptBuilder withAgent(Agent agent) {
	data.setAgent(agent);
	return this;
    }

    @Override
    public PromptBuilder withLocations(List<SimulatedLocation> locations) {
	data.setLocations(locations);
	return this;
    }

    @Override
    public PromptBuilder withConversation(Conversation conversation) {
	data.setConversation(conversation);
	return this;
    }

    public PromptBuilder withPrompt(String prompt) {
	this.prompt = prompt;
	return this;
    }

    @Override
    public PromptBuilder createConversationWith(Agent other) {
	prompt
	    .replace("%other_summary_description%", atomicBuilder.getAgentSummaryDescription(other))
	    .replace("%other_name%", other.getFullName());
	return this;
    }

    @Override
    public PromptBuilder createReactionSuggestion(String observation) {
	prompt = prompt
	    .replace("%relevant_memories%", atomicBuilder.buildRelevantMemories(data.getAgent(), observation))
	    .replace("%observation%", observation);

	return this;
    }

    @Override
    public PromptBuilder createMemoryRankPrompt() {
	prompt = String
	    .format(prompt,
		    data
			.getAgent()
			.getMemoryStream()
			.getUnweightedMemories()
			.stream()
			.map(memory -> memory.getDescription())
			.collect(Collectors.joining(", ")));
	return this;
    }

    @Override
    public PromptBuilder createAskQuestionPrompt(String question) {
	prompt = prompt
	    .replace("%question%", question)
	    .replace("%relevant_memories%", atomicBuilder.buildRelevantMemories(data.getAgent(), question));

	return this;
    }

    @Override
    public Prompt build() {
	if (prompt == null || prompt.isEmpty()) {
	    throw new SmallvilleException("Must call a creation function to make a new prompt first");
	}

	Agent agent = data.getAgent();

	prompt = prompt
	    .replace("[World Description]", atomicBuilder.getWorldDescription(data.getLocations()))
	    .replace("[Agent Summary Description]", atomicBuilder.getAgentSummaryDescription(agent))
	    .replace("[Current Time]", atomicBuilder.getTimeAsString(LocalDateTime.now()))
	    .replace("[Agent Name]", agent.getFullName())
	    .replace("[Current Location]", agent.getLocation().getName())
	    .replace("[Current Location's Objects]", atomicBuilder.getObjects(agent.getLocation().getObjects()))
	    .replace("[Current Activity]", agent.getCurrentActivity())
	    .replace("[Last Activity]", agent.getLastActivity())
	    .replace("[Most Recent Plan]", atomicBuilder.getNextPlan(agent))
	    .replace("[Future Plans]", "Plans: " + atomicBuilder.asNaturalLanguage(agent.getPlans()));

	return new Prompt.User(prompt);
    }

    public PromptBuilder createObjectUpdates(String tenses) {
	prompt = prompt.replace("%tenses%", tenses);
	return this;
    }
}