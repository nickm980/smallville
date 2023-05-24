package io.github.nickm980.smallville.prompts.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import io.github.nickm980.smallville.entities.Timekeeper;
import io.github.nickm980.smallville.config.SmallvilleConfig;

public class DateModel {

    public String getTime() {
	return format(Timekeeper.getSimulationTime(), SmallvilleConfig.getConfig().getTimeFormat());
    }

    public String getFull() {
	return format(Timekeeper.getSimulationTime(), SmallvilleConfig.getConfig().getFullTimeFormat());
    }

    public String getYesterday() {
	return format(Timekeeper.getSimulationTime().minusDays(1), SmallvilleConfig.getConfig().getYesterdayFormat());
    }

    private String format(LocalDateTime time, String pattern) {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
	return time.format(formatter);
    }
}
