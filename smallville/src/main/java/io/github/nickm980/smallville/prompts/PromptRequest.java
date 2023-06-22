package io.github.nickm980.smallville.prompts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Prompt {
    private String content;
    private String assistant;
    private boolean isFunctional;

    public Prompt(String content) {
	this.content = content;
    }

    abstract String getRole();

    public void setAssistant(String assistant) {
	this.assistant = assistant;
    }

    public void setFunction(boolean functional) {
	this.isFunctional = functional;
    }

    public boolean isFunctional() {
	return isFunctional;
    }

    public String getFunction() {
	return "auto";
    }

    public String getContent() {
	return content;
    }

    public Map<String, String> build() {
	Map<String, String> a = new HashMap<>();

	Map<String, String> map = new HashMap<>();
	map.put("role", getRole());
	map.put("content", content);

	return map;
    }

    public static class User extends Prompt {
	public User(String content) {
	    super(content);
	}

	@Override
	String getRole() {
	    return "user";
	}
    }

    public static class System extends Prompt {
	public System(String content) {
	    super(content);
	}

	@Override
	String getRole() {
	    return "system";
	}
    }
}
