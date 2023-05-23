package io.github.nickm980.smallville.prompts;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.memory.Memory;
import io.github.nickm980.smallville.entities.memory.TemporalMemory;

/**
 * Creates the prompts used by other prompts and converts objects to natural
 * language
 *
 */
public class MiniPrompts {
    private static final Logger LOG = LoggerFactory.getLogger(MiniPrompts.class);

    public String buildAgentSummary(Agent agent) {
	String prompt = SmallvilleConfig.getPrompts().getAgent().getSummary();

	Map<String, Object> data = new HashMap<String, Object>();
	data.put("agent.name", agent.getFullName());
	data.put("agent.locationName", agent.getLocation().getName());
	data.put("agent.description", agent.getCharacteristics().stream().map(c -> c.getDescription()).toList());

	return new TemplateEngine().format(prompt, data);
    }

    public Map<String, Object> fromAgent(Agent agent) {
	Map<String, Object> result = new HashMap<String, Object>();
	result.put("name", agent.getFullName());
	result.put("memories", agent.getMemoryStream().getMemories().stream().limit(10).toList());
	result.put("activity", agent.getCurrentActivity());
	result.put("lastActivity", agent.getLastActivity());
	result.put("summary", buildAgentSummary(agent));
	result.put("locationName", agent.getLocation().getName());
	result.put("locationChildren", agent.getLocation().getObjects());
	result
	    .put("description",
		    String.join("; ", agent.getCharacteristics().stream().map(c -> c.getDescription()).toList()));

	result.put("_internal", agent);

	result
	    .put("plans", agent.getPlans().stream().sorted(new TemporalMemory.TemporalComparator()).limit(2).toList());

	return result;
    }

    public String buildRelevantMemories(Agent agent, String observation) {
	String result = "";

	for (Memory memory : agent.getMemoryStream().getRelevantMemories(observation)) {
	    result += memory.getDescription();
	}

	LOG.info(agent.getFullName() + "'s relevant memories (" + observation + "): " + result);
	return result;
    }
}
