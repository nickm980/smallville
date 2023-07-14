package io.github.nickm980.smallville.api.v1.dto;

public class AgentStateResponse {
    private String name;
    private String action;
    private String location;
    private String emoji;
    private String object;
    
    public String getObject() {
	return object;
    }
    
    public void setObject(String object) {
	this.object = object;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getEmoji() {
        return emoji;
    }
    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
    
    
}
