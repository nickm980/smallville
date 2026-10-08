package io.github.nickm980.smallville.prompts.dto;

public class WorldProposalCandidate {
    private String answer;
    private String type;
    private String parentLocation;
    private String name;
    private String proposedState;
    private String reason;
    private String normalizationDetail;

    public String getAnswer() {
	return answer;
    }

    public void setAnswer(String answer) {
	this.answer = answer;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getParentLocation() {
	return parentLocation;
    }

    public void setParentLocation(String parentLocation) {
	this.parentLocation = parentLocation;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getProposedState() {
	return proposedState;
    }

    public void setProposedState(String proposedState) {
	this.proposedState = proposedState;
    }

    public String getReason() {
	return reason;
    }

    public void setReason(String reason) {
	this.reason = reason;
    }

    public String getNormalizationDetail() {
	return normalizationDetail;
    }

    public void setNormalizationDetail(String normalizationDetail) {
	this.normalizationDetail = normalizationDetail;
    }
}
