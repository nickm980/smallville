package io.github.nickm980.smallville.api.v1.dto;

import java.util.ArrayList;
import java.util.List;

public class LocalLedgerExportResponse {
    private String generatedAtUtc;
    private ScenarioResponse scenario = new ScenarioResponse();
    private BootstrapResponse bootstrap = new BootstrapResponse();
    private WorldSnapshotResponse world = new WorldSnapshotResponse();
    private GovernanceLedgerResponse governanceLedger = new GovernanceLedgerResponse();
    private List<WorldSnapshotResponse.WorldProposalResponse> proposalHistoryFull = new ArrayList<WorldSnapshotResponse.WorldProposalResponse>();
    private MemoryIndexResponse memoryIndex = new MemoryIndexResponse();
    private OfflinePolicyResponse offlinePolicy = new OfflinePolicyResponse();
    private GovernancePolicyResponse governancePolicy = new GovernancePolicyResponse();

    public String getGeneratedAtUtc() {
	return generatedAtUtc;
    }

    public void setGeneratedAtUtc(String generatedAtUtc) {
	this.generatedAtUtc = generatedAtUtc;
    }

    public ScenarioResponse getScenario() {
	return scenario;
    }

    public void setScenario(ScenarioResponse scenario) {
	this.scenario = scenario;
    }

    public BootstrapResponse getBootstrap() {
	return bootstrap;
    }

    public void setBootstrap(BootstrapResponse bootstrap) {
	this.bootstrap = bootstrap;
    }

    public WorldSnapshotResponse getWorld() {
	return world;
    }

    public void setWorld(WorldSnapshotResponse world) {
	this.world = world;
    }

    public GovernanceLedgerResponse getGovernanceLedger() {
	return governanceLedger;
    }

    public void setGovernanceLedger(GovernanceLedgerResponse governanceLedger) {
	this.governanceLedger = governanceLedger;
    }

    public List<WorldSnapshotResponse.WorldProposalResponse> getProposalHistoryFull() {
	return proposalHistoryFull;
    }

    public void setProposalHistoryFull(List<WorldSnapshotResponse.WorldProposalResponse> proposalHistoryFull) {
	this.proposalHistoryFull = proposalHistoryFull;
    }

    public MemoryIndexResponse getMemoryIndex() {
	return memoryIndex;
    }

    public void setMemoryIndex(MemoryIndexResponse memoryIndex) {
	this.memoryIndex = memoryIndex;
    }

    public OfflinePolicyResponse getOfflinePolicy() {
	return offlinePolicy;
    }

    public void setOfflinePolicy(OfflinePolicyResponse offlinePolicy) {
	this.offlinePolicy = offlinePolicy;
    }

    public GovernancePolicyResponse getGovernancePolicy() {
	return governancePolicy;
    }

    public void setGovernancePolicy(GovernancePolicyResponse governancePolicy) {
	this.governancePolicy = governancePolicy;
    }

    public static class ScenarioResponse {
	private String name = "default";
	private String configRoot;
	private boolean simulationFileEnabled;
	private String worldSummary = "";

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getConfigRoot() {
	    return configRoot;
	}

	public void setConfigRoot(String configRoot) {
	    this.configRoot = configRoot;
	}

	public boolean isSimulationFileEnabled() {
	    return simulationFileEnabled;
	}

	public void setSimulationFileEnabled(boolean simulationFileEnabled) {
	    this.simulationFileEnabled = simulationFileEnabled;
	}

	public String getWorldSummary() {
	    return worldSummary;
	}

	public void setWorldSummary(String worldSummary) {
	    this.worldSummary = worldSummary;
	}
    }

    public static class BootstrapResponse {
	private int seededLocationCount;
	private int seededAgentCount;
	private List<String> seededAgents = new ArrayList<String>();
	private String governanceLedgerPath;
	private String memoryRoot;
	private int dreamIntervalTicks;
	private String dreamWindowStart;
	private String dreamWindowEnd;
	private int hotMemoryLimit;
	private int recallTopK;
	private String archiveCompression;

	public int getSeededLocationCount() {
	    return seededLocationCount;
	}

	public void setSeededLocationCount(int seededLocationCount) {
	    this.seededLocationCount = seededLocationCount;
	}

	public int getSeededAgentCount() {
	    return seededAgentCount;
	}

	public void setSeededAgentCount(int seededAgentCount) {
	    this.seededAgentCount = seededAgentCount;
	}

	public List<String> getSeededAgents() {
	    return seededAgents;
	}

	public void setSeededAgents(List<String> seededAgents) {
	    this.seededAgents = seededAgents;
	}

