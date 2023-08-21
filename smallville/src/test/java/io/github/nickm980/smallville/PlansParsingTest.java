package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.llm.ChatGPT;
import io.github.nickm980.smallville.memory.Plan;
import io.github.nickm980.smallville.prompts.ChatService;

public class PlansParsingTest {

    @Test
    public void test_plans_parse_with_times_at_beginning_and_end() {
	ChatService service = new ChatService(new World(), new ChatGPT());

	List<Plan> plans = service.parsePlans("""
		2:01 am at Red House: Bedroom, sleeping
		\n  2:30 PM Meet with the farmer to discuss crops at
		\nHelp with feeding the animals from 3:00 PM - 4:00 PM
		\nRead a book under the shade of a tree from 4:00 PM - 5:00 PM
		\n2:20 am at Red House: Bedroom, still sleeping
		""");

	assertTrue(plans.size() == 5);
    }

    @Test
    public void test_plans_parse_with_time_at_end() {
	ChatService service = new ChatService(new World(), new ChatGPT());

	List<Plan> plans = service.parsePlans("""
		. Walk to the farmhouse at 2:00 PM
		\n- Meet with the farmer to discuss crops at 2:30 PM
		\n- Help with feeding the animals from 3:00 PM - 4:00 PM
		\n- Read a book under the shade of a tree from 4:00 PM - 5:00 PM
		\n- Have dinner at home at 6:00 PM.
		""");

	assertTrue(plans.size() == 5);
    }
}
