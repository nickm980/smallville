package io.github.nickm980.smallville.memory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import io.github.nickm980.smallville.entities.SimulationTime;
import io.github.nickm980.smallville.math.SmallvilleMath;

/**
 * Plans are basically just memories in the future tense. There are more events
 * closer to the current time and later plans are more spread out. Should always
 * have enough plans for the rest of the day but need to recalculate
 * periodically
 */
public class Plan extends Memory implements TemporalMemory {

    private final LocalDateTime time;
    public PlanType type;

    public Plan(String description, LocalDateTime time) {
	this(description, time, PlanType.LONG_TERM);
    }

    public Plan(String description, LocalDateTime time, PlanType type) {
	super(description);
	this.time = time;
	this.type = type;
    }

    public PlanType getType() {
	return type;
    }

    public LocalDateTime getTime() {
	return time;
    }

    @Override
    double getRecency() {
	var now = SimulationTime.now();
	var a = ChronoUnit.SECONDS.between(time, SimulationTime.startedAt());
	var b = ChronoUnit.SECONDS.between(now, time);
	var timeSinceStart = ChronoUnit.SECONDS.between(now, SimulationTime.startedAt());

	return SmallvilleMath.normalize(SmallvilleMath.decay(a, b), timeSinceStart, 0);
    }

    public void convert(PlanType type) {
	this.type = type;
    }
}