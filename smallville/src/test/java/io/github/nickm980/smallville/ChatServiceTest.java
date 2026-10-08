package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.llm.LLM;
import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.memory.MemoryStream;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.memory.Reflection;
import io.github.nickm980.smallville.prompts.ChatService;
import io.github.nickm980.smallville.prompts.PromptRequest;
import io.github.nickm980.smallville.prompts.dto.CurrentActivity;
import io.github.nickm980.smallville.update.UpdateService;

public class ChatServiceTest {

    @Test
    public void test_get_current_activity_logs_activity_and_location_with_separator() {
	World world = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	world.create(kitchen);

	Agent agent = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "brewing tea",
	    kitchen);

	LLM llm = Mockito.mock(LLM.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn(
	    "Activity: bringing tea to Alex\n"
		+ "Location: Green House: Glass Table\n"
		+ "Emoji: tea");

	ChatService service = new ChatService(world, llm);
	Logger logger = Logger.getLogger(UpdateService.class);
	CapturingAppender appender = new CapturingAppender();
	logger.addAppender(appender);

	try {
	    CurrentActivity result = service.getCurrentActivity(agent);

	    assertEquals("bringing tea to Alex", result.getActivity());
	    assertEquals("Green House: Glass Table", result.getLocation());
	    assertEquals("tea", result.getEmoji());
	    assertTrue(appender.messages.stream().anyMatch(message -> message.equals("bringing tea to Alex Green House: Glass Table")));
	    assertFalse(appender.messages.stream().anyMatch(message -> message.contains("bringing tea to AlexGreen House: Glass Table")));
	} finally {
	    logger.removeAppender(appender);
	}
    }

    @Test
    public void test_get_current_activity_strips_exact_duplicate_location_suffix() {
	World world = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	world.create(kitchen);

	Agent agent = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "checking on seedlings",
	    kitchen);

	LLM llm = Mockito.mock(LLM.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn(
	    "Activity: checking on seedlings at Blue House: Kitchen\n"
		+ "Location: Blue House: Kitchen\n"
		+ "Emoji: sprout");

	ChatService service = new ChatService(world, llm);

	CurrentActivity result = service.getCurrentActivity(agent);

	assertEquals("checking on seedlings", result.getActivity());
	assertEquals("Blue House: Kitchen", result.getLocation());
	assertEquals("sprout", result.getEmoji());
    }

    @Test
    public void test_get_current_activity_preserves_non_duplicate_location_wording() {
	World world = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	world.create(kitchen);

	Agent agent = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "checking on seedlings",
	    kitchen);

	LLM llm = Mockito.mock(LLM.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn(
	    "Activity: checking on seedlings beside Blue House: Kitchen\n"
		+ "Location: Blue House: Kitchen\n"
		+ "Emoji: sprout");

	ChatService service = new ChatService(world, llm);

	CurrentActivity result = service.getCurrentActivity(agent);

	assertEquals("checking on seedlings beside Blue House: Kitchen", result.getActivity());
	assertEquals("Blue House: Kitchen", result.getLocation());
	assertEquals("sprout", result.getEmoji());
    }

    @Test
    public void test_create_reflection_for_uses_once_trimmed_question_for_relevant_memories() throws Exception {
	World world = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	world.create(kitchen);

	Agent agent = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "brewing tea",
	    kitchen);

	MemoryStream memories = Mockito.spy(new MemoryStream());
	Field field = Agent.class.getDeclaredField("memories");
	field.setAccessible(true);
	field.set(agent, memories);

	Mockito.doReturn(List.of(new Observation("Alex and Jamie are active around that time")))
	    .when(memories)
	    .getRelevantMemories(ArgumentMatchers.anyString());

