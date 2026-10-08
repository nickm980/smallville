package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.config.simulation.SimulationFile;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.memory.Plan;
import io.github.nickm980.smallville.prompts.PromptRequest;
import io.github.nickm980.smallville.prompts.PromptBuilder;
import io.github.nickm980.smallville.prompts.TemplateMapper;

public class PromptBuilderTest {
    static PromptBuilder builder;
    static World world;
    static Agent agent;

    @BeforeAll
    public static void setUp() {
	world = new World();
	Location location = new Location("location");

	world.create(location);

	agent = new Agent("name", List.of(new Characteristic("desc")), "test", location);
	agent.setCurrentActivity("Doing nothing");
	agent.setCurrentActivity("making dinner");
	agent
	    .getMemoryStream()
	    .addAll(List
		.of(new Plan("plan-5", LocalDateTime.now().minusMinutes(5)),
			new Plan("plan-10", LocalDateTime.now().minusMinutes(10)),
			new Plan("plan+now", LocalDateTime.now())));
	agent.getMemoryStream().add(new Characteristic("hello"));
	world.create(agent);
	builder = new PromptBuilder()
	    .withObservation("this is an observation")
	    .withAgent(agent)
	    .withStatements(List.of("2 to the moon2"))
	    .withWorld(world)
	    .withQuestion("hello there!")
	    .withLocations(world.getLocations())
	    .withTense("tenses");
    }

    @Test
    public void testPromptTemplates() {
	String input = SmallvilleConfig.getPrompts().getMisc().getDebug();
	PromptRequest prompt = builder.setPrompt(input).build();
	String result = prompt.build().get("content");

	assertEquals("pong", getKey(result, "ping"));
	assertEquals("hello there!", getKey(result, "question"));
	assertTrue(!getKey(result, "date.time").isEmpty());
	assertTrue(!getKey(result, "date.full").isEmpty());
	assertTrue(!getKey(result, "memories.relevant").isEmpty());
	assertTrue(!getKey(result, "memories.characteristics").isEmpty());
	assertTrue(!getKey(result, "memories.unranked").isEmpty());
	assertTrue(!getKey(result, "agent.plans").isEmpty());
	assertTrue(!getKey(result, "agent.locationChildren").isEmpty());
	assertTrue(!getKey(result, "agent.locationName").isEmpty());
	assertTrue(!getKey(result, "agent.description").isEmpty());
	assertEquals("Doing nothing", getKey(result, "agent.lastActivity"));
	assertTrue(getKey(result, "agent.memories").contains("desc"));
	assertEquals("name", getKey(result, "agent.name"));
    }

    @Test
    public void test_agent_summary_includes_working_memory_and_existing_fields() {
	agent.setTraits("steady, helpful, quiet");
	agent.getMemoryStream().addWorkingMemory("remember the tea tray");

	String summary = new TemplateMapper().buildAgentSummary(agent);

	assertTrue(summary.contains("Name: name"));
	assertTrue(summary.contains("Current Location: location"));
	assertTrue(summary.contains("Active Continuity: remember the tea tray;"));
	assertTrue(summary.contains("Preferred Model:"));
	assertTrue(summary.contains("Social Preference: balanced"));
    }

    @Test
    public void test_reaction_prompt_renders_active_continuity_relevant_memories_and_observation() {
	World localWorld = new World();
	Location greenhouse = new Location("Green House: Glass Table");
	localWorld.create(greenhouse);

	Agent localAgent = new Agent("Jamie", List.of(new Characteristic("grounded")), "reviewing notes", greenhouse);
	localAgent.setTraits("steady, helpful, quiet");
	localAgent.getMemoryStream().addWorkingMemory("remember the tea tray");
	localAgent.getMemoryStream().add(new Observation("Alex promised tea after pruning"));
	localWorld.create(localAgent);

	String prompt = new PromptBuilder()
	    .withObservation("Alex promised tea after pruning")
	    .withAgent(localAgent)
	    .withWorld(localWorld)
	    .setPrompt(SmallvilleConfig.getPrompts().getReactions().getReaction())
	    .build()
	    .build()
	    .get("content");

	assertTrue(prompt.contains("Active Continuity: remember the tea tray;"));
	assertTrue(prompt.contains("Relevant Memories: Alex promised tea after pruning"));
	assertTrue(prompt.contains("Observation: Alex promised tea after pruning"));
    }

