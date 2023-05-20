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

import io.github.nickm980.smallville.Util;
import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.llm.LLM;
import io.github.nickm980.smallville.models.ActionHistory;
import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.Conversation;
import io.github.nickm980.smallville.models.Dialog;
import io.github.nickm980.smallville.models.Location;
import io.github.nickm980.smallville.models.SimulatedLocation;
import io.github.nickm980.smallville.models.SimulatedObject;
import io.github.nickm980.smallville.models.memory.Plan;
import io.github.nickm980.smallville.prompts.Prompt;
import io.github.nickm980.smallville.prompts.PromptBuilder;
import io.github.nickm980.smallville.prompts.PromptService;
import io.github.nickm980.smallville.prompts.TimePhrase;
import io.github.nickm980.smallville.prompts.response.CurrentPlan;
import io.github.nickm980.smallville.prompts.response.ObjectChangeResponse;
import io.github.nickm980.smallville.prompts.response.Reaction;

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
	    .withLocations(world.getLocations())
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
	    .withLocations(world.getLocations())
	    .createAskQuestionPrompt(question)
	    .build();

	return chat.sendChat(prompt, .9);
    }

    public List<Plan> getPlans(Agent agent) {
	Prompt prompt = new PromptBuilder()
	    .withLocations(world.getLocations())
	    .withAgent(agent)
	    .createFuturePlansPrompt()
	    .build();

	String response = chat.sendChat(prompt, .7);

	return parsePlans(response);
    }

    public List<Plan> getShortTermPlans(Agent agent) {
	Prompt prompt = new PromptBuilder()
	    .withLocations(world.getLocations())
	    .withAgent(agent)
	    .createShortTermPlansPrompt()
	    .build();

	String response = chat.sendChat(prompt, .7);

	return parsePlans(response);
    }

    public CurrentPlan getCurrentPlan(Agent agent) {
	CurrentPlan result = new CurrentPlan();
	Prompt prompt = new PromptBuilder()
	    .withAgent(agent)
	    .withLocations(world.getLocations())
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
	result.setLocation(json.get("location").asText());

	LOG.info("[Activity]" + result.getCurrentActivity() + " location: " + agent.getLocation().getName());

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

    public List<Plan> parsePlans(String input) {
	List<Plan> plans = new ArrayList<>();

	String[] lines = input.split("\n");

	for (String line : lines) {
	    String[] splitPlan = line.split("\\d+", 2); // split after first number

	    if (splitPlan.length == 1) {
		continue;
	    }

	    int index = input.indexOf(splitPlan[1]) - 2;

	    if (index == -1) {
		LOG.warn("Skipping a plan");
		continue;
	    }

	    String time = input.substring(index, index + 8).trim();

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
	    LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.parse(time, formatter));

	    Plan plan = new Plan(line, start);
	    plans.add(plan);
	}

	return plans;
    }

    public ObjectChangeResponse[] getObjectsChangedBy(Agent agent) {
	Prompt tensesPrompt = new PromptBuilder().withAgent(agent).createPastAndPresent().build();
	String tenses = chat.sendChat(tensesPrompt, .1);

	Prompt changedPrompt = new PromptBuilder()
	    .withAgent(agent)
	    .withLocations(world.getLocations())
	    .createObjectUpdates(tenses)
	    .build();

	String response = chat.sendChat(changedPrompt, .3);

	String[] lines = response.split("\n");
	ObjectChangeResponse[] objects = new ObjectChangeResponse[lines.length];

	for (int i = 0; i < lines.length; i++) {
	    String line = lines[i];
	    String[] parts = line.split(": ");
	    String item = parts[0].trim();
	    String value = parts[1].trim();
	    objects[i] = new ObjectChangeResponse(item, value);
	}

	return objects;
    }

    public String getExactLocation(Agent agent) {
	Prompt prompt = new PromptBuilder().withAgent(agent).createExactLocation().build();
	return chat.sendChat(prompt, 0);
    }
}
