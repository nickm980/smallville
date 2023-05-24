package io.github.nickm980.smallville.entities;

import java.time.Duration;

import java.time.LocalDateTime;

public class SimulationTime {
    private static final LocalDateTime START = LocalDateTime.now();

    private boolean isRealTime = true;
    private LocalDateTime simulationTime;
    private Duration timestepDuration;

    private static SimulationTime instance;

    public SimulationTime() {
	isRealTime = true;
    }

    public SimulationTime(LocalDateTime simulationTime, Duration timestepDuration) {
	this.simulationTime = simulationTime;
	this.timestepDuration = timestepDuration;
    }

    public LocalDateTime getSimulationTime() {
	return isRealTime ? LocalDateTime.now() : simulationTime;
    }

    public void setSimulationTime(LocalDateTime simulationTime) {
	this.simulationTime = simulationTime;
    }

    public Duration getTimestepDuration() {
	return timestepDuration;
    }

    public void setTimestepDuration(Duration timestepDuration) {
	this.timestepDuration = timestepDuration;
    }

    public void incrementSimulationTime() {
	simulationTime = simulationTime.plus(timestepDuration);
    }

    public static LocalDateTime startedAt() {
	return START;
    }

    public static SimulationTime getInstance() {
	if (instance == null) {
	    instance = new SimulationTime();
	}

	return instance;
    }
}