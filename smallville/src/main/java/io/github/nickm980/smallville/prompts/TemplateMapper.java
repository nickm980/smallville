package io.github.nickm980.smallville.prompts;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.memory.MemoryStream;
import io.github.nickm980.smallville.memory.Plan;
import io.github.nickm980.smallville.memory.PlanType;

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
	data.put("agent.locationName", agent.getLocation().getFullPath());
	data.put("agent.description", stream.getCharacteristics().stream().map(c -> c.getDescription()).collect(Collectors.toList()));
	data.put("agent.traits", agent.getTraits());

	return new TemplateEngine().format(prompt, data);
    }

    public Map<String, Object> fromAgent(Agent agent) {
	Map<String, Object> result = new HashMap<String, Object>();

	MemoryStream stream = agent.getMemoryStream();
	String desc = String.join("; ", stream.getCharacteristics().stream().map(c -> c.getDescription()).collect(Collectors.toList()));

	if (stream.getPlans() == null) {
	    LOG.error("no plans found!!!");
	}

	result.put("name", agent.getFullName());
	result.put("memories", agent.getMemoryStream().getMemories().stream().limit(10).collect(Collectors.toList()));
	result.put("activity", agent.getCurrentActivity());
	result.put("lastActivity", agent.getLastActivity());
	result.put("summary", buildAgentSummary(agent));
	result.put("locationName", agent.getLocation().getFullPath());
	result.put("locationChildren", agent.getLocation().getFullPath());
	result.put("description", desc);
	result.put("plans", stream.getPlans());
	result.put("shortPlans", stream.getPlans(PlanType.SHORT_TERM));
	result.put("recentMemories", agent.getMemoryStream().getRecentMemories());
	/*
	 * agent.plansBlock is a number list of the upcoming plans with a block [...]
	 * between the current time and the next time.
	 */
	result.put("plansBlock", buildPlansBlock(agent.getFullName(), stream.getPlans()));
	return result;
    }

    public String buildPlansBlock(String name, List<Plan> plans) {
	String result = "";
	LocalDateTime time = LocalDateTime.now();

	boolean includeBlock = false;
	int index = 0;
	boolean hasBeenUpdated = false;

	for (Plan plan : plans) {
	    result += "- " + plan.getDescription() + System.lineSeparator();
	    boolean hasPlanPast = plan.getTime().compareTo(time) < 0;

	    if (!hasBeenUpdated
		    && ((hasPlanPast && includeBlock) || index == plans.size() && !result.contains("[...]"))) {
		result += "[...]" + System.lineSeparator();
		hasBeenUpdated = true;
	    }

	    if (hasPlanPast) {
		includeBlock = true;
	    }

	    index++;
	}

	if (plans == null || plans.isEmpty()) {
	    result = """
	    	- $name will wake up at 8:00 AM
	    	[...]
	    	- $name will get ready for bed at 10:00 PM
	    	""";
	}

	result.replace("$name", name);

	return result;
    }

    public String buildRelevantMemories(Agent agent, String observation) {
	List<String> memories = agent
	    .getMemoryStream()
	    .getRelevantMemories(observation)
	    .stream()
	    .map(item -> item.getDescription())
	    .collect(Collectors.toList());

	String result = String.join("; ", memories);
	
	LOG.debug(agent.getFullName() + "'s relevant memories (" + observation + "): " + result);

	return result;
    }
}
