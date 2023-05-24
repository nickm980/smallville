package io.github.nickm980.smallville.entities;

import java.time.Duration;

import java.time.LocalDateTime;

public class Timekeeper {
    private static boolean isRealTime = true;
    private static LocalDateTime simulationTime;
    private static Duration timestepDuration;

    public void initialize() {
        isRealTime = true;
    }

    public static void initialize(LocalDateTime simulationTime, Duration timestepDuration) {

        Timekeeper.simulationTime = simulationTime;
        Timekeeper.timestepDuration = timestepDuration;
    }


    public static LocalDateTime getSimulationTime() {
        if (!isRealTime) {
            return simulationTime;
        } else if (isRealTime) {
            return LocalDateTime.now();
        }
        System.out.println("simulationTime: " + simulationTime);
        return null;
    }

    public static void setSimulationTime(LocalDateTime simulationTime) {
        Timekeeper.simulationTime = simulationTime;
    }

    public static Duration getTimestepDuration() {
        return timestepDuration;
    }

    public static void setTimestepDuration(Duration timestepDuration) {
        Timekeeper.timestepDuration = timestepDuration;
    }

    public static void incrementSimulationTime() {
        simulationTime = simulationTime.plus(timestepDuration);
    }

}