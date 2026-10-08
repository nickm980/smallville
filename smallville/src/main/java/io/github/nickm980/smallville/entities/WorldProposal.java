package io.github.nickm980.smallville.entities;

public class WorldProposal {
    private String id;
    private String agent;
    private String type;
    private String parentLocation;
    private String name;
    private String proposedState;
    private String reason;
    private String status;
    private int createdAtTick;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getAgent() {
	return agent;
    }

    public void setAgent(String agent) {
	this.agent = agent;
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

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public int getCreatedAtTick() {
	return createdAtTick;
    }

    public void setCreatedAtTick(int createdAtTick) {
	this.createdAtTick = createdAtTick;
    }
}
