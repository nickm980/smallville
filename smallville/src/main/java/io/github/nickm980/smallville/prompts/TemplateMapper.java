package io.github.nickm980.smallville.prompts;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.SimulationTime;
import io.github.nickm980.smallville.entities.memory.Memory;
import io.github.nickm980.smallville.entities.memory.MemoryStream;
import io.github.nickm980.smallville.entities.memory.Plan;

/**
 * Creates the prompts used by other prompts and converts objects to natural
 * language
 *
 */
public class TemplateMapper {
    private static final Logger LOG = LoggerFactory.getLogger(TemplateMapper.class);

    public String buildAgentSummary(Agent agent) {
	String prompt = SmallvilleConfig.getPrompts().getAgent().getSummary();
	MemoryStream stream = agent.getMemoryStream();
	Map<String, Object> data = new HashMap<String, Object>();
	data.put("agent.name", agent.getFullName());
	data.put("agent.locationName", agent.getLocation().getName());
	data.put("agent.description", stream.getCharacteristics().stream().map(c -> c.getDescription()).toList());

	return new TemplateEngine().format(prompt, data);
    }

    public Map<String, Object> fromAgent(Agent agent) {
	MemoryStream stream = agent.getMemoryStream();

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
		    String.join("; ", stream.getCharacteristics().stream().map(c -> c.getDescription()).toList()));

	result.put("_internal", agent);

	result.put("plans", stream.getPlans());
	List<Memory> memories = agent.getMemoryStream().getRecentMemories();
	result.put("recentMemories", memories);

	if (!stream.getPlans().isEmpty()) {
	    Plan firstSmallerPlan = new Plan("", LocalDateTime.MIN);
	    Plan firstGreaterPlan = new Plan("", LocalDateTime.MAX);

	    for (Plan plan : stream.getPlans()) {
		LocalDateTime planDateTime = plan.getTime();

		if (planDateTime.compareTo(SimulationTime.now()) <= 0) {
		    if (firstSmallerPlan == null || planDateTime.isAfter(firstSmallerPlan.getTime())) {
			firstSmallerPlan = plan;
		    }
		} else {
		    if (firstGreaterPlan == null || planDateTime.isBefore(firstGreaterPlan.getTime())) {
			firstGreaterPlan = plan;
		    }
		}
	    }

	    result.put("firstSmallerPlan", firstSmallerPlan.getDescription());
	    result.put("firstGreaterPlan", firstGreaterPlan.getDescription());
	}

	return result;
    }

    public String buildRelevantMemories(Agent agent, String observation) {
	List<String> memories = agent
	    .getMemoryStream()
	    .getRelevantMemories(observation)
	    .stream()
	    .map(item -> item.getDescription())
	    .toList();

	String result = String.join("; ", memories);
	LOG.info(agent.getFullName() + "'s relevant memories (" + observation + "): " + result);

	return result;
    }
}
