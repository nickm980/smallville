package io.github.nickm980.smallville.memory.ledger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Compressed dream pack — produced by the dreaming phase when cold memories
 * are archived.  Serialized as {@code <pack-id>.json.gz} under the agent's
 * dream-packs directory.
 */
public class DreamPack {

    private String packId;
    private String agentName;
    private int createdAtTick;
    private String summaryText;
    private List<String> sourceHashes;
    private Map<String, String> retentionReasons; // sourceHash → reason
    private int compressedEntryCount;

    public DreamPack() {
    }

    public DreamPack(String agentName, int createdAtTick, String summaryText,
		     List<String> sourceHashes, Map<String, String> retentionReasons,
		     int compressedEntryCount) {
	this.packId = UUID.randomUUID().toString();
	this.agentName = agentName;
	this.createdAtTick = createdAtTick;
	this.summaryText = summaryText;
	this.sourceHashes = sourceHashes == null ? new ArrayList<>() : new ArrayList<>(sourceHashes);
	this.retentionReasons = retentionReasons == null ? new HashMap<>() : new HashMap<>(retentionReasons);
	this.compressedEntryCount = compressedEntryCount;
    }

    // --- Getters / Setters ---

    public String getPackId() {
	return packId;
    }

    public void setPackId(String packId) {
	this.packId = packId;
    }

    public String getAgentName() {
	return agentName;
    }

    public void setAgentName(String agentName) {
	this.agentName = agentName;
    }

    public int getCreatedAtTick() {
	return createdAtTick;
    }

    public void setCreatedAtTick(int createdAtTick) {
	this.createdAtTick = createdAtTick;
    }

    public String getSummaryText() {
	return summaryText;
    }

    public void setSummaryText(String summaryText) {
	this.summaryText = summaryText;
    }

    public List<String> getSourceHashes() {
	return sourceHashes;
    }

    public void setSourceHashes(List<String> sourceHashes) {
	this.sourceHashes = sourceHashes == null ? new ArrayList<>() : new ArrayList<>(sourceHashes);
    }

    public Map<String, String> getRetentionReasons() {
	return retentionReasons;
    }

    public void setRetentionReasons(Map<String, String> retentionReasons) {
	this.retentionReasons = retentionReasons == null ? new HashMap<>() : new HashMap<>(retentionReasons);
    }

    public int getCompressedEntryCount() {
	return compressedEntryCount;
    }

    public void setCompressedEntryCount(int compressedEntryCount) {
	this.compressedEntryCount = compressedEntryCount;
    }
}