	public String getGovernanceLedgerPath() {
	    return governanceLedgerPath;
	}

	public void setGovernanceLedgerPath(String governanceLedgerPath) {
	    this.governanceLedgerPath = governanceLedgerPath;
	}

	public String getMemoryRoot() {
	    return memoryRoot;
	}

	public void setMemoryRoot(String memoryRoot) {
	    this.memoryRoot = memoryRoot;
	}

	public int getDreamIntervalTicks() {
	    return dreamIntervalTicks;
	}

	public void setDreamIntervalTicks(int dreamIntervalTicks) {
	    this.dreamIntervalTicks = dreamIntervalTicks;
	}

	public String getDreamWindowStart() {
	    return dreamWindowStart;
	}

	public void setDreamWindowStart(String dreamWindowStart) {
	    this.dreamWindowStart = dreamWindowStart;
	}

	public String getDreamWindowEnd() {
	    return dreamWindowEnd;
	}

	public void setDreamWindowEnd(String dreamWindowEnd) {
	    this.dreamWindowEnd = dreamWindowEnd;
	}

	public int getHotMemoryLimit() {
	    return hotMemoryLimit;
	}

	public void setHotMemoryLimit(int hotMemoryLimit) {
	    this.hotMemoryLimit = hotMemoryLimit;
	}

	public int getRecallTopK() {
	    return recallTopK;
	}

	public void setRecallTopK(int recallTopK) {
	    this.recallTopK = recallTopK;
	}

	public String getArchiveCompression() {
	    return archiveCompression;
	}

	public void setArchiveCompression(String archiveCompression) {
	    this.archiveCompression = archiveCompression;
	}
    }

    public static class GovernanceLedgerResponse {
	private List<WorldSnapshotResponse.WorldProposalResponse> proposals = new ArrayList<WorldSnapshotResponse.WorldProposalResponse>();
	private List<String> guidanceRules = new ArrayList<String>();
	private List<String> blockedProposalTargets = new ArrayList<String>();

	public List<WorldSnapshotResponse.WorldProposalResponse> getProposals() {
	    return proposals;
	}

	public void setProposals(List<WorldSnapshotResponse.WorldProposalResponse> proposals) {
	    this.proposals = proposals;
	}

	public List<String> getGuidanceRules() {
	    return guidanceRules;
	}

	public void setGuidanceRules(List<String> guidanceRules) {
	    this.guidanceRules = guidanceRules;
	}

	public List<String> getBlockedProposalTargets() {
	    return blockedProposalTargets;
	}

	public void setBlockedProposalTargets(List<String> blockedProposalTargets) {
	    this.blockedProposalTargets = blockedProposalTargets;
	}
    }

    public static class MemoryIndexResponse {
	private String rootPath;
	private List<AgentMemoryIndexResponse> agents = new ArrayList<AgentMemoryIndexResponse>();

	public String getRootPath() {
	    return rootPath;
	}

	public void setRootPath(String rootPath) {
	    this.rootPath = rootPath;
	}

	public List<AgentMemoryIndexResponse> getAgents() {
	    return agents;
	}

	public void setAgents(List<AgentMemoryIndexResponse> agents) {
	    this.agents = agents;
	}
    }

    public static class AgentMemoryIndexResponse {
	private String agent;
	private int personaAnchorCount;
	private int workingCount;
	private int recentCount;
	private int archivedCount;
	private int dreamPackCount;
	private Integer lastDreamTick;
	private String indexFile;
	private String eventsFile;
	private List<String> dreamPackFiles = new ArrayList<String>();

	public String getAgent() {
	    return agent;
	}

	public void setAgent(String agent) {
	    this.agent = agent;
	}

	public int getPersonaAnchorCount() {
	    return personaAnchorCount;
	}

	public void setPersonaAnchorCount(int personaAnchorCount) {
	    this.personaAnchorCount = personaAnchorCount;
	}

	public int getWorkingCount() {
	    return workingCount;
	}

	public void setWorkingCount(int workingCount) {
	    this.workingCount = workingCount;
	}

	public int getRecentCount() {
	    return recentCount;
	}

	public void setRecentCount(int recentCount) {
	    this.recentCount = recentCount;
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

	public Integer getLastDreamTick() {
	    return lastDreamTick;
	}

	public void setLastDreamTick(Integer lastDreamTick) {
	    this.lastDreamTick = lastDreamTick;
	}

	public String getIndexFile() {
	    return indexFile;
	}

	public void setIndexFile(String indexFile) {
	    this.indexFile = indexFile;
	}

	public String getEventsFile() {
	    return eventsFile;
	}

	public void setEventsFile(String eventsFile) {
	    this.eventsFile = eventsFile;
	}

	public List<String> getDreamPackFiles() {
	    return dreamPackFiles;
	}

	public void setDreamPackFiles(List<String> dreamPackFiles) {
	    this.dreamPackFiles = dreamPackFiles;
	}
    }

