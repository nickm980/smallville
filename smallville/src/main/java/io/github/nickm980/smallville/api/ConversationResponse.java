package io.github.nickm980.smallville.api;

import java.util.Map;

public class ConversationResponse {

    private Map<String, String> messages;

    public Map<String, String> getMessages() {
	return messages;
    }

    public void setMessages(Map<String, String> messages) {
	this.messages = messages;
    }
}