    @Test
    public void test_long_term_prompt_renders_active_continuity_relevant_memories_schedule_context_and_observation() {
	World localWorld = new World();
	Location greenhouse = new Location("Green House: Glass Table");
	localWorld.create(greenhouse);

	Agent localAgent = new Agent("Jamie", List.of(new Characteristic("grounded")), "reviewing notes", greenhouse);
	localAgent.setTraits("steady, helpful, quiet");
	localAgent.getMemoryStream().addWorkingMemory("remember the tea tray");
	localAgent.getMemoryStream().add(new Observation("Alex promised tea after pruning"));
	localWorld.create(localAgent);

	SimulationFile.DailyRhythmSeed rhythm = new SimulationFile.DailyRhythmSeed();
	rhythm.setBreakfast("06:00-09:30");
	rhythm.setLunch("12:00-14:00");
	rhythm.setDinner("18:00-20:30");
	rhythm.setMorningTea("06:00-10:00");
	rhythm.setAfternoonTea("15:00-17:30");
	rhythm.setSnack("Flexible morning or afternoon");

	String prompt = new PromptBuilder()
	    .withObservation("Alex promised tea after pruning")
	    .withDailyRhythm(rhythm)
	    .withAgent(localAgent)
	    .withWorld(localWorld)
	    .setPrompt(SmallvilleConfig.getPrompts().getPlans().getLongTerm())
	    .build()
	    .build()
	    .get("content");

	assertTrue(prompt.contains("Active Continuity: remember the tea tray;"));
	assertTrue(prompt.contains("Relevant Memories: Alex promised tea after pruning"));
	assertTrue(prompt.contains("Daily Rhythm: breakfast 06:00-09:30, lunch 12:00-14:00, dinner 18:00-20:30"));
	assertTrue(prompt.contains("Observation ("));
	assertTrue(prompt.contains("Alex promised tea after pruning"));
    }

    @Test
    public void test_short_term_prompt_renders_active_continuity_relevant_memories_daily_requirement_and_observation() {
	World localWorld = new World();
	Location greenhouse = new Location("Green House: Glass Table");
	localWorld.create(greenhouse);

	Agent localAgent = new Agent("Jamie", List.of(new Characteristic("grounded")), "reviewing notes", greenhouse);
	localAgent.setTraits("steady, helpful, quiet");
	localAgent.getMemoryStream().addWorkingMemory("remember the tea tray");
	localAgent.getMemoryStream().add(new Observation("Alex promised tea after pruning"));
	localAgent.getMemoryStream().add(new Plan("review the tray map", LocalDateTime.now().plusMinutes(10)));
	localWorld.create(localAgent);

	SimulationFile.DailyRhythmSeed rhythm = new SimulationFile.DailyRhythmSeed();
	rhythm.setBreakfast("06:00-09:30");
	rhythm.setLunch("12:00-14:00");
	rhythm.setDinner("18:00-20:30");
	rhythm.setMorningTea("06:00-10:00");
	rhythm.setAfternoonTea("15:00-17:30");
	rhythm.setSnack("Flexible morning or afternoon");

	String prompt = new PromptBuilder()
	    .withObservation("Alex promised tea after pruning")
	    .withDailyRhythm(rhythm)
	    .withAgent(localAgent)
	    .withWorld(localWorld)
	    .setPrompt(SmallvilleConfig.getPrompts().getPlans().getShortTerm())
	    .build()
	    .build()
	    .get("content");

	assertTrue(prompt.contains("Active Continuity: remember the tea tray;"));
	assertTrue(prompt.contains("Daily Requirement: review the tray map;"));
	assertTrue(prompt.contains("Relevant Memories: Alex promised tea after pruning"));
	assertTrue(prompt.contains("Observation: Alex promised tea after pruning"));
	assertTrue(prompt.contains("Daily Rhythm: breakfast 06:00-09:30, lunch 12:00-14:00, dinner 18:00-20:30"));
    }

    private String getKey(String s, String key) {
	String result = "";

	for (String line : s.split("\n")) {
	    if (line.contains(key)) {
		result = line.split(key)[1];
		System.out.println(key + " : " + s);
	    }
	}
	
	return result.replace(":", "").trim();
    }
}