    public static class OfflinePolicyResponse {
	private boolean offlineMode;
	private boolean loopbackOnly;
	private boolean cloudSupportEnabled;
	private String primaryApiPath;
	private boolean primaryLoopback;
	private boolean askShadowBridgeEnabled;
	private String askShadowBridgeEndpoint;
	private boolean askShadowBridgeLoopback;
	private String cloudSupportApiPath;
	private boolean cloudSupportLoopback;

	public boolean isOfflineMode() {
	    return offlineMode;
	}

	public void setOfflineMode(boolean offlineMode) {
	    this.offlineMode = offlineMode;
	}

	public boolean isLoopbackOnly() {
	    return loopbackOnly;
	}

	public void setLoopbackOnly(boolean loopbackOnly) {
	    this.loopbackOnly = loopbackOnly;
	}

	public boolean isCloudSupportEnabled() {
	    return cloudSupportEnabled;
	}

	public void setCloudSupportEnabled(boolean cloudSupportEnabled) {
	    this.cloudSupportEnabled = cloudSupportEnabled;
	}

	public String getPrimaryApiPath() {
	    return primaryApiPath;
	}

	public void setPrimaryApiPath(String primaryApiPath) {
	    this.primaryApiPath = primaryApiPath;
	}

	public boolean isPrimaryLoopback() {
	    return primaryLoopback;
	}

	public void setPrimaryLoopback(boolean primaryLoopback) {
	    this.primaryLoopback = primaryLoopback;
	}

	public boolean isAskShadowBridgeEnabled() {
	    return askShadowBridgeEnabled;
	}

	public void setAskShadowBridgeEnabled(boolean askShadowBridgeEnabled) {
	    this.askShadowBridgeEnabled = askShadowBridgeEnabled;
	}

	public String getAskShadowBridgeEndpoint() {
	    return askShadowBridgeEndpoint;
	}

	public void setAskShadowBridgeEndpoint(String askShadowBridgeEndpoint) {
	    this.askShadowBridgeEndpoint = askShadowBridgeEndpoint;
	}

	public boolean isAskShadowBridgeLoopback() {
	    return askShadowBridgeLoopback;
	}

	public void setAskShadowBridgeLoopback(boolean askShadowBridgeLoopback) {
	    this.askShadowBridgeLoopback = askShadowBridgeLoopback;
	}

	public String getCloudSupportApiPath() {
	    return cloudSupportApiPath;
	}

	public void setCloudSupportApiPath(String cloudSupportApiPath) {
	    this.cloudSupportApiPath = cloudSupportApiPath;
	}

	public boolean isCloudSupportLoopback() {
	    return cloudSupportLoopback;
	}

	public void setCloudSupportLoopback(boolean cloudSupportLoopback) {
	    this.cloudSupportLoopback = cloudSupportLoopback;
	}
    }

    public static class GovernancePolicyResponse {
	private String proposalMode;
	private int reviewWindowHours;
	private boolean unwindowedExport = true;
	private List<String> guidanceRules = new ArrayList<String>();
	private List<WorldSnapshotResponse.GovernanceBlockRuleResponse> blockRules = new ArrayList<WorldSnapshotResponse.GovernanceBlockRuleResponse>();

	public String getProposalMode() {
	    return proposalMode;
	}

	public void setProposalMode(String proposalMode) {
	    this.proposalMode = proposalMode;
	}

	public int getReviewWindowHours() {
	    return reviewWindowHours;
	}

	public void setReviewWindowHours(int reviewWindowHours) {
	    this.reviewWindowHours = reviewWindowHours;
	}

	public boolean isUnwindowedExport() {
	    return unwindowedExport;
	}

	public void setUnwindowedExport(boolean unwindowedExport) {
	    this.unwindowedExport = unwindowedExport;
	}

	public List<String> getGuidanceRules() {
	    return guidanceRules;
	}

	public void setGuidanceRules(List<String> guidanceRules) {
	    this.guidanceRules = guidanceRules;
	}

	public List<WorldSnapshotResponse.GovernanceBlockRuleResponse> getBlockRules() {
	    return blockRules;
	}

	public void setBlockRules(List<WorldSnapshotResponse.GovernanceBlockRuleResponse> blockRules) {
	    this.blockRules = blockRules;
	}
    }
}
