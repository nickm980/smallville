package io.github.nickm980.smallville.llm.update;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.Location;
import io.github.nickm980.smallville.models.NaturalLanguageConvertible;
import io.github.nickm980.smallville.models.memory.Characteristic;
import io.github.nickm980.smallville.models.memory.Memory;

/**
 * Creates the variable prompts and converts objects to natural language
 *
 */
public class AtomicPromptBuilder {
    /**
     * [Current Time]
     * <p>
     * Example "It is currently February 13, 2023, 4:56 pm"
     * 
     * @param time Current time
     * @return Current time as natural language
     */
    public String getTimeAsString(LocalDateTime time) {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy, h:mm a");
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
    public String getWorldDescription(List<? extends Location> list) {
	var prompt = "Only use the following locations in your answer: ";

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
	var prompt = """
		Pretend you are %name%

		Name: %name%
		Description: %description%
		Current Location: %location%
		%plans%

		[Current Time]
		""";

	List<Characteristic> characteristics = person.getCharacteristics();
	String descriptions = characteristics.stream().map(Memory::getDescription).collect(Collectors.joining("; "));

	prompt = prompt
	    .replace("%time%", LocalDateTime.now().toString())
	    .replace("%name%", person.getFullName())
	    .replace("%description%", descriptions)
	    .replace("%location%", person.getLocation().getName());

	if (!person.getPlans().isEmpty()) {
	    prompt = prompt.replace("%plans%", "Future Plans: " + asNaturalLanguage(person.getPlans()));
	}

	prompt = prompt.replace("%plans%", "");

	return prompt;
    }

    public String buildRelevantMemories(Agent agent, String observation) {
	String result = "";

	for (Memory memory : agent.getMemoryStream().getRelevantMemories(observation)) {
	    result += memory.getDescription();
	}

	return result;
    }

    public String asNaturalLanguage(Collection<? extends NaturalLanguageConvertible> convertibles) {
	String result = "";

	for (NaturalLanguageConvertible convertible : convertibles) {
	    result += convertible.asNaturalLanguage() + ", ";
	}

	return result;
    }
}
