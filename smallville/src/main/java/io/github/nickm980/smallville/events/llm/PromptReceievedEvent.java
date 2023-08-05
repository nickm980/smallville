package io.github.nickm980.smallville.events.llm;

import io.github.nickm980.smallville.events.SmallvilleEvent;

public class PromptReceievedEvent extends SmallvilleEvent {

    private String prompt;
    private String result;
    private long responseTime;
    
    public PromptReceievedEvent(String prompt, String result, long responseTime) {
	this.prompt = prompt;
	this.result = result;
	this.responseTime = responseTime;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    
    public long getResponseTime() {
	return responseTime;
    }
}
