package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.memory.Plan;
import io.github.nickm980.smallville.prompts.PromptRequest;
import io.github.nickm980.smallville.prompts.PromptBuilder;

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
	System.out.println(result);
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
    public void testLongTermPlans() {
	String input = SmallvilleConfig.getPrompts().getPlans().getLongTerm();
	PromptRequest prompt = builder.setPrompt(input).build();

	assertTrue(prompt.build().get("content").contains("[...]"));
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
