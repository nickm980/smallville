package io.github.nickm980.smallville;

import org.junit.Test;

import io.github.nickm980.smallville.llm.api.ChatGPT;
import io.github.nickm980.smallville.llm.update.ChatService;

public class Parsing {

    @Test
    public void testParsingPlans() {
	ChatService service = new ChatService(new World(), new ChatGPT());

	service.parsePlans("""
		Walk to the farmhouse at 2:00 PM
		\nMeet with the farmer to discuss crops at 2:30 PM
		\nHelp with feeding the animals from 3:00 PM - 4:00 PM
		\nRead a book under the shade of a tree from 4:00 PM - 5:00 PM
		\nHave dinner at home at 6:00 PM.
		""");
	
	
    }
}
