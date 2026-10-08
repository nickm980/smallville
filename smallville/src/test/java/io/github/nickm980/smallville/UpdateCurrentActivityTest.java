package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.prompts.Prompts;
import io.github.nickm980.smallville.prompts.dto.CurrentActivity;
import io.github.nickm980.smallville.update.UpdateCurrentActivity;
import io.github.nickm980.smallville.update.UpdateInfo;

public class UpdateCurrentActivityTest {

    @Test
    public void test_update_current_activity_resolves_location_notes_without_throwing() {
	World world = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	Location glassTable = new Location("Green House: Glass Table");
	world.create(kitchen);
	world.create(glassTable);

	Agent agent = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "brewing tea",
	    kitchen);

	Prompts prompts = Mockito.mock(Prompts.class);
	CurrentActivity currentActivity = new CurrentActivity();
	currentActivity.setActivity("bringing tea to Alex");
	currentActivity.setLocation("Green House: Glass Table (by the sunny window)");
	currentActivity.setEmoji("tea");
	currentActivity.setLastActivity("brewed tea");

	Mockito.when(prompts.getCurrentActivity(agent)).thenReturn(currentActivity);

	new UpdateCurrentActivity().update(prompts, world, agent, new UpdateInfo());

	assertEquals("Green House: Glass Table", agent.getLocation().getFullPath());
	assertEquals("bringing tea to Alex", agent.getCurrentActivity());
	assertEquals("tea", agent.getEmoji());
	assertEquals(1, agent.getMemoryStream().getObservations().size());
	assertEquals("brewed tea", agent.getMemoryStream().getObservations().get(0).getDescription());
    }
}
