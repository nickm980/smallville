package io.github.nickm980.smallville;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.analytics.Analytics;
import io.github.nickm980.smallville.analytics.AnalyticsListener;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.events.EventBus;
import io.github.nickm980.smallville.events.agent.AgentUpdateEvent;
import io.github.nickm980.smallville.events.llm.PromptReceievedEvent;

public class EventBusTest {

    @Test
    public void testEventBus() {
	EventBus eventBus = EventBus.getEventBus();

	Analytics analytics = new Analytics();
	eventBus.registerListener(new AnalyticsListener(analytics));

	AgentUpdateEvent event = new AgentUpdateEvent(new Agent("name", List.of(), null, null), null, null);
	eventBus.postEvent(event);

	PromptReceievedEvent event2 = new PromptReceievedEvent(null, null, 0);
	eventBus.postEvent(event2);
	eventBus.postEvent( new PromptReceievedEvent(null, null, 0));

	assertEquals(true, true);
    }
}
