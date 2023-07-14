package io.github.nickm980.smallville.memory;

import io.github.nickm980.smallville.math.SmallvilleMath;

public abstract class Memory implements Comparable<Memory> {

    private String description;
    private int weight;

    public Memory(String description) {
	if (description == null) {
	    description = "";
	}
	this.description = description.replace("-", "").trim();
	this.weight = 0;
    }

    /**
     * Calculate the score of a memory based on a query score = a * recency + a *
     * importance + a * relevancy
     * 
     * @param observation
     * @return A value between 0 and 1 where 1 is the strongest score
     */
    public double getScore(String observation) {
	int a1 = 1;
	int a2 = 1;
	int a3 = 1;
	double score = a1 * getRecency() + a2 * getImportance() + a3 * getRelevancy(observation);

	if (Double.isNaN(score)) {
	    score = getRelevancy(observation);
	}

	return score;
    }

    /**
     * How recent the memory was. A larger number is more recent
     * 
     * @return double bounded between SIMULATION_START_TIME and SimulationTime.now()
     */
    abstract double getRecency();

    public double getImportance() {
	return weight;
    }

    private double getRelevancy(String observation) {
	return SmallvilleMath.calculateSentenceSimilarity(observation, description);
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public void setImportance(int weight) {
	this.weight = weight;
    }

    @Override
    public int compareTo(Memory o) {
	double score = getScore(description);
	double other = getScore(o.getDescription());
	if (score > other) {
	    return 1;
	}
	if (score < other) {
	    return -1;
	}
	return 0;
    }
}
