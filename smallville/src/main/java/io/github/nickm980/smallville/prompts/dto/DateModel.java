package io.github.nickm980.smallville.prompts.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.github.nickm980.smallville.config.SmallvilleConfig;

public class DateModel {

    public String getTime() {
	return format(LocalDateTime.now(), SmallvilleConfig.getConfig().getTimeFormat());
    }

    public String getFull() {
	return format(LocalDateTime.now(), SmallvilleConfig.getConfig().getFullTimeFormat());
    }

    public String getYesterday() {
	return format(LocalDateTime.now().minusDays(1), SmallvilleConfig.getConfig().getYesterdayFormat());
    }

    private String format(LocalDateTime time, String pattern) {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
	return time.format(formatter);
    }
}
