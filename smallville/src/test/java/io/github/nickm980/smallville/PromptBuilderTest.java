package io.github.nickm980.smallville;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import io.github.nickm980.smallville.entities.*;
import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.entities.memory.Characteristic;
import io.github.nickm980.smallville.entities.memory.Plan;
import io.github.nickm980.smallville.prompts.Prompt;
import io.github.nickm980.smallville.prompts.PromptBuilder;

public class PromptBuilderTest {

    @Test
    public void testPromptTemplates() {
	String prompt = SmallvilleConfig.getPrompts().getMisc().getDebug();
	World world = new World();
	SimulatedLocation location = new SimulatedLocation("location");
	world.save(location);
	Agent ageng = new Agent("name", List.of(new Characteristic("desc")), prompt, new AgentLocation(location));
	world.save(ageng);

	Prompt builder = new PromptBuilder()
	    .withAgent(ageng)
	    .withLocations(world.getLocations())
	    .setPrompt(prompt)
	    .build();

	assertEquals("Ping pong", builder.build().get("content").trim());
    }

    @Test
    public void testLongTermPlansPrompt() {
	String prompt = SmallvilleConfig.getPrompts().getPlans().getShortTerm();
	World world = new World();
	SimulatedLocation location = new SimulatedLocation("location");
	SimulatedObject obj = new SimulatedObject("obj", new ObjectState("off", List.of()), location);
	
	world.save(obj);
	world.save(location);
	
	Agent agent = new Agent("name", List.of(new Characteristic("desc")), prompt, new AgentLocation(location));
	agent.setGoal("Run for president");
	agent.setCurrentActivity("Doing nothing");
	agent.setCurrentActivity("making dinner");
	agent.addPlans(List.of(new Plan("plan", Timekeeper.getSimulationTime())));
	
	world.save(agent);

	Prompt builder = new PromptBuilder()
	    .withObservation("walked to the moon")
	    .withAgent(agent)
	    .withWorld(world)
	    .withQuestion("hello there!")
	    .withLocations(world.getLocations())
	    .withTense("tenses")
	    .setPrompt(prompt)
	    .build();

	System.out.println(builder.build().get("content"));
    }
}
