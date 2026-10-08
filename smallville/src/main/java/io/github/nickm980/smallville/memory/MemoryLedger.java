package io.github.nickm980.smallville.memory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MemoryLedger {
    private String agent;
    private List<String> personaAnchors = new ArrayList<String>();
    private AffectState affect = new AffectState();
    private List<LedgerMemoryEntry> working = new ArrayList<LedgerMemoryEntry>();
    private List<LedgerMemoryEntry> recent = new ArrayList<LedgerMemoryEntry>();
    private List<LedgerMemoryEntry> archived = new ArrayList<LedgerMemoryEntry>();
    private List<DreamPack> dreamPacks = new ArrayList<DreamPack>();
    private RecallPolicy recallPolicy = new RecallPolicy();
    private MemoryLedgerSummary summary = new MemoryLedgerSummary();
    private Integer lastDreamTick = -1;
    private String lastDreamWindowKey;
    private String updatedAtUtc;

    @JsonIgnore
    private final Set<String> seenMemoryKeys = new LinkedHashSet<String>();

    public String getAgent() {
	return agent;
    }

    public void setAgent(String agent) {
	this.agent = agent;
    }

    public List<String> getPersonaAnchors() {
	return personaAnchors;
    }

    public void setPersonaAnchors(List<String> personaAnchors) {
	this.personaAnchors = personaAnchors == null ? new ArrayList<String>() : new ArrayList<String>(personaAnchors);
	rebuildSummary();
    }

    public AffectState getAffect() {
	return affect;
    }

    public void setAffect(AffectState affect) {
	this.affect = affect == null ? new AffectState() : affect;
    }

    public List<LedgerMemoryEntry> getWorking() {
	return working;
    }

    public void setWorking(List<LedgerMemoryEntry> working) {
	this.working = working == null ? new ArrayList<LedgerMemoryEntry>() : new ArrayList<LedgerMemoryEntry>(working);
	rebuildSummary();
	rebuildSeenMemoryKeys();
    }

    public List<LedgerMemoryEntry> getRecent() {
	return recent;
    }

    public void setRecent(List<LedgerMemoryEntry> recent) {
	this.recent = recent == null ? new ArrayList<LedgerMemoryEntry>() : new ArrayList<LedgerMemoryEntry>(recent);
	rebuildSummary();
	rebuildSeenMemoryKeys();
    }

    public List<LedgerMemoryEntry> getArchived() {
	return archived;
    }

    public void setArchived(List<LedgerMemoryEntry> archived) {
	this.archived = archived == null ? new ArrayList<LedgerMemoryEntry>() : new ArrayList<LedgerMemoryEntry>(archived);
	rebuildSummary();
	rebuildSeenMemoryKeys();
    }

    public List<DreamPack> getDreamPacks() {
	return dreamPacks;
    }

    public void setDreamPacks(List<DreamPack> dreamPacks) {
	this.dreamPacks = dreamPacks == null ? new ArrayList<DreamPack>() : new ArrayList<DreamPack>(dreamPacks);
	rebuildSummary();
	rebuildSeenMemoryKeys();
    }

    public RecallPolicy getRecallPolicy() {
	return recallPolicy;
    }

    public void setRecallPolicy(RecallPolicy recallPolicy) {
	this.recallPolicy = recallPolicy == null ? new RecallPolicy() : recallPolicy;
    }

    public MemoryLedgerSummary getSummary() {
	return summary;
    }

    public void setSummary(MemoryLedgerSummary summary) {
	this.summary = summary == null ? new MemoryLedgerSummary() : summary;
    }

    public Integer getLastDreamTick() {
	return lastDreamTick;
    }

    public void setLastDreamTick(Integer lastDreamTick) {
	this.lastDreamTick = lastDreamTick;
    }

    public String getLastDreamWindowKey() {
	return lastDreamWindowKey;
    }

    public void setLastDreamWindowKey(String lastDreamWindowKey) {
	this.lastDreamWindowKey = lastDreamWindowKey;
    }

    public String getUpdatedAtUtc() {
	return updatedAtUtc;
    }

    public void setUpdatedAtUtc(String updatedAtUtc) {
	this.updatedAtUtc = updatedAtUtc;
    }

    @JsonIgnore
    public Set<String> getSeenMemoryKeys() {
	return seenMemoryKeys;
    }

    public void rebuildSummary() {
	MemoryLedgerSummary next = summary == null ? new MemoryLedgerSummary() : summary;
	next.setPersonaAnchorCount(personaAnchors == null ? 0 : personaAnchors.size());
	next.setWorkingCount(working == null ? 0 : working.size());
	next.setRecentCount(recent == null ? 0 : recent.size());
	next.setArchivedCount(archived == null ? 0 : archived.size());
	next.setDreamPackCount(dreamPacks == null ? 0 : dreamPacks.size());
	next.setLastDreamTick(lastDreamTick);
	summary = next;
    }

    public void rebuildSeenMemoryKeys() {
	seenMemoryKeys.clear();
	registerEntries(working);
	registerEntries(recent);
	registerEntries(archived);
	if (dreamPacks == null) {
	    return;
	}
	for (DreamPack dreamPack : dreamPacks) {
	    if (dreamPack == null) {
		continue;
	    }
	    registerEntries(dreamPack.getMemories());
	}
    }

    public boolean hasSeenKey(String key) {
	return key != null && seenMemoryKeys.contains(key);
    }

    public void rememberKey(String key) {
	if (key != null && !key.isBlank()) {
	    seenMemoryKeys.add(key);
	}
    }

    private void registerEntries(List<LedgerMemoryEntry> entries) {
	if (entries == null) {
	    return;
	}
	for (LedgerMemoryEntry entry : entries) {
	    if (entry == null) {
		continue;
	    }
	    String key = entry.getKey();
	    if (key != null && !key.isBlank()) {
		seenMemoryKeys.add(key);
	    }
	}
    }

    public static class MemoryLedgerSummary {
	private int personaAnchorCount;
	private int workingCount;
	private int recentCount;
	private int archivedCount;
	private int dreamPackCount;
	private Integer lastDreamTick;

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
    }

    public static class RecallPolicy {
	private int topK = 3;
	private int hotMemoryLimit = 6;
	private String archiveCompression = "json.gz";
	private List<String> order = List.of("working", "recent", "dream-packs", "archived");

	public int getTopK() {
	    return topK;
	}

	public void setTopK(int topK) {
	    this.topK = topK;
	}

	public int getHotMemoryLimit() {
	    return hotMemoryLimit;
	}

	public void setHotMemoryLimit(int hotMemoryLimit) {
	    this.hotMemoryLimit = hotMemoryLimit;
	}

	public String getArchiveCompression() {
	    return archiveCompression;
	}

	public void setArchiveCompression(String archiveCompression) {
	    this.archiveCompression = archiveCompression;
	}

	public List<String> getOrder() {
	    return order;
	}

	public void setOrder(List<String> order) {
	    this.order = order == null ? List.of("working", "recent", "dream-packs", "archived") : new ArrayList<String>(order);
	}
    }

    public static class LedgerMemoryEntry {
	private String id;
	private String key;
	private String kind;
	private String description;
	private String sourceHash;
	private String source;
	private String memoryTime;
	private String seenAtUtc;
	private String seenAtSimulationTime;
	private int firstSeenTick;
	private int lastSeenTick;
	private double importance;
	private String retentionReason;
	private int recallCount;

	public String getId() {
	    return id;
	}

	public void setId(String id) {
	    this.id = id;
	}

	public String getKey() {
	    return key;
	}

	public void setKey(String key) {
	    this.key = key;
	}

	public String getKind() {
	    return kind;
	}

	public void setKind(String kind) {
	    this.kind = kind;
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

	public String getSource() {
	    return source;
	}

	public void setSource(String source) {
	    this.source = source;
	}

	public String getMemoryTime() {
	    return memoryTime;
	}

	public void setMemoryTime(String memoryTime) {
	    this.memoryTime = memoryTime;
	}

	public String getSeenAtUtc() {
	    return seenAtUtc;
	}

	public void setSeenAtUtc(String seenAtUtc) {
	    this.seenAtUtc = seenAtUtc;
	}

	public String getSeenAtSimulationTime() {
	    return seenAtSimulationTime;
	}

	public void setSeenAtSimulationTime(String seenAtSimulationTime) {
	    this.seenAtSimulationTime = seenAtSimulationTime;
	}

	public int getFirstSeenTick() {
	    return firstSeenTick;
	}

	public void setFirstSeenTick(int firstSeenTick) {
	    this.firstSeenTick = firstSeenTick;
	}

	public int getLastSeenTick() {
	    return lastSeenTick;
	}

	public void setLastSeenTick(int lastSeenTick) {
	    this.lastSeenTick = lastSeenTick;
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

	public int getRecallCount() {
	    return recallCount;
	}

	public void setRecallCount(int recallCount) {
	    this.recallCount = recallCount;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
		return true;
	    }
	    if (!(obj instanceof LedgerMemoryEntry)) {
		return false;
	    }
	    LedgerMemoryEntry other = (LedgerMemoryEntry) obj;
	    return Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(id);
	}
    }

    public static class DreamPack {
	private String id;
	private String createdAtUtc;
	private String createdAtSimulationTime;
	private int createdAtTick;
	private String compression = "json.gz";
	private List<String> summary = new ArrayList<String>();
	private List<String> sourceHashes = new ArrayList<String>();
	private List<String> retentionReasons = new ArrayList<String>();
	private List<LedgerMemoryEntry> memories = new ArrayList<LedgerMemoryEntry>();

	public String getId() {
	    return id;
	}

	public void setId(String id) {
	    this.id = id;
	}

	public String getCreatedAtUtc() {
	    return createdAtUtc;
	}

	public void setCreatedAtUtc(String createdAtUtc) {
	    this.createdAtUtc = createdAtUtc;
	}

	public String getCreatedAtSimulationTime() {
	    return createdAtSimulationTime;
	}

	public void setCreatedAtSimulationTime(String createdAtSimulationTime) {
	    this.createdAtSimulationTime = createdAtSimulationTime;
	}

	public int getCreatedAtTick() {
	    return createdAtTick;
	}

	public void setCreatedAtTick(int createdAtTick) {
	    this.createdAtTick = createdAtTick;
	}

	public String getCompression() {
	    return compression;
	}

	public void setCompression(String compression) {
	    this.compression = compression;
	}

	public List<String> getSummary() {
	    return summary;
	}

	public void setSummary(List<String> summary) {
	    this.summary = summary == null ? new ArrayList<String>() : new ArrayList<String>(summary);
	}

	public List<String> getSourceHashes() {
	    return sourceHashes;
	}

	public void setSourceHashes(List<String> sourceHashes) {
	    this.sourceHashes = sourceHashes == null ? new ArrayList<String>() : new ArrayList<String>(sourceHashes);
	}

	public List<String> getRetentionReasons() {
	    return retentionReasons;
	}

	public void setRetentionReasons(List<String> retentionReasons) {
	    this.retentionReasons = retentionReasons == null ? new ArrayList<String>() : new ArrayList<String>(retentionReasons);
	}

	public List<LedgerMemoryEntry> getMemories() {
	    return memories;
	}

	public void setMemories(List<LedgerMemoryEntry> memories) {
	    this.memories = memories == null ? new ArrayList<LedgerMemoryEntry>() : new ArrayList<LedgerMemoryEntry>(memories);
	}
    }
}
