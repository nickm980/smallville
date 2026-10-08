package io.github.nickm980.smallville.memory.dreaming;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.config.simulation.SimulationFile;
import io.github.nickm980.smallville.entities.AffectState;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.SimulationTime;
import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.memory.Memory;
import io.github.nickm980.smallville.memory.MemoryStream;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.memory.Plan;
import io.github.nickm980.smallville.memory.ledger.DreamPack;
import io.github.nickm980.smallville.memory.ledger.LedgerEntryType;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerEntry;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerIndex;
import io.github.nickm980.smallville.memory.ledger.MemoryJournalWriter;

/**
 * Deterministic dreaming phase — runs locally without LLM calls.
 * <p>
 * During the dream window, scores all non-working memories against persona
 * anchors, partitions into hot/cold, compresses cold into a DreamPack,
 * derives a new AffectState, and writes everything to the NDJSON journal.
 */
public class DreamingService {

    private static final Logger LOG = LoggerFactory.getLogger(DreamingService.class);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final MemoryJournalWriter journalWriter;
    private final Map<String, Integer> lastDreamTick = new HashMap<>();

    public DreamingService(MemoryJournalWriter journalWriter) {
	this.journalWriter = journalWriter;
    }

    /**
     * Run the dream cycle for an agent if eligible.
     *
     * @return true if a dream cycle actually ran
     */
    public boolean runDreamCycle(Agent agent, SimulationFile.MemorySeed config, int currentTick) {
	if (config == null) {
	    config = new SimulationFile.MemorySeed();
	}

	if (!isDreamEligible(agent.getFullName(), config, currentTick)) {
	    return false;
	}

	LOG.info("Starting dream cycle for {} at tick {}", agent.getFullName(), currentTick);

	MemoryStream stream = agent.getMemoryStream();
	String personaAnchor = buildPersonaAnchor(agent);

	// Score all non-working memories against the persona anchor
	List<Memory> allMemories = new ArrayList<>();
	allMemories.addAll(stream.getObservations());
	allMemories.addAll(stream.getPlans());
	// Don't include characteristics — they're anchors, not candidates

	List<ScoredMemory> scored = allMemories.stream()
	    .map(m -> new ScoredMemory(m, m.getScore(personaAnchor)))
	    .sorted(Comparator.comparingDouble(ScoredMemory::getScore).reversed())
	    .collect(Collectors.toList());

	// Partition: hot = top N, cold = remainder
	int hotLimit = config.getHotMemoryLimit();
	List<ScoredMemory> hotSet = scored.stream().limit(hotLimit).collect(Collectors.toList());
	List<ScoredMemory> coldSet = scored.stream().skip(hotLimit).collect(Collectors.toList());

	// Compress cold set into a DreamPack
	DreamPack dreamPack = null;
	if (!coldSet.isEmpty()) {
	    dreamPack = compressToDreamPack(agent.getFullName(), currentTick, coldSet);
	    journalWriter.writeDreamPack(agent.getFullName(), dreamPack, config.isArchiveCompression());
	}

	// Derive AffectState from dream results
	AffectState affect = deriveAffect(hotSet, currentTick);
	agent.setAffect(affect);

	// Update the index
	MemoryLedgerIndex index = journalWriter.loadOrCreateIndex(agent.getFullName());
	index.setLastDreamTick(currentTick);
	index.setHotCount(hotSet.size());
	index.setArchivedCount(index.getArchivedCount() + coldSet.size());
	if (dreamPack != null) {
	    index.setDreamPackCount(index.getDreamPackCount() + 1);
	}
	index.setTotalEvents(index.getTotalEvents() + 1);
	index.setLastAffect(affect);
	journalWriter.writeIndex(agent.getFullName(), index);

	// Write dream-compress ledger entry
	String dreamDesc = String.format("Dream cycle: %d hot, %d archived, mood=%s",
	    hotSet.size(), coldSet.size(), affect.getMoodLabel());
	MemoryLedgerEntry entry = new MemoryLedgerEntry(
	    agent.getFullName(), currentTick, SimulationTime.now(),
	    LedgerEntryType.DREAM_COMPRESS, dreamDesc, 0, null);
	journalWriter.appendEntry(agent.getFullName(), entry);

	lastDreamTick.put(agent.getFullName(), currentTick);

	LOG.info("Dream cycle complete for {}: {} hot, {} cold, affect={}", 
	    agent.getFullName(), hotSet.size(), coldSet.size(), affect.getMoodLabel());

	return true;
    }

