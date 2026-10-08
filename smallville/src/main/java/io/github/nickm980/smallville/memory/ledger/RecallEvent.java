package io.github.nickm980.smallville.memory.ledger;

/**
 * Records a single recall operation — which memory was selected, from which
 * layer, and whether it was promoted into working memory.
 */
public class RecallEvent {

    private String agentName;
    private int tick;
    private String query;
    private String selectedMemoryHash;
    private String sourceLayer; // working|hot|dream-summary|archive
    private boolean promotedToWorking;

    public RecallEvent() {
    }

    public RecallEvent(String agentName, int tick, String query, String selectedMemoryHash,
		       String sourceLayer, boolean promotedToWorking) {
	this.agentName = agentName;
	this.tick = tick;
	this.query = query;
	this.selectedMemoryHash = selectedMemoryHash;
	this.sourceLayer = sourceLayer;
	this.promotedToWorking = promotedToWorking;
    }

    // --- Getters / Setters ---

    public String getAgentName() {
	return agentName;
    }

    public void setAgentName(String agentName) {
	this.agentName = agentName;
    }

    public int getTick() {
	return tick;
    }

    public void setTick(int tick) {
	this.tick = tick;
    }

    public String getQuery() {
	return query;
    }

    public void setQuery(String query) {
	this.query = query;
    }

    public String getSelectedMemoryHash() {
	return selectedMemoryHash;
    }

    public void setSelectedMemoryHash(String selectedMemoryHash) {
	this.selectedMemoryHash = selectedMemoryHash;
    }

    public String getSourceLayer() {
	return sourceLayer;
    }

    public void setSourceLayer(String sourceLayer) {
	this.sourceLayer = sourceLayer;
    }

    public boolean isPromotedToWorking() {
	return promotedToWorking;
    }

    public void setPromotedToWorking(boolean promotedToWorking) {
	this.promotedToWorking = promotedToWorking;
    }
}
