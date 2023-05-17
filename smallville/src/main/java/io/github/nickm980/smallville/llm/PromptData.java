package io.github.nickm980.smallville.llm;

import java.util.ArrayList;
import java.util.List;

import io.github.nickm980.smallville.models.Agent;
import io.github.nickm980.smallville.models.Conversation;
import io.github.nickm980.smallville.models.Location;
import io.github.nickm980.smallville.models.SimulatedLocation;

public class PromptData {

    private Agent agent;
    private List<? extends Location> locations;
    private Conversation conversation;
    private String question;

    public PromptData() {
	this.locations = new ArrayList<SimulatedLocation>();
	this.locations = List.of();
	this.question = "";
    }

    public Agent getAgent() {
	return agent;
    }

    public void setAgent(Agent agent) {
	this.agent = agent;
    }

    public List<? extends Location> getLocations() {
	return locations;
    }

    public void setLocations(List<? extends Location> locations) {
	this.locations = locations;
    }

    public void setConversation(Conversation conversation) {
	this.conversation = conversation;
    }

    public Conversation getConversation() {
	return conversation;
    }

    public void setQuestion(String question) {
	this.question = question;
    }

    public String getQuestion() {
	return question;
    }

}