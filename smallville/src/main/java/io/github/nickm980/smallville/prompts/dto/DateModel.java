package io.github.nickm980.smallville.prompts.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.entities.SimulationTime;

public class DateModel {

    public String getTime() {
	return format(SimulationTime.now(), SmallvilleConfig.getConfig().getTimeFormat());
    }

    public String getFull() {
	return format(SimulationTime.now(), SmallvilleConfig.getConfig().getFullTimeFormat());
    }

    public String getYesterday() {
	return format(SimulationTime.now().minusDays(1), SmallvilleConfig.getConfig().getYesterdayFormat());
    }

    private String format(LocalDateTime time, String pattern) {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
	return time.format(formatter);
    }
}
