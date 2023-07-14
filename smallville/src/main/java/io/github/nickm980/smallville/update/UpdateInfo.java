package io.github.nickm980.smallville.update;

public class UpdateInfo {

    private boolean shouldUpdateConversation;
    private String observation;
    
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
