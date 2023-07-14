package io.github.nickm980.smallville.memory;

import java.time.LocalDateTime;
import java.util.Comparator;

public interface TemporalMemory {
    LocalDateTime getTime();

    public static class TemporalComparator implements Comparator<TemporalMemory> {

	@Override
	public int compare(TemporalMemory o1, TemporalMemory o2) {
	    return o1.getTime().compareTo(o2.getTime());
	}

    }
}
