package io.github.nickm980.smallville.entities.memory;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import io.github.nickm980.smallville.entities.AccessTime;
import io.github.nickm980.smallville.entities.Timekeeper;
import io.github.nickm980.smallville.math.SmallvilleMath;

/**
 * Observations are past events (including dialog / conversations) that the
 * agent is aware of. There is a decay which makes older memories easier to
 * forget
 */
public class Observation extends Memory implements TemporalMemory {

    private final LocalDateTime time;

    public Observation(String description) {
	super(description);
	time = Timekeeper.getSimulationTime();
    }

    public Observation(String description, LocalDateTime time, int importance) {
	super(description);
	this.time = time;
	super.setImportance(importance);
    }

    @Override
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
}