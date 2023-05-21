package io.github.nickm980.smallville.models.memory;

import java.util.Comparator;

public class TemporalMemoryComparator implements Comparator<TemporalMemory> {

    @Override
    public int compare(TemporalMemory o1, TemporalMemory o2) {
	return o1.getTime().compareTo(o2.getTime());
    }

}