	LLM llm = Mockito.mock(LLM.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn(
	    "- What should Jamie tend next?\n"
		+ "- Who needs tea next?\n"
		+ "- At 8:50 am) and seem to be active around that time",
	    "Insight: Jamie notices a shared morning rhythm");

	ChatService service = new ChatService(world, llm);

	Reflection reflection = service.createReflectionFor(agent);

	Mockito.verify(memories).getRelevantMemories("At 8:50 am) and seem to be active around that time");
	Mockito.verify(memories, Mockito.never()).getRelevantMemories(" 8:50 am) and seem to be active around that time");
	assertEquals("Jamie notices a shared morning rhythm", reflection.getDescription());
    }

    @Test
    public void test_create_world_proposal_prompt_includes_valid_parent_guidance_and_reason_requirement() {
	World world = new World();
	Location blueHouse = new Location("Blue House");
	Location study = new Location("Blue House: Study");
	Location greenHouse = new Location("Green House");
	Location glassTable = new Location("Green House: Glass Table");
	Location garden = new Location("Garden");
	Location bench = new Location("Garden: Bench");
	world.create(blueHouse);
	world.create(study);
	world.create(greenHouse);
	world.create(glassTable);
	world.create(garden);
	world.create(bench);

	Agent agent = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "reviewing tray notes",
	    study);
	agent.setGoals(List.of("Keep the house and garden calm and ready for work."));
	agent.setRituals(List.of("Start with tea and a practical status check."));

	LLM llm = Mockito.mock(LLM.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn("Answer: No");

	ChatService service = new ChatService(world, llm);
	service.createWorldProposal(agent);

	ArgumentCaptor<PromptRequest> promptCaptor = ArgumentCaptor.forClass(PromptRequest.class);
	Mockito.verify(llm).sendChat(promptCaptor.capture(), Mockito.eq(.2));
	String content = promptCaptor.getValue().getContent();

	assertTrue(content.contains("Valid add_location parents in this world:"));
	assertTrue(content.contains("Blue House"));
	assertTrue(content.contains("Blue House: Study"));
	assertTrue(content.contains("Green House"));
	assertTrue(content.contains("Do not use these object-like leaf parents for add_location:"));
	assertTrue(content.contains("Green House: Glass Table"));
	assertTrue(content.contains("Garden: Bench"));
	assertTrue(content.contains("prefer add_object instead of add_location, or answer No."));
	assertTrue(content.contains("If any required proposal line would be blank, Answer must be No."));
	assertTrue(content.contains("Reason must be one short non-empty sentence tied to the current activity, goals, rituals, or world-building frame."));
	assertTrue(content.contains("If you cannot supply a grounded non-empty Reason, Answer must be No."));
	assertTrue(content.contains("Example valid Yes response:"));
	assertTrue(content.contains("Answer: Yes"));
	assertTrue(content.contains("ParentLocation: Garden: South Bed"));
	assertTrue(content.contains("Reason: to create a dedicated space for transplanting and bed tidy work during afternoon tea breaks."));
    }

    @Test
    public void test_parse_plans_merges_split_entries_and_drops_wrapper_lines() {
	World world = new World();
	LLM llm = Mockito.mock(LLM.class);
	ChatService service = new ChatService(world, llm);
	Logger logger = Logger.getLogger(UpdateService.class);
	CapturingAppender appender = new CapturingAppender();
	logger.addAppender(appender);

	try {
	    List<io.github.nickm980.smallville.memory.Plan> plans = service.parsePlans(
		"Here is Alex's plan for today:\n\n"
		    + "7:30 am at Green House: Glass Table\n"
		    + "Plan sheets and tray maps spread beside a mug ring\n\n"
		    + "8:00 am at Blue House: Kitchen\n"
		    + "Tea things laid out beside a garden notebook");

	    assertEquals(2, plans.size());
	    assertEquals("7:30 am at Green House: Glass Table, Plan sheets and tray maps spread beside a mug ring", plans.get(0).getDescription());
	    assertEquals("8:00 am at Blue House: Kitchen, Tea things laid out beside a garden notebook", plans.get(1).getDescription());
	    assertFalse(appender.messages.stream().anyMatch(message -> message.contains("Temporal memory possibly missing a time")));
	} finally {
	    logger.removeAppender(appender);
	}
    }

