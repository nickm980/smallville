package io.github.nickm980.smallville.memory.ledger;

import io.github.nickm980.smallville.entities.AffectState;

/**
 * Per-agent index summarizing memory governance state.  Persisted as
 * {@code index.json} in the agent's memory ledger directory.
 */
public class MemoryLedgerIndex {

    private String agentName;
    private int totalEvents;
    private int hotCount;
    private int archivedCount;
    private int dreamPackCount;
    private int lastDreamTick;
    private int lastRecallTick;
    private int ponderCount;
    private int lastPonderTick;
    private AffectState lastAffect;

    public MemoryLedgerIndex() {
    }

    public MemoryLedgerIndex(String agentName) {
	this.agentName = agentName;
    }

    // --- Getters / Setters ---

    public String getAgentName() {
	return agentName;
    }

    public void setAgentName(String agentName) {
	this.agentName = agentName;
    }

    public int getTotalEvents() {
	return totalEvents;
    }

    public void setTotalEvents(int totalEvents) {
	this.totalEvents = totalEvents;
    }

    public int getHotCount() {
	return hotCount;
    }

    public void setHotCount(int hotCount) {
	this.hotCount = hotCount;
    }

    public int getArchivedCount() {
	return archivedCount;
    }

    public void setArchivedCount(int archivedCount) {
	this.archivedCount = archivedCount;
    }

    public int getDreamPackCount() {
	return dreamPackCount;
    }

    public void setDreamPackCount(int dreamPackCount) {
	this.dreamPackCount = dreamPackCount;
    }

    public int getLastDreamTick() {
	return lastDreamTick;
    }

    public void setLastDreamTick(int lastDreamTick) {
	this.lastDreamTick = lastDreamTick;
    }

    public int getLastRecallTick() {
	return lastRecallTick;
    }

    public void setLastRecallTick(int lastRecallTick) {
	this.lastRecallTick = lastRecallTick;
    }

    public int getPonderCount() {
	return ponderCount;
    }

    public void setPonderCount(int ponderCount) {
	this.ponderCount = ponderCount;
    }

    public int getLastPonderTick() {
	return lastPonderTick;
    }

    public void setLastPonderTick(int lastPonderTick) {
	this.lastPonderTick = lastPonderTick;
    }

    public AffectState getLastAffect() {
	return lastAffect;
    }

    public void setLastAffect(AffectState lastAffect) {
	this.lastAffect = lastAffect;
    }
}
