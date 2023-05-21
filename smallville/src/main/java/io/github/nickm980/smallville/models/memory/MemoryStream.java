package io.github.nickm980.smallville.models.memory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Includes plans, observations, and characteristics
 */
public class MemoryStream {
    private List<Memory> memories;

    public MemoryStream() {
	this.memories = new ArrayList<Memory>();
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

    public void addObservation(String memory) {
	this.memories.add(new Observation(memory));
    }

    public List<Memory> getMemories() {
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
	}).sorted(new TemporalMemoryComparator()).collect(Collectors.toList());
    }

    public void setPlans(List<Plan> plans) {
	this.memories.addAll(plans);
    }

    public void addObservations(List<String> memories) {
	this.memories.addAll(memories.stream().map(Observation::new).toList());
    }

    public void addMemories(List<Memory> memories) {
	this.memories.addAll(memories);
    }

    public void addCharacteristics(List<Characteristic> characteristics) {
	this.memories.addAll(characteristics);
    }

    public void prunePlans() {
	memories.removeIf((memory) -> {
	    if (memory instanceof Plan) {
		Plan plan = (Plan) memory;
		return plan.getTime() != null && plan.getTime().compareTo(LocalDateTime.now()) < 0;
	    }
	    return false;
	});
    }

    public void setShortTermPlans(List<Plan> plans) {
	List<Plan> removed = getShortTermPlans();
	memories.removeAll(removed);
	memories.addAll(plans);
    }

    public List<Plan> getShortTermPlans() {
	return getPlans().stream().filter(plan -> plan.isShortTerm()).toList();
    }

    public List<? extends TemporalMemory> sortByTime(List<? extends TemporalMemory> mems) {
	return mems.stream().sorted(new Comparator<TemporalMemory>() {
	    @Override
	    public int compare(TemporalMemory o1, TemporalMemory o2) {
		return o1.getTime().compareTo(o2.getTime());
	    }
	}).toList();
    }

    public void addPlans(List<Plan> plans) {
	this.memories.addAll(plans);
    }

    public List<Plan> getPlans(PlanType midTerm) {
	return getPlans().stream().filter(plan -> plan.getType() == midTerm).toList();
    }
}
