package io.github.nickm980.smallville.api.v1.dto;

public class MemoryResponse {

    private String description;
    private String time;
    private String type;
    private double importance;

    public String getTime() {
	return time;
    }

    public void setTime(String time) {
	this.time = time;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public double getImportance() {
	return importance;
    }

    public void setImportance(double importance) {
	this.importance = importance;
    }
}
