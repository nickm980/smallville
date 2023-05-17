package io.github.nickm980.smallville.llm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Prompt {
    private String content;
    private List<Prompt> others;

    public Prompt(String content) {
	this.content = content;
	this.others = new ArrayList<Prompt>();
    }

    public void append(String content) {
	this.content += " " + content;
    }
    
    abstract String getRole();
    
    public String getContent() {
	return content;
    }
    
    public Map<String, String> build() {
	Map<String, String> map = new HashMap<>();
	map.put("role", getRole());
	map.put("content", content.replace("\n", " "));

	for (Prompt prompt : others) {
	    others.add(prompt);
	}
	
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

