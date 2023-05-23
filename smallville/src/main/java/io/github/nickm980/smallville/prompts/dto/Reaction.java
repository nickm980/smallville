package io.github.nickm980.smallville.prompts.dto;

public class Reaction {
    private boolean react;
    private String currentActivity;
    private String emoji;

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

    public void setReact(boolean react) {
	this.react = react;
    }

    public boolean willReact() {
	return react;
    }
}
