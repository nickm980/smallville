package io.github.nickm980.smallville.entities.memory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import io.github.nickm980.smallville.entities.AccessTime;
import io.github.nickm980.smallville.entities.NaturalLanguageConvertible;
import io.github.nickm980.smallville.entities.Timekeeper;
import io.github.nickm980.smallville.math.SmallvilleMath;

/**
 * Plans are basically just memories in the future tense. There are more events
 * closer to the current time and later plans are more spread out. Should always
 * have enough plans for the rest of the day but need to recalculate
 * periodically
 */
public class Plan extends Memory implements TemporalMemory, NaturalLanguageConvertible {

    private final LocalDateTime time;
    private boolean isShortTerm;
    public PlanType type;

    public Plan(String description, LocalDateTime time) {
	this(description, time, false);
    }

    public Plan(String description, LocalDateTime time, boolean isShortTerm) {
	super(description);
	this.time = time;
	this.isShortTerm = isShortTerm;
	this.type = PlanType.LONG_TERM;
    }

    public PlanType getType() {
	return type;
    }

    public boolean isShortTerm() {
	return isShortTerm;
    }

    public LocalDateTime getTime() {
	return time;
    }

    @Override
    double getRecency() {
	var now = Timekeeper.getSimulationTime();
	var a = ChronoUnit.SECONDS.between(time, AccessTime.START);
	var b = ChronoUnit.SECONDS.between(now, time);
	var timeSinceStart = ChronoUnit.SECONDS.between(now, AccessTime.START);

	return SmallvilleMath.normalize(SmallvilleMath.decay(a, b), timeSinceStart, 0);
    }

    @Override
    public String asNaturalLanguage() {
	return getDescription();
    }

    public void convertToShortTermMemory(boolean b, PlanType type) {
	this.isShortTerm = b;
	this.type = type;
    }
}