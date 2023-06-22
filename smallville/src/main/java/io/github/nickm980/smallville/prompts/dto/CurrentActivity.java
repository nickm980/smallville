package io.github.nickm980.smallville.prompts.dto;

public class CurrentActivity {
    private String lastActivity;
    private String activity;
    private String emoji;
    private String location;

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

    public String getActivity() {
	return activity;
    }

    public void setActivity(String currentActivity) {
	this.activity = currentActivity;
    }

    public String getEmoji() {
	return emoji;
    }

    public void setEmoji(String emoji) {
	this.emoji = emoji;
    }
}
