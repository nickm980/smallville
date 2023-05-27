package io.github.nickm980.smallville.repository;

import java.time.LocalDateTime;

import io.github.nickm980.smallville.entities.SimulationTime;

public class RepositoryItem<T> {
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private T data;

    RepositoryItem(T data) {
	createdAt = SimulationTime.now();
	updatedAt = SimulationTime.now();
	this.data = data;
    }

    public LocalDateTime createdAt() {
	return createdAt;
    }

    public LocalDateTime updatedAt() {
	return updatedAt;
    }

    public T getData() {
	return data;
    }

    public void update(T data) {
	this.updatedAt = SimulationTime.now();
	this.data = data;
    }
}
