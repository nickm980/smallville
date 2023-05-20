package io.github.nickm980.smallville.prompts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import io.github.nickm980.smallville.config.Config;
import io.github.nickm980.smallville.exceptions.SmallvilleException;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.Conversation;
import io.github.nickm980.smallville.models.Location;
import io.github.nickm980.smallville.models.SimulatedLocation;
import io.github.nickm980.smallville.models.SimulatedObject;

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

    @Override
    public PromptBuilder createConversationWith(Agent other) {
	// TODO: add relevant memories
	prompt = Config.getPrompts().getCreateConversationWith();

	prompt
	    .replace("%other_summary_description%", atomicBuilder.getAgentSummaryDescription(other))
	    .replace("%other_name%", other.getFullName());
	return this;
    }

    @Override
    public PromptBuilder createReactionSuggestion(String observation) {
	prompt = Config.getPrompts().getCreateReactionSuggestion();

	prompt = prompt
	    .replace("%current_activity%", data.getAgent().getCurrentActivity())
	    .replace("%relevant_memories%", atomicBuilder.buildRelevantMemories(data.getAgent(), observation))
	    .replace("%observation%", observation);

	return this;
    }

    @Override
    public PromptBuilder createCurrentPlanPrompt() {
	prompt = Config.getPrompts().getCreateCurrentPlanPrompt();
	return this;
    }

    @Override
    public PromptBuilder createMemoryRankPrompt() {
	prompt = Config.getPrompts().getCreateMemoryRankPrompt();

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
    public PromptBuilder createFuturePlansPrompt(TimePhrase time) {
	prompt = Config.getPrompts().getCreateFuturePlansPrompt();
	return this;
    }

    @Override
    public PromptBuilder createAskQuestionPrompt(String question) {
	prompt = Config.getPrompts().getCreateAskQuestionPrompt();

	prompt = prompt
	    .replace("%question%", question)
	    .replace("%relevant_memories%", atomicBuilder.buildRelevantMemories(data.getAgent(), question));

	return this;
    }

    public PromptBuilder createPastAndPresent() {
	prompt = Config.getPrompts().getCreatePastAndPresent();
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
	    .replace("[Current Location's Objects]", atomicBuilder.getObjects(agent.getLocation().getObjects()))
	    .replace("[Current Activity]", agent.getCurrentActivity())
	    .replace("[Last Activity]", agent.getLastActivity())
	    .replace("[Future Plans]", "Plans: " + atomicBuilder.asNaturalLanguage(agent.getPlans()));

	return new Prompt.User(prompt);
    }

    public PromptBuilder createObjectUpdates(String tenses) {
	prompt = Config.getPrompts().getCreateObjectUpdates();

	// tenses is "<name> is no longer <past activity> and is now <current activity>"
	prompt = prompt.replace("%tenses%", tenses);
	return this;
    }

    public PromptBuilder createExactLocation() {
	prompt = Config.getPrompts().getPickLocation();

	return this;
    }
}