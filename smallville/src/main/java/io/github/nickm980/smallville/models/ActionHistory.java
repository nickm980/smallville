package io.github.nickm980.smallville.models;

public class ActionHistory {

    private String activity;
    private String lastActivity;
    private String emoji;
    
    public ActionHistory(String action) {
	this.activity = action;
	this.lastActivity = "";
    }

    public String getActivity() {
	return activity;
    }

    public String getLastActivity() {
	return lastActivity;
    }

    public void setActivity(String activity) {
	this.lastActivity = this.activity;
	this.activity = activity;
    }

    public void setEmoji(String emoji) {
	this.emoji = emoji;
    }

    public String getEmoji() {
	return emoji;
    }
}
