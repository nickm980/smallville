package io.github.nickm980.smallville.analytics;

import java.time.LocalDateTime;

public class PromptData {
    private final String prompt;
    private final String result;
    private final long responseTime;
    private final LocalDateTime createdAt;

    public PromptData(String prompt, String result, long responseTime) {
	this.prompt = prompt;
	this.result = result;
	this.responseTime = responseTime;
	this.createdAt = LocalDateTime.now();
    }

    public String getPrompt() {
        return prompt;
    }

    public String getResult() {
        return result;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }
}