package io.github.nickm980.smallville.memory.ledger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Single NDJSON-serializable ledger event.  Append-only to the agent's
 * events.ndjson journal file.
 */
public class MemoryLedgerEntry {

    private String id;
    private String agentName;
    private int tick;
    private LocalDateTime timestamp;
    private LedgerEntryType type;
    private String description;
    private String sourceHash;
    private double importance;
    private String retentionReason; // nullable

    public MemoryLedgerEntry() {
    }

    public MemoryLedgerEntry(String agentName, int tick, LocalDateTime timestamp, LedgerEntryType type,
			     String description, double importance, String retentionReason) {
	this.id = UUID.randomUUID().toString();
	this.agentName = agentName;
	this.tick = tick;
	this.timestamp = timestamp;
	this.type = type;
	this.description = description;
	this.sourceHash = sha256(description);
	this.importance = importance;
	this.retentionReason = retentionReason;
    }

    public static String sha256(String input) {
	if (input == null || input.isEmpty()) {
	    return "";
	}
	try {
	    MessageDigest digest = MessageDigest.getInstance("SHA-256");
	    byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) {
		hex.append(String.format("%02x", b));
	    }
	    return hex.toString();
	} catch (NoSuchAlgorithmException e) {
	    throw new RuntimeException("SHA-256 not available", e);
	}
    }

    // --- Getters / Setters ---

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

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

    public LocalDateTime getTimestamp() {
	return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
	this.timestamp = timestamp;
    }

    public LedgerEntryType getType() {
	return type;
    }

    public void setType(LedgerEntryType type) {
	this.type = type;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getSourceHash() {
	return sourceHash;
    }

    public void setSourceHash(String sourceHash) {
	this.sourceHash = sourceHash;
    }

    public double getImportance() {
	return importance;
    }

    public void setImportance(double importance) {
	this.importance = importance;
    }

    public String getRetentionReason() {
	return retentionReason;
    }

    public void setRetentionReason(String retentionReason) {
	this.retentionReason = retentionReason;
    }
}
