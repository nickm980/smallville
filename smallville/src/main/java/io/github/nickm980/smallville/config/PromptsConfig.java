package io.github.nickm980.smallville.config;

public class PromptsConfig {

    private String createConversationWith;
    private String createReactionSuggestion;
    private String createCurrentPlanPrompt;
    private String createMemoryRankPrompt;
    private String createFuturePlansPrompt;
    private String createAskQuestionPrompt;
    private String createPastAndPresent;
    private String createObjectUpdates;
    private String pickLocation;
    private String createShortTermPlans;
    private String createMidTermPlans;
    private String agentSummaryDescription;
    
    public String getAgentSummaryDescription() {
        return agentSummaryDescription;
    }

    public void setAgentSummaryDescription(String agentSummaryDescription) {
        this.agentSummaryDescription = agentSummaryDescription;
    }

    public String getCreateMidTermPlans() {
	return createMidTermPlans;
    }

    public void setCreateMidTermPlans(String createMidTermPlans) {
	this.createMidTermPlans = createMidTermPlans;
    }

    public PromptsConfig() {
    }

    public String getCreateConversationWith() {
	return createConversationWith;
    }

    public void setCreateConversationWith(String createConversationWith) {
	this.createConversationWith = createConversationWith;
    }

    public String getCreateReactionSuggestion() {
	return createReactionSuggestion;
    }

    public void setCreateReactionSuggestion(String createReactionSuggestion) {
	this.createReactionSuggestion = createReactionSuggestion;
    }

    public String getCreateCurrentPlanPrompt() {
	return createCurrentPlanPrompt;
    }

    public void setCreateCurrentPlanPrompt(String createCurrentPlanPrompt) {
	this.createCurrentPlanPrompt = createCurrentPlanPrompt;
    }

    public String getCreateMemoryRankPrompt() {
	return createMemoryRankPrompt;
    }

    public void setCreateMemoryRankPrompt(String createMemoryRankPrompt) {
	this.createMemoryRankPrompt = createMemoryRankPrompt;
    }

    public String getCreateFuturePlansPrompt() {
	return createFuturePlansPrompt;
    }

    public void setCreateFuturePlansPrompt(String createFuturePlansPrompt) {
	this.createFuturePlansPrompt = createFuturePlansPrompt;
    }

    public String getCreateAskQuestionPrompt() {
	return createAskQuestionPrompt;
    }

    public void setCreateAskQuestionPrompt(String createAskQuestionPrompt) {
	this.createAskQuestionPrompt = createAskQuestionPrompt;
    }

    public String getCreatePastAndPresent() {
	return createPastAndPresent;
    }

    public void setCreatePastAndPresent(String createPastAndPresent) {
	this.createPastAndPresent = createPastAndPresent;
    }

    public String getCreateObjectUpdates() {
	return createObjectUpdates;
    }

    public void setCreateObjectUpdates(String createObjectUpdates) {
	this.createObjectUpdates = createObjectUpdates;
    }

    public String getPickLocation() {
	return pickLocation;
    }

    public void setPickLocation(String pickLocation) {
	this.pickLocation = pickLocation;
    }

    public String getCreateShortTermPlans() {
	return createShortTermPlans;
    }

    public void setCreateShortTermPlans(String createShortTermPlans) {
	this.createShortTermPlans = createShortTermPlans;
    }

}
