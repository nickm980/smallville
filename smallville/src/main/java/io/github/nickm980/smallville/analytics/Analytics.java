package io.github.nickm980.smallville.analytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Analytics {

    private List<PromptData> promptData = new ArrayList<PromptData>();
    private Map<String, Integer> locationVisits = new HashMap<String, Integer>();

    public void savePrompt(String prompt, String result, long responseTime) {
	if (promptData.size() > 50) {
	    promptData.clear();
	}
	promptData.add(new PromptData(prompt, result, responseTime));
    }

    public List<PromptData> getPromptHistory() {
	return promptData;
    }

    public List<Map<String, ?>> getVisits() {
	List<Map<String, ?>> result = new ArrayList<Map<String, ?>>();
	
	for (String name : locationVisits.keySet()) {
	    result.add(Map.of("name", name, "value", locationVisits.get(name)));
	}

	return result;
    }

    public void incrementVisits(String locationName) {
	locationVisits.compute(locationName, (key, value) -> (value == null) ? 1 : value + 1);
    }

    public void reset() {
	promptData.clear();
	locationVisits.clear();
    }
}
