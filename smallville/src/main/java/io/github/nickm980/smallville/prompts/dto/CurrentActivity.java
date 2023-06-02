package io.github.nickm980.smallville.prompts.dto;

public class CurrentActivity {
    private String lastActivity;
    private String currentActivity;
    private String emoji;
    private String location;
    private String object;
    
    public String getLocation() {
	return location;
    }

    public void setLocation(String location) {
	this.location = location;
    }

    public String getLastActivity() {
	return lastActivity;
    }

    public void setLastActivity(String lastActivity) {
	this.lastActivity = lastActivity;
    }

    public String getCurrentActivity() {
	return currentActivity;
    }

    public void setCurrentActivity(String currentActivity) {
	this.currentActivity = currentActivity;
    }

    public String getEmoji() {
	return emoji;
    }

    public void setEmoji(String emoji) {
	this.emoji = emoji;
    }

    public String getObject() {
	return object;
    }
    
    public void setObject(String object) {
	this.object = object;
    }
}
