package io.github.nickm980.smallville.analytics;

import java.util.HashMap;
import java.util.Map;

import io.github.nickm980.smallville.events.Listen;
import io.github.nickm980.smallville.events.SmallvilleListener;
import io.github.nickm980.smallville.events.agent.AgentUpdateEvent;
import io.github.nickm980.smallville.events.llm.PromptReceievedEvent;

public class AnalyticsListener implements SmallvilleListener {

    private Analytics analytics;
    private Map<String, Integer> updateCount = new HashMap<String, Integer>();
    
    public AnalyticsListener(Analytics analytics) {
	this.analytics = analytics;
    }
    
    @Listen
    public void onUpdate(AgentUpdateEvent e) {	
	updateCount.compute(e.getAgent().getFullName(), (key, value) -> (value == null) ? 1 : value + 1);
	
	if (updateCount.get(e.getAgent().getFullName()) > 30) {
	    analytics.reset();
	}
	
	analytics.incrementVisits(e.getMoveTo().getFullPath());
    }
    
    @Listen
    public void onPromptReceieved(PromptReceievedEvent e) {	
	analytics.savePrompt(e.getPrompt(), e.getResult(), e.getResponseTime());
    }
}
