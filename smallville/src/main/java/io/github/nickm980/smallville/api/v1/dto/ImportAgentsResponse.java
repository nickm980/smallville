package io.github.nickm980.smallville.api.v1.dto;

import java.util.ArrayList;
import java.util.List;

public class ImportAgentsResponse {
    private boolean success;
    private boolean preview;
    private List<String> errors = new ArrayList<String>();
    private List<String> createdAgents = new ArrayList<String>();
    private List<AgentImportPreview> agents = new ArrayList<AgentImportPreview>();

    public boolean isSuccess() {
	return success;
    }

    public void setSuccess(boolean success) {
	this.success = success;
    }

    public boolean isPreview() {
	return preview;
    }

    public void setPreview(boolean preview) {
	this.preview = preview;
    }

    public List<String> getErrors() {
	return errors;
    }

    public void setErrors(List<String> errors) {
	this.errors = errors;
    }

    public List<String> getCreatedAgents() {
	return createdAgents;
    }

    public void setCreatedAgents(List<String> createdAgents) {
	this.createdAgents = createdAgents;
    }

    public List<AgentImportPreview> getAgents() {
	return agents;
    }

    public void setAgents(List<AgentImportPreview> agents) {
	this.agents = agents;
    }

    public static class AgentImportPreview {
	private String name;
	private String location;
	private String activity;
	private String model;
	private List<String> issues = new ArrayList<String>();

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getLocation() {
	    return location;
	}

	public void setLocation(String location) {
	    this.location = location;
	}

	public String getActivity() {
	    return activity;
	}

	public void setActivity(String activity) {
	    this.activity = activity;
	}

	public String getModel() {
	    return model;
	}

	public void setModel(String model) {
	    this.model = model;
	}

	public List<String> getIssues() {
	    return issues;
	}

	public void setIssues(List<String> issues) {
	    this.issues = issues;
	}
    }
}
