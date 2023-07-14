package io.github.nickm980.smallville.update;

public class UpdateInfo {

    private boolean shouldUpdateConversation;
    private String observation;
    private boolean plansUpdated;

    public boolean isPlansUpdated() {
	return plansUpdated;
    }

    public void setPlansUpdated(boolean plansUpdated) {
	this.plansUpdated = plansUpdated;
    }

    public boolean shouldUpdateConversation() {
	return shouldUpdateConversation;
    }

    public void setShouldUpdateConversation(boolean shouldUpdateConversation) {
	this.shouldUpdateConversation = shouldUpdateConversation;
    }

    public String getObservation() {
	return observation;
    }

    public void setObservation(String observation) {
	this.observation = observation;
    }
}
