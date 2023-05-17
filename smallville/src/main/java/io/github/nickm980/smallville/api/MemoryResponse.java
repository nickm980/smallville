package io.github.nickm980.smallville.api;

import java.time.LocalDateTime;

public class MemoryResponse {

    private String description;
    private LocalDateTime time;
    private String type;
    private double importance;

    public LocalDateTime getTime() {
	return time;
    }

    public void setTime(LocalDateTime time) {
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
