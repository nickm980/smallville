package io.github.nickm980.smallville;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import io.github.nickm980.smallville.llm.ChatGPT;
import io.github.nickm980.smallville.models.memory.MemoryStream;
import io.github.nickm980.smallville.models.memory.Plan;
import io.github.nickm980.smallville.update.ChatService;

public class PlansParsingTest {

    @Test
    public void testParsingPlans() {
	ChatService service = new ChatService(new World(), new ChatGPT());

	List<Plan> plans = service.parsePlans("""
		Walk to the farmhouse at 2:00 PM
		\nMeet with the farmer to discuss crops at 2:30 PM
		\nHelp with feeding the animals from 3:00 PM - 4:00 PM
		\nRead a book under the shade of a tree from 4:00 PM - 5:00 PM
		\nHave dinner at home at 6:00 PM.
		""");

	assertTrue(plans.size() == 5);
    }

    @Test
    public void testWithExtraNumbers() {
	ChatService service = new ChatService(new World(), new ChatGPT());

	List<Plan> plans = service.parsePlans("""
		. Walk to the farmhouse at 2:00 PM
		\n- Meet with the farmer to discuss crops at 2:30 PM
		\n- Help with feeding the animals from 3:00 PM - 4:00 PM
		\n- Read a book under the shade of a tree from 4:00 PM - 5:00 PM
		\n- Have dinner at home at 6:00 PM.
		""");

	System.out.println(plans.get(3).getTime());
	assertTrue(plans.size() == 5);
    }

    @Test
    public void testTime() {
	ChatService service = new ChatService(new World(), new ChatGPT());
	List<Plan> plans = service.parsePlans("""
		\nI will then go to the Green House and sleep from 12:05 AM.
		\nI will wake up and make breakfast from 11:30 PM - 9:30 AM.
		\nI will then go to the Forest and spend some time gathering branches from 10:00 AM - 11:00 AM.
				""");
	MemoryStream stream = new MemoryStream();

	stream.setPlans(plans);

	stream.prunePlans();
	System.out.println(stream.getPlans().size());
	assertTrue(stream.getPlans().size() == 1);
    }
}
