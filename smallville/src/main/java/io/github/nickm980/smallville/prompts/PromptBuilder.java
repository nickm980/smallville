package io.github.nickm980.smallville.prompts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.exceptions.SmallvilleException;
import io.github.nickm980.smallville.prompts.dto.DateModel;
import io.github.nickm980.smallville.prompts.dto.WorldModel;

public class PromptBuilder {

    private Map<String, Object> data;
    private String prompt;
    private final TemplateMapper prompts;
    private Agent agent;
    
    public PromptBuilder() {
	this.data = new HashMap<>();
	this.prompts = new TemplateMapper();

	data.put("ping", "pong");
	data.put("date", new DateModel());
    }

    public PromptBuilder withAgent(Agent agent) {
	this.agent = agent;
	data.put("agent", prompts.fromAgent(agent));

	if (data.get("memories.unranked") == null) {
	    data.put("memories.unranked", agent.getMemoryStream().getUnweightedMemories());
	}

	if (data.get("memories.characteristics") == null) {
	    data.put("memories.characteristics", agent.getMemoryStream().getMemories());
	}

	if (data.get("observation") != null) {
	    data.put("memories.relevant", prompts.buildRelevantMemories(agent, (String) data.get("observation")));
	}

	return this;
    }

    public PromptBuilder withStatements(List<String> statements) {
	String result = "";

	for (int i = 0; i < statements.size(); i++) {
	    result += i + 1 + ") " + statements.get(i);
	    result += "\n";
	}

	data.put("statements", result);
	return this;
    }

    public PromptBuilder withLocations(List<Location> locations) {
	data.put("locations", locations);
	return this;
    }

    public PromptBuilder withConversation(Conversation conversation) {
	data.put("conversation", conversation);
	return this;
    }

    public PromptBuilder withOther(Agent other) {
	data.put("other", prompts.fromAgent(other));
	return this;
    }

    public PromptBuilder withWorld(World world) {
	data.put("world", WorldModel.fromWorld(agent.getFullName(), world));

	if (data.get("locations") == null) {
	    data.put("locations", world.getLocations());
	}

	return this;
    }

    public PromptBuilder withQuestion(String question) {
	data.put("question", question);
	return this;
    }

    public PromptBuilder withObservation(String observation) {
	data.put("observation", observation);
	return this;
    }

    public PromptBuilder setPrompt(String prompt) {
	this.prompt = prompt;
	return this;
    }

    public PromptBuilder withTense(String tenses) {
	data.put("tenses", tenses);
	return this;
    }

    public PromptRequest build() {
	if (prompt == null || prompt.isEmpty()) {
	    throw new SmallvilleException("Must call a creation function to make a new prompt first");
	}

	TemplateEngine engine = new TemplateEngine();
	prompt = engine.format(prompt, data);
	PromptRequest result = new PromptRequest.User(prompt);

	return result;
    }
}