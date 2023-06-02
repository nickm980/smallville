package io.github.nickm980.smallville;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.AgentLocation;
import io.github.nickm980.smallville.entities.ObjectState;
import io.github.nickm980.smallville.entities.SimulatedLocation;
import io.github.nickm980.smallville.entities.SimulatedObject;
import io.github.nickm980.smallville.entities.memory.Characteristic;
import io.github.nickm980.smallville.entities.memory.Plan;
import io.github.nickm980.smallville.prompts.Prompt;
import io.github.nickm980.smallville.prompts.PromptBuilder;

public class PromptBuilderTest {

    @Test
    public void testPromptTemplates() {
	String prompt = SmallvilleConfig.getPrompts().getPlans().getCurrent();
	World world = new World();
	SimulatedLocation location = new SimulatedLocation("location");
	world.create(location);
	Agent ageng = new Agent("name", List.of(new Characteristic("desc")), prompt, new AgentLocation(location));
	world.create(ageng);

	Prompt builder = new PromptBuilder()
	    .withWorld(world)
	    .withAgent(ageng)
	    .withLocations(world.getLocations())
	    .setPrompt(prompt)
	    .build();

	assertEquals("Ping pong", builder.build().get("content").trim());
    }

    @Test
    public void testLongTermPlansPrompt() {
	String prompt = SmallvilleConfig.getPrompts().getAgent().getReflectionResult();
	World world = new World();
	SimulatedLocation location = new SimulatedLocation("location");
	SimulatedObject obj = new SimulatedObject("obj", new ObjectState("off", List.of()), location);

	world.create(obj);
	world.create(location);

	Agent ageng = new Agent("name", List.of(new Characteristic("desc")), prompt, new AgentLocation(location));
	ageng.setGoal("Run for president");
	ageng.setCurrentActivity("Doing nothing");
	ageng.setCurrentActivity("making dinner");
	ageng
	    .getMemoryStream()
	    .addAll(List
		.of(new Plan("plan", LocalDateTime.now().plusMinutes(5)), new Plan("plan2", LocalDateTime.now())));
	ageng.getMemoryStream().add(new Characteristic("hello"));
	world.create(ageng);
	
	Prompt builder = new PromptBuilder()
	    .withAgent(ageng)
	    .withStatements(List.of("2 to the moon2"))
	    .withWorld(world)
	    .withQuestion("hello there!")
	    .withLocations(world.getLocations())
	    .withTense("tenses")
	    .setPrompt(prompt)
	    .build();

	System.out.println(builder.build().get("content"));
    }
}
