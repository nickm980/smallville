package io.github.nickm980.smallville.prompts.dto;

public class Reaction {
    private String reasoning;
    private String answer;
    private String conversation;

    public String getReasoning() {
	return reasoning;
    }

    public void setReasoning(String reasoning) {
	this.reasoning = reasoning;
    }

    public String getAnswer() {
	return answer;
    }

    public void setAnswer(String answer) {
	this.answer = answer;
    }

    public String getConversation() {
	return conversation;
    }

    public void setConversation(String conversation) {
	this.conversation = conversation;
    }
}
