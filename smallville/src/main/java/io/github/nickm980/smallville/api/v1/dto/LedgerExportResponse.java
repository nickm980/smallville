package io.github.nickm980.smallville.api.v1.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.nickm980.smallville.memory.ledger.MemoryLedgerIndex;

/**
 * Response DTO for {@code GET /world/ledger/export}.
 */
public class LedgerExportResponse {

    private String generatedAtUtc;
    private String scenario;
    private Map<String, Object> bootstrap = new HashMap<>();
    private WorldSnapshotResponse world;
    private List<WorldSnapshotResponse.WorldProposalResponse> governanceLedger = new ArrayList<>();
    private List<WorldSnapshotResponse.WorldProposalResponse> proposalHistoryFull = new ArrayList<>();
    private Map<String, MemoryLedgerIndex> memoryIndex = new HashMap<>();
    private Map<String, Object> offlinePolicy = new HashMap<>();
    private Map<String, Object> governancePolicy = new HashMap<>();

    // --- Getters / Setters ---

    public String getGeneratedAtUtc() {
	return generatedAtUtc;
    }

    public void setGeneratedAtUtc(String generatedAtUtc) {
	this.generatedAtUtc = generatedAtUtc;
    }

    public String getScenario() {
	return scenario;
    }

    public void setScenario(String scenario) {
	this.scenario = scenario;
    }

    public Map<String, Object> getBootstrap() {
	return bootstrap;
    }

    public void setBootstrap(Map<String, Object> bootstrap) {
	this.bootstrap = bootstrap;
    }

    public WorldSnapshotResponse getWorld() {
	return world;
    }

    public void setWorld(WorldSnapshotResponse world) {
	this.world = world;
    }

    public List<WorldSnapshotResponse.WorldProposalResponse> getGovernanceLedger() {
	return governanceLedger;
    }

    public void setGovernanceLedger(List<WorldSnapshotResponse.WorldProposalResponse> governanceLedger) {
	this.governanceLedger = governanceLedger;
    }

    public List<WorldSnapshotResponse.WorldProposalResponse> getProposalHistoryFull() {
	return proposalHistoryFull;
    }

    public void setProposalHistoryFull(List<WorldSnapshotResponse.WorldProposalResponse> proposalHistoryFull) {
	this.proposalHistoryFull = proposalHistoryFull;
    }

    public Map<String, MemoryLedgerIndex> getMemoryIndex() {
	return memoryIndex;
    }

    public void setMemoryIndex(Map<String, MemoryLedgerIndex> memoryIndex) {
	this.memoryIndex = memoryIndex;
    }

    public Map<String, Object> getOfflinePolicy() {
	return offlinePolicy;
    }

    public void setOfflinePolicy(Map<String, Object> offlinePolicy) {
	this.offlinePolicy = offlinePolicy;
    }

    public Map<String, Object> getGovernancePolicy() {
	return governancePolicy;
    }

    public void setGovernancePolicy(Map<String, Object> governancePolicy) {
	this.governancePolicy = governancePolicy;
    }
}
