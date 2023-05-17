package io.github.nickm980.smallville.llm.response;

public class CurrentPlan {
    private String lastActivity;
    private String currentActivity;
    private String emoji;

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

}