    @Test
    public void test_parse_plans_keeps_standalone_untimed_fragments_as_warnings() {
	World world = new World();
	LLM llm = Mockito.mock(LLM.class);
	ChatService service = new ChatService(world, llm);
	Logger logger = Logger.getLogger(UpdateService.class);
	CapturingAppender appender = new CapturingAppender();
	logger.addAppender(appender);

	try {
	    List<io.github.nickm980.smallville.memory.Plan> plans = service.parsePlans(
		"12:00 am at Blue House: Kitchen, organize the tea tray\n"
		    + "Remember to stay calm");

	    assertEquals(1, plans.size());
	    assertEquals("12:00 am at Blue House: Kitchen, organize the tea tray", plans.get(0).getDescription());
	    assertTrue(appender.messages.stream().anyMatch(message -> message.contains("Temporal memory possibly missing a time. Remember to stay calm")));
	} finally {
	    logger.removeAppender(appender);
	}
    }

    @Test
    public void test_get_plans_prompt_discourages_headers_and_split_lines() {
	World world = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	world.create(kitchen);

	Agent agent = new Agent(
	    "Alex",
	    List.of(new Characteristic("curious"), new Characteristic("patient")),
	    "reviewing notes",
	    kitchen);

	LLM llm = Mockito.mock(LLM.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn("8:00 am at Blue House: Kitchen, organize the tea tray");

	ChatService service = new ChatService(world, llm);
	service.getPlans(agent);

	ArgumentCaptor<PromptRequest> promptCaptor = ArgumentCaptor.forClass(PromptRequest.class);
	Mockito.verify(llm).sendChat(promptCaptor.capture(), Mockito.eq(.4));
	String content = promptCaptor.getValue().getContent();

	assertTrue(content.contains("Do not add a header or intro sentence"));
	assertTrue(content.contains("Do not split a single plan entry across multiple lines"));
	assertTrue(content.contains("Every plan line must contain the time, exact location, and action on the same line"));
	assertTrue(content.contains("Never start with assistant narration such as \"I'd be happy to help\" or \"Here is the plan\""));
	assertTrue(content.contains("Return Alex's full-day plan now."));
    }

    @Test
    public void test_get_short_term_plans_prompt_discourages_headers_and_split_lines() {
	World world = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	world.create(kitchen);

	Agent agent = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("observant")),
	    "setting out tea",
	    kitchen);

	LLM llm = Mockito.mock(LLM.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn("12:00 am at Blue House: Kitchen, have a glass of water");

	ChatService service = new ChatService(world, llm);
	service.getShortTermPlans(agent);

	ArgumentCaptor<PromptRequest> promptCaptor = ArgumentCaptor.forClass(PromptRequest.class);
	Mockito.verify(llm).sendChat(promptCaptor.capture(), Mockito.eq(.4));
	String content = promptCaptor.getValue().getContent();

	assertTrue(content.contains("Return only final plan lines with no bullets, numbering, commentary, intro sentence, heading, or helper text."));
	assertTrue(content.contains("Every plan line must contain the time, exact location, and action on the same line."));
	assertTrue(content.contains("Never put the location on one line and the activity on the next line."));
	assertTrue(content.contains("Never start with assistant narration such as \"I'd be happy to help\" or \"Here is the plan\"."));
	assertTrue(content.contains("Example format: 12:00 am at Blue House: Kitchen, have a glass of water"));
    }

    private static class CapturingAppender extends AppenderSkeleton {
	private final List<String> messages = new ArrayList<String>();

	@Override
	protected void append(LoggingEvent event) {
	    messages.add(String.valueOf(event.getRenderedMessage()));
	}

	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
	    return false;
	}
    }
}