    boolean isDreamEligible(String agentName, SimulationFile.MemorySeed config, int currentTick) {
	// Check interval
	Integer lastTick = lastDreamTick.get(agentName);
	if (lastTick != null && (currentTick - lastTick) < config.getDreamIntervalTicks()) {
	    return false;
	}

	// Check dream window
	return isWithinDreamWindow(config.getDreamWindowStart(), config.getDreamWindowEnd());
    }

    boolean isWithinDreamWindow(String startStr, String endStr) {
	try {
	    LocalTime start = LocalTime.parse(startStr, TIME_FMT);
	    LocalTime end = LocalTime.parse(endStr, TIME_FMT);
	    LocalTime simTime = SimulationTime.now().toLocalTime();

	    if (start.isAfter(end)) {
		// Wraps midnight: e.g. 22:00 to 06:00
		return !simTime.isBefore(start) || !simTime.isAfter(end);
	    } else {
		return !simTime.isBefore(start) && !simTime.isAfter(end);
	    }
	} catch (Exception e) {
	    LOG.warn("Could not parse dream window times, defaulting to eligible", e);
	    return true;
	}
    }

    private String buildPersonaAnchor(Agent agent) {
	StringBuilder sb = new StringBuilder();
	sb.append(agent.getFullName());

	if (agent.getTraits() != null && !agent.getTraits().isBlank()) {
	    sb.append(". Traits: ").append(agent.getTraits());
	}

	for (Characteristic c : agent.getMemoryStream().getCharacteristics()) {
	    sb.append(". ").append(c.getDescription());
	}

	for (String goal : agent.getGoals()) {
	    sb.append(". Goal: ").append(goal);
	}

	for (String ritual : agent.getRituals()) {
	    sb.append(". Ritual: ").append(ritual);
	}

	if (agent.getSocialPreference() != null) {
	    sb.append(". Social preference: ").append(agent.getSocialPreference());
	}

	return sb.toString();
    }

    private DreamPack compressToDreamPack(String agentName, int tick, List<ScoredMemory> coldSet) {
	List<String> sourceHashes = new ArrayList<>();
	Map<String, String> retentionReasons = new HashMap<>();
	StringBuilder summaryBuilder = new StringBuilder();

	int summaryLimit = Math.min(5, coldSet.size());
	for (int i = 0; i < coldSet.size(); i++) {
	    Memory m = coldSet.get(i).getMemory();
	    String hash = MemoryLedgerEntry.sha256(m.getDescription());
	    sourceHashes.add(hash);

	    if (i < summaryLimit) {
		if (summaryBuilder.length() > 0) {
		    summaryBuilder.append("; ");
		}
		summaryBuilder.append(m.getDescription());
		retentionReasons.put(hash, "top-" + (i + 1) + "-cold-by-score");
	    } else {
		retentionReasons.put(hash, "archived");
	    }
	}

	return new DreamPack(agentName, tick, summaryBuilder.toString(),
	    sourceHashes, retentionReasons, coldSet.size());
    }

    private AffectState deriveAffect(List<ScoredMemory> hotSet, int tick) {
	if (hotSet.isEmpty()) {
	    return AffectState.neutral(tick);
	}

	double avgImportance = hotSet.stream()
	    .mapToDouble(sm -> sm.getMemory().getImportance())
	    .average()
	    .orElse(0.0);

	long socialCount = hotSet.stream()
	    .filter(sm -> isSocialMemory(sm.getMemory().getDescription()))
	    .count();
	double socialRatio = (double) socialCount / hotSet.size();

	String topFocus = hotSet.get(0).getMemory().getDescription();

	List<String> drivers = hotSet.stream()
	    .limit(3)
	    .map(sm -> sm.getMemory().getDescription())
	    .collect(Collectors.toList());

	return AffectState.fromDreamCycle(avgImportance, socialRatio, topFocus, drivers, tick);
    }

    private boolean isSocialMemory(String description) {
	if (description == null) return false;
	String lower = description.toLowerCase();
	return lower.contains("said") || lower.contains("told") || lower.contains("asked")
	    || lower.contains("conversation") || lower.contains("spoke") || lower.contains("talked")
	    || lower.contains("together") || lower.contains("met") || lower.contains("greeting");
    }

    /**
     * Restore last dream tick from persisted index (called on restart).
     */
    public void restoreLastDreamTick(String agentName, int tick) {
	lastDreamTick.put(agentName, tick);
    }

    /** Scored wrapper for sorting during dream cycle. */
    static class ScoredMemory {
	private final Memory memory;
	private final double score;

	ScoredMemory(Memory memory, double score) {
	    this.memory = memory;
	    this.score = score;
	}

	Memory getMemory() { return memory; }
	double getScore() { return score; }
    }
}
