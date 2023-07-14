package io.github.nickm980.smallville.api.v1.dto;

import java.util.Map;

public class ConversationResponse {

    private String name;
    private String message;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }
}
