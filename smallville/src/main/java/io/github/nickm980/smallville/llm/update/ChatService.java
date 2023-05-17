package io.github.nickm980.smallville.llm.update;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.llm.Prompt;
import io.github.nickm980.smallville.llm.PromptBuilder;
import io.github.nickm980.smallville.llm.PromptService;
import io.github.nickm980.smallville.llm.TimePhrase;
import io.github.nickm980.smallville.llm.api.LLM;
import io.github.nickm980.smallville.llm.response.CurrentPlan;
import io.github.nickm980.smallville.llm.response.Reaction;
import io.github.nickm980.smallville.models.ActionHistory;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.Conversation;
import io.github.nickm980.smallville.models.Dialog;
import io.github.nickm980.smallville.models.SimulatedLocation;
import io.github.nickm980.smallville.models.SimulatedObject;
import io.github.nickm980.smallville.models.memory.Plan;

//TODO: rename this class to a better name. Its hard to tell what it does
public class ChatService {

    private final LLM chat;
    private final static Logger LOG = LoggerFactory.getLogger(PromptService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final World world;

    public ChatService(World world, LLM chat) {
	this.chat = chat;
	this.world = world;
    }

    public int[] getWeights(Agent agent) {
	Prompt prompt = new PromptBuilder().withAgent(agent).createMemoryRankPrompt().build();

	String response = chat.sendChat(prompt, .1);

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

    public Reaction getReaction(Agent agent, String observation) {
	Prompt prompt = new PromptBuilder()
	    .withAgent(agent)
	    .withLocations(world.getObjects())
	    .createReactionSuggestion(observation)
	    .build();

	String response = chat.sendChat(prompt, 1);
	ObjectMapper mapper = new ObjectMapper();
	Reaction reaction = new Reaction();

	try {
	    JsonNode json = mapper.readTree(response);
	    boolean willReact = json.get("react").asBoolean();

	    if (willReact) {
		String currentActivity = json.get("reaction").asText();
		String emoji = json.get("emoji").asText();
		String location = json.get("location").asText();

		reaction.setLocation(location);
		reaction.setEmoji(emoji);
		reaction.setCurrentActivity(currentActivity);
	    }

	    reaction.setReact(willReact);
	} catch (JsonProcessingException e) {
	    LOG.error("Failed to parse json for memory ranking. Continuing anyways...");
	}

	return reaction;
    }

    public String ask(Agent agent, String question) {
	Prompt prompt = new PromptBuilder()
	    .withAgent(agent)
	    .withLocations(world.getObjects())
	    .createAskQuestionPrompt(question)
	    .build();

	return chat.sendChat(prompt, .2);
    }

    public List<Plan> getPlans(Agent agent, TimePhrase phrase) {
	Prompt prompt = new PromptBuilder()
	    .withLocations(world.getObjects())
	    .withAgent(agent)
	    .createFuturePlansPrompt(phrase)
	    .build();

	String response = chat.sendChat(prompt, .7);

	return parsePlans(response);
    }

    public CurrentPlan getCurrentPlan(Agent agent) {
	CurrentPlan result = new CurrentPlan();
	Prompt prompt = new PromptBuilder()
	    .withAgent(agent)
	    .withLocations(world.getObjects())
	    .createCurrentPlanPrompt()
	    .build();

	String response = chat.sendChat(prompt, .7);// higher value provides better results for emojis
	response = response.substring(response.indexOf("{"));

	JsonNode json = null;

	try {
	    json = objectMapper.readTree(response);
	} catch (JsonProcessingException e) {
	    LOG.error("Returning empty current plan because could not parse the result");
	    return result;
	}

	result.setCurrentActivity(json.get("activity").asText());
	result.setEmoji(json.get("emoji").asText());
	result.setLastActivity(json.get("last_activity").asText());

	LOG.info(agent.getFullName() + ": " + result.getCurrentActivity() + " emoji: " + result.getEmoji());

	return result;
    }

    public Conversation getConversationIfExists(Agent agent, Agent other) {
	Prompt prompt = new PromptBuilder().withAgent(agent).createConversationWith(other).build();

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

    public String combinePastAndPresentActivities(ActionHistory activity) {
	Prompt prompt = new PromptBuilder().createPastAndPresent().build();

	return chat.sendChat(prompt, .5);
    }

    private static List<Plan> parsePlans(String input) {
	List<Plan> plans = new ArrayList<>();

	Pattern pattern = Pattern.compile("([0-9]+)\\. (.+) from ([0-9]+:[0-9]+ [AP]M) to ([0-9]+:[0-9]+ [AP]M)");
	Matcher matcher = pattern.matcher(input);

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

	while (matcher.find()) {
	    LOG.info("matched");
	    String description = matcher.group(2);
	    LocalTime startTime = LocalTime.parse(matcher.group(3), formatter);
	    LocalTime endTime = LocalTime.parse(matcher.group(4), formatter);
	    Duration duration = Duration.between(startTime, endTime);
	    plans.add(new Plan(description, LocalDateTime.of(LocalDate.now(), startTime), duration));
	}

	return plans;
    }

    public SimulatedObject[] getObjectsChangedBy(Agent agent) {
	Prompt tensesPrompt = new PromptBuilder().withAgent(agent).createPastAndPresent().build();
	String tenses = chat.sendChat(tensesPrompt, .1);

	Prompt changedPrompt = new PromptBuilder()
	    .withAgent(agent)
	    .withLocations(agent.getLocation().getObjects())
	    .createObjectUpdates(tenses)
	    .build();

	String response = chat.sendChat(changedPrompt, .3);
	LOG.info(response);
	return new SimulatedObject[0];
    }
}
