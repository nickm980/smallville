package io.github.nickm980.smallville.models.memory;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Includes plans, observations, and characteristics
 */
public class MemoryStream {
    private Set<Memory> memories;

    public MemoryStream() {
	this.memories = new HashSet<Memory>();
    }

    /**
     * Prunes the weaker, less poingnant memories and returns the strongest ones
     * based on observations and updated plans.
     * <p>
     * Will run several comparisons. First, will extract names from the query and
     * compare the token embeddings of the names to each memory. Then will do the
     * same for the full query.
     * 
     * @return
     */
    public List<Memory> getRelevantMemories(String query) {
	return memories.stream().sorted().limit(3).toList();
    }

    public List<Memory> getUnweightedMemories() {
	return memories.stream().filter(memory -> memory.getImportance() == 0).toList();
    }

    public void remember(String memory) {
	this.memories.add(new Observation(memory));
    }

    public Set<Memory> getMemories() {
	return memories;
    }

    public List<Observation> getObservations() {
	return memories.stream().filter(memory -> {
	    return memory instanceof Observation;
	}).map(memory -> {
	    return (Observation) memory;
	}).collect(Collectors.toList());
    }

    public List<Characteristic> getCharacteristics() {
	return memories.stream().filter(memory -> {
	    return memory instanceof Characteristic;
	}).map(memory -> {
	    return (Characteristic) memory;
	}).collect(Collectors.toList());
    }

    public List<Plan> getPlans() {
	return memories.stream().filter(memory -> {
	    return memory instanceof Plan;
	}).map(memory -> {
	    return (Plan) memory;
	}).collect(Collectors.toList());
    }

    public void setPlans(List<Plan> plans) {
	memories.addAll(plans);
    }

    public void addAll(List<String> memories) {
	this.memories.addAll(memories.stream().map(Observation::new).toList());
    }

    public void addMemories(List<Memory> memories) {
	this.memories.addAll(memories);
    }

    public void addCharacteristics(List<Characteristic> characteristics) {
	this.memories.addAll(characteristics);
    }

    public void prunePlans() {
	for (Memory memory : memories) {
	    if (memory instanceof Plan) {
		Plan plan = (Plan) memory;
		if (plan.getTime() != null && plan.getTime().compareTo(LocalDateTime.now()) < 0) {
		    memories.remove(plan);
		}
	    }
	}
    }
}
