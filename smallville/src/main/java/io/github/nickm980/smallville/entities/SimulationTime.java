package io.github.nickm980.smallville.entities;

import java.time.Duration;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

import io.github.nickm980.smallville.exceptions.SmallvilleException;


public final class SimulationTime {
    private static final LocalDateTime START = LocalDateTime.now();

    private static volatile LocalDateTime time = LocalDateTime.now();
    private static volatile Duration step = Duration.ofMinutes(1);

    public static synchronized LocalDateTime now() { return time; }

    public static synchronized void setSimulationTime(LocalDateTime simTime) {
	time = simTime;
    }

    public static synchronized void setStep(Duration duration) {
	step = duration;
    }

    public static synchronized void update() {
	if (step == null || time == null) {
	    throw new SmallvilleException("Missing timestep or time");
	}

	time = time.plus(step);
    }

    public static synchronized LocalDateTime startedAt() {
	return START;
    }

    public static synchronized Duration getStepDuration() {
	return step;
    }

    public static int getStepDurationInMinutes() {
	return (int) getStepDuration().getSeconds()/60;
    }
}