package io.github.nickm980.smallville.prompts;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nickm980.smallville.Util;
import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.Dialog;
import io.github.nickm980.smallville.llm.LLM;
import io.github.nickm980.smallville.memory.Memory;
import io.github.nickm980.smallville.memory.Plan;
import io.github.nickm980.smallville.memory.Reflection;
import io.github.nickm980.smallville.nlp.LocalNLP;
import io.github.nickm980.smallville.prompts.dto.CurrentActivity;
import io.github.nickm980.smallville.prompts.dto.ObjectChangeResponse;
import io.github.nickm980.smallville.prompts.dto.Reaction;
import io.github.nickm980.smallville.update.UpdateService;

public class ChatService implements Prompts {

    private final LLM chat;
    private final static Logger LOG = LoggerFactory.getLogger(UpdateService.class);
    private final World world;

    public ChatService(World world, LLM chat) {
	this.chat = chat;
	this.world = world;
    }

    @Override
    public int[] getWeights(Agent agent) {
	PromptRequest prompt = new PromptBuilder()
	    .withAgent(agent)
	    .setPrompt(SmallvilleConfig.getPrompts().getMisc().getRankMemories())
	    .build();

	String response = chat.sendChat(prompt, .1);
	response = response.replace(",]", "]");

	ObjectMapper objectMapper = new ObjectMapper();
	int[] result = new int[0];

	if (!response.contains("[")) {
	    result = new int[1];
	    result[0] = Integer.parseInt(response);
	    return result;
	}

	try {
	    result = objectMapper.readValue(response, int[].class);
	} catch (JsonProcessingException e) {
	    LOG.error("Failed to parse json for memory ranking. Continuing anyways...");
	}

	return result;
    }

    @Override
    public String ask(Agent agent, String question) {
	PromptRequest prompt = new PromptBuilder()
	    .withObservation(question.replace("?", ""))
	    .withQuestion(question)
	    .withLocations(world.getLocations())
	    .withAgent(agent)
	    .setPrompt(SmallvilleConfig.getPrompts().getAgent().getAskQuestion())
	    .build();

	return chat.sendChat(prompt, .5);
    }

    @Override
    public List<Plan> getPlans(Agent agent) {
	PromptRequest prompt = new PromptBuilder()
	    .withLocations(world.getLocations())
	    .withObservation(agent.getMemoryStream().getLastObservation().getDescription())
	    .withAgent(agent)
	    .withWorld(world)
	    .setPrompt(SmallvilleConfig.getPrompts().getPlans().getLongTerm())
	    .build();

	String response = chat.sendChat(prompt, .6);
	return List.of(new Plan(response.replace("\n", " "), LocalDateTime.now()));
    }

    @Override
    public List<Plan> getShortTermPlans(Agent agent) {
	PromptRequest prompt = new PromptBuilder()
	    .withLocations(world.getLocations())
	    .withObservation(agent.getMemoryStream().getLastObservation().getDescription())
	    .withAgent(agent)
	    .withWorld(world)
	    .setPrompt(SmallvilleConfig.getPrompts().getPlans().getShortTerm())
	    .build();

	String response = chat.sendChat(prompt, .7);

	return parsePlans(response);
    }

    @Override
    public CurrentActivity getCurrentActivity(Agent agent) {
	PromptRequest prompt = new PromptBuilder()
	    .withAgent(agent)
	    .withWorld(world)
	    .withLocations(world.getLocations())
	    .setPrompt(SmallvilleConfig.getPrompts().getPlans().getCurrent())
	    .build();

	String response = chat.sendChat(prompt, .5);

	LocalNLP nlp = new LocalNLP();
	CurrentActivity activity = Util.parseAsClass(response, CurrentActivity.class);
	LOG.info(activity.getActivity() + activity.getLocation());
	activity.setLastActivity(nlp.convertToPastTense(agent.getCurrentActivity()));

	return activity;
    }

    @Override
    public Conversation getConversationIfExists(Agent agent, Agent other, String topic) {
	PromptRequest prompt = new PromptBuilder()
	    .withAgent(agent)
	    .withOther(other)
	    .withObservation(topic)
	    .setPrompt(SmallvilleConfig.getPrompts().getReactions().getConversation())
	    .build();

	String response = chat.sendChat(prompt, .7);
	String[] lines = response.split("\\r?\\n");

	List<Dialog> dialogs = new ArrayList<>();
	for (String line : lines) {
	    String[] parts = line.split(":\\s+", 2);
	    if (parts.length == 2) { // ignores all lines before the conversation
		dialogs.add(new Dialog(parts[0], parts[1]));
	    }
	}

	Conversation conversation = new Conversation(agent.getFullName(), other.getFullName(), dialogs);
	return conversation;
    }

    @Override
    public List<Plan> parsePlans(String input) {
	List<Plan> plans = new ArrayList<>();

	String[] lines = input.split("\n");

	for (String line : lines) {
	    LocalDateTime start = null;

	    try {
		start = parseTime(input, line);
	    } catch (Exception e) {
		LOG.error("Could not parse time");
		continue;
	    }

	    if (start == null) {
		continue;
	    }

	    Plan plan = new Plan(line, start);
	    plans.add(plan);
	}

	return plans;
    }

    private LocalDateTime parseTime(String input, String line) throws DateTimeParseException {
	String[] splitPlan = line.split("\\d+", 2); // split after first number

	if (line.isBlank()) {
	    return null;
	}

	if (splitPlan.length == 1) {
	    LOG.warn("Temporal memory possibly missing a time. " + line);
	    return null;
	}

	int index = input.indexOf(splitPlan[1]) - 2;

	if (index == -1) {
	    index++;
	}

	String time = input.substring(index, index + 8).trim().toUpperCase();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

	return LocalDateTime.of(LocalDate.now(), LocalTime.parse(time, formatter));
    }

    @Override
    public ObjectChangeResponse[] getObjectsChangedBy(Agent agent) {
	if (agent.getCurrentActivity().equals(agent.getLastActivity())) {
	    return new ObjectChangeResponse[0];
	}

	PromptRequest tensesPrompt = new PromptBuilder()
	    .withAgent(agent)
	    .withWorld(world)
	    .setPrompt(SmallvilleConfig.getPrompts().getMisc().getCombineSentences())
	    .build(); // might be able to use LocalNLP for this

	String tenses = chat.sendChat(tensesPrompt, .1);

	PromptRequest changedPrompt = new PromptBuilder()
	    .withAgent(agent)
	    .withTense(tenses)
	    .withWorld(world)
	    .withLocations(world.getLocations())
	    .setPrompt(SmallvilleConfig.getPrompts().getWorld().getObjectStates())
	    .build();

	String response = chat.sendChat(changedPrompt, .3);

	String[] lines = response.split("\n");
	ObjectChangeResponse[] objects = new ObjectChangeResponse[lines.length];

	for (int i = 0; i < lines.length; i++) {
	    String line = lines[i];

	    if (line.isBlank()) {
		continue;
	    }

	    String[] parts = line.split(":");

	    if (parts.length < 2) {
		continue;
	    }

	    String item = parts[0].trim();
	    String value = parts[1].trim();

	    LOG.debug("Trying to change " + item + " to " + value);

	    if (item != null && value != null && !value.equalsIgnoreCase("Unchanged")) {
		objects[i] = new ObjectChangeResponse(item, value);
	    }
	}

	if (objects.length == 0) {
	    LOG.warn("No objects were updated");
	}

	return objects;
    }

    @Override
    public Reflection createReflectionFor(Agent agent) {
	Reflection reflection = new Reflection("");
	PromptRequest prompt = new PromptBuilder()
	    .withAgent(agent)
	    .setPrompt(SmallvilleConfig.getPrompts().getAgent().getReflectionQuestion())
	    .build();

	String query = chat.sendChat(prompt, .1);
	String[] lines = query.split("\n");
	query = query.split("\n")[lines.length - 1].substring(2);

	LOG.debug("[Reflections] Question: " + query);

	Set<Memory> filter = new HashSet<Memory>();
	filter.addAll(agent.getMemoryStream().getRelevantMemories(query.substring(2)));
	List<Memory> memories = new ArrayList<>(filter); // Convert the set back to a list

	LOG.debug(String.join(",", memories.stream().map(m -> m.getDescription()).collect(Collectors.toList())));

	PromptRequest secondPrompt = new PromptBuilder()
	    .withAgent(agent)
	    .withStatements(memories.stream().map(m -> m.getDescription()).collect(Collectors.toList()))
	    .setPrompt(SmallvilleConfig.getPrompts().getAgent().getReflectionResult())
	    .build();

	String description = chat.sendChat(secondPrompt, .8);

	// retrieve just the insight. remove the because clause and the key
	int index = description.lastIndexOf(":");

	if (index != -1) {
	    description = description.substring(index);
	}

	description = description.replaceAll(":", "").trim();

	reflection.setDescription(description);

	return reflection;
    }

    @Override
    public Reaction shouldUpdatePlans(Agent agent, String observation) {
	PromptRequest prompt = new PromptBuilder()
	    .withObservation(observation)
	    .withAgent(agent)
	    .setPrompt(SmallvilleConfig.getPrompts().getReactions().getReaction())
	    .build();

	String response = chat.sendChat(prompt, .2);
	Reaction result = Util.parseAsClass(response, Reaction.class);

	LOG.debug("reacting " + result.getAnswer());
	return result;
    }

    public String createTraits(Agent agent) {
	PromptRequest prompt = new PromptBuilder()
	    .withAgent(agent)
	    .setPrompt(SmallvilleConfig.getPrompts().getAgent().getCharacteristics())
	    .build();

	return chat.sendChat(prompt, .5);
    }

    @Override
    public Dialog saySomething(Agent agent, String observation) {
	PromptRequest request = new PromptBuilder()
	    .withObservation(observation)
	    .withAgent(agent)
	    .setPrompt(SmallvilleConfig.getPrompts().getReactions().getSay())
	    .build();
	
	String result = chat.sendChat(request, .5);
	
	return new Dialog(agent.getFullName(), result);
    }
}
