package io.github.nickm980.smallville.entities;

import java.util.ArrayList;
import java.util.List;

import io.github.nickm980.smallville.exceptions.SmallvilleException;

public class Conversation {

    private List<Dialog> messages;
    private String agent;
    private String other;

    public Conversation(String agent, String other, List<Dialog> messages) {
	this.messages = new ArrayList<Dialog>();
	this.agent = agent;
	this.other = other;
	this.messages = messages;
    }

    public List<Dialog> getDialog() {
	return messages;
    }

    public boolean isPartOfConversation(String name) {
	return agent.equals(name) || other.equals(name);
    }
    
    public int size() {
	return messages.size();
    }

    public String getTalker() {
	return agent;
    }
    
    public String getTalkee() {
	return other;
    }
}
