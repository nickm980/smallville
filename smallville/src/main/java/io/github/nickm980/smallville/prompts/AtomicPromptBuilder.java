package io.github.nickm980.smallville.prompts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.config.Config;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.Location;
import io.github.nickm980.smallville.models.NaturalLanguageConvertible;
import io.github.nickm980.smallville.models.SimulatedLocation;
import io.github.nickm980.smallville.models.SimulatedObject;
import io.github.nickm980.smallville.models.memory.Characteristic;
import io.github.nickm980.smallville.models.memory.Memory;
import io.github.nickm980.smallville.models.memory.Plan;

/**
 * Creates the variable prompts and converts objects to natural language
 *
 */
public class AtomicPromptBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(AtomicPromptBuilder.class);
    /**
     * [Current Time]
     * <p>
     * Example "It is currently 4:56 pm"
     * 
     * @param time Current time
     * @return Current time as natural language
     */
    public String getTimeAsString(LocalDateTime time) {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Config.getConfig().getTimeFormat());
	return "It is currently " + time.format(formatter);
    }

    /**
     * [World Description]
     * <p>
     * Location listing of the world
     * 
     * @param list
     * @return
     */
    public String getWorldDescription(List<SimulatedLocation> list) {
	var prompt = """
		Only use the following locations in your response.

		Locations:
		""";

	for (Location location : list) {
	    prompt += location.asNaturalLanguage() + "; ";
	}

	return prompt;
    }

    /**
     * [Agent's Summary Description]
     * <p>
     * This summary comprises agents’ identity information (e.g., name and
     * personality), as well as a description of their main motivational drivers and
     * statements that describes their current occupation and self-assessment.
     * <p>
     * To save costs it currently filters memories by instanceof
     * {@link Characteristic} for the description
     * 
     * @return
     */
    public String getAgentSummaryDescription(Agent person) {
	var prompt = Config.getPrompts().getAgentSummaryDescription();

	List<Characteristic> characteristics = person.getCharacteristics();
	String descriptions = characteristics.stream().map(Memory::getDescription).collect(Collectors.joining("; "));

	prompt = prompt
	    .replace("%time%", LocalDateTime.now().toString())
	    .replace("%name%", person.getFullName())
	    .replace("%description%", descriptions)
	    .replace("%location%", person.getLocation().getName());

	if (!person.getPlans().isEmpty()) {
	    prompt = prompt
		.replace("%plans%", "Future Plans: " + asNaturalLanguage(person.getMemoryStream().getShortTermPlans()));
	} else {
	    prompt = prompt.replace("%plans%", "");
	}
	
	return prompt;
    }

    public String buildRelevantMemories(Agent agent, String observation) {
	String result = "";

	for (Memory memory : agent.getMemoryStream().getRelevantMemories(observation)) {
	    result += memory.getDescription();
	}

	LOG.info(agent.getFullName() + "'s relevant memories (" + observation + "): " + result);
	return result;
    }

    public String asNaturalLanguage(Collection<? extends NaturalLanguageConvertible> convertibles) {
	String result = "";

	for (NaturalLanguageConvertible convertible : convertibles) {
	    result += convertible.asNaturalLanguage() + "; ";
	}

	return result;
    }

    public CharSequence getObjects(List<SimulatedObject> objects) {
	String result = "";

	for (var obj : objects) {
	    result += obj.getName() + ", ";
	}

	return result;
    }

    public CharSequence getNextPlan(Agent agent) {
	String result = "";

	Plan plan = agent.getPlans().stream().sorted(new Comparator<Plan>() {
	    @Override
	    public int compare(Plan o1, Plan o2) {
		return o1.getTime().compareTo(o2.getTime());
	    }
	}).findFirst().orElse(null);

	if (plan == null) {
	    return result;
	}

	return "The next plan for the day is: " + plan.asNaturalLanguage();
    }
}
