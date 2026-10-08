package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.llm.ChatGPT;
import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.update.UpdateService;

public class UpdateServiceTest {

    @Test
    public void test_start_ambient_conversation_adds_dialog_to_both_agents() {
	World world = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	world.create(kitchen);

	Agent alex = new Agent(
	    "Alex",
	    List.of(new Characteristic("curious"), new Characteristic("artistic")),
	    "checking seedlings",
	    kitchen);
	alex.setTraits("curious, artistic, patient");
	world.create(alex);

	Agent jamie = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "bringing tea",
	    kitchen);
	jamie.setTraits("grounded, hospitable, observant");
	world.create(jamie);

	ChatGPT llm = Mockito.mock(ChatGPT.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn(
	    "Alex: The seedlings look stronger this morning.\n"
		+ "Jamie: Good, I brought tea before we check the next tray.\n"
		+ "Alex: Let us start with the glass-table notes.\n"
		+ "Jamie: That sounds like a calm plan.");

	UpdateService service = new UpdateService(llm, world);

	boolean started = service.startAmbientConversation(alex, jamie, "They meet in the kitchen to talk about seedlings.");

	assertTrue(started);
	assertEquals(1, world.getConversationsAfter(null).size());
	assertEquals(4, alex.getMemoryStream().getObservations().size());
	assertEquals(4, jamie.getMemoryStream().getObservations().size());
	assertEquals(1, alex.getMemoryStream().getWorkingMemories().size());
	assertEquals(1, jamie.getMemoryStream().getWorkingMemories().size());
	assertEquals("That sounds like a calm plan.", alex.getMemoryStream().getWorkingMemories().get(0).getDescription());
	assertEquals("That sounds like a calm plan.", jamie.getMemoryStream().getWorkingMemories().get(0).getDescription());
    }
}
