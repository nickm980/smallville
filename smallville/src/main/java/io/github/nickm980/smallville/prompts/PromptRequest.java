package io.github.nickm980.smallville.prompts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PromptRequest {
    private String content;
    private String assistant;
    private String function;

    public PromptRequest(String content) {
	this.content = content;
    }

    abstract String getRole();

    public void setAssistant(String assistant) {
	this.assistant = assistant;
    }

    public boolean isFunctional() {
	return function != null && !function.isEmpty();
    }

    public void setFunction(String function) {
	this.function = function;
    }
   
    public String getFunction() {
	return function;
    }

    public String getContent() {
	return content;
    }

    public Map<String, String> build() {
	Map<String, String> map = new HashMap<>();
	map.put("role", getRole());
	map.put("content", content);

	return map;
    }

    public static class User extends PromptRequest {
	public User(String content) {
	    super(content);
	}

	@Override
	String getRole() {
	    return "user";
	}
    }

    public static class System extends PromptRequest {
	public System(String content) {
	    super(content);
	}

	@Override
	String getRole() {
	    return "system";
	}
    }
}
