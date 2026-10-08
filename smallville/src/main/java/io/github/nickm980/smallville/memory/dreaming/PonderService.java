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
import io.github.nickm980.smallville.memory.ledger.LedgerEntryType;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerEntry;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerIndex;
import io.github.nickm980.smallville.memory.ledger.MemoryJournalWriter;

/**
 * Daytime ponder mechanism — a lighter alternative to dream cycles that
 * runs during daily rhythm break windows (morning tea, lunch, afternoon tea).
 * <p>
 * Ponder scores recent memories (last 3 ticks) against the agent's persona
 * anchor using a shifted contentment formula that treats routine activity
 * as emotionally neutral rather than distressing.  The result is blended
 * with the existing dream-derived affect state.
 * <p>
 * Ponder and dream are mutually exclusive per tick: dream takes priority.
 */
public class PonderService {

    private static final Logger LOG = LoggerFactory.getLogger(PonderService.class);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final int PONDER_MEMORY_DEPTH = 3;

    private final MemoryJournalWriter journalWriter;
    private final Map<String, Integer> lastPonderTick = new HashMap<>();

    public PonderService(MemoryJournalWriter journalWriter) {
	this.journalWriter = journalWriter;
    }

    /**
     * Run ponder for an agent if eligible. Returns true if ponder fired.
     *
     * @param agent       the agent to ponder
     * @param memConfig   memory seed config (ponder settings)
     * @param rhythm      daily rhythm seed (break windows)
     * @param currentTick current simulation tick
     * @param dreamFired  whether a dream cycle already ran this tick
     */
    public boolean runPonder(Agent agent, SimulationFile.MemorySeed memConfig,
			     SimulationFile.DailyRhythmSeed rhythm, int currentTick, boolean dreamFired) {
	if (memConfig == null || rhythm == null) {
	    return false;
	}

	if (!memConfig.isPonderEnabled()) {
	    return false;
	}

	if (dreamFired) {
	    return false;
	}

	if (!isPonderEligible(agent.getFullName(), memConfig, currentTick)) {
	    return false;
	}

	String breakWindow = detectBreakWindow(rhythm);
	if (breakWindow == null) {
	    return false;
	}

	// Must NOT be within dream window
	if (isWithinDreamWindow(memConfig.getDreamWindowStart(), memConfig.getDreamWindowEnd())) {
	    return false;
	}

	LOG.info("Starting ponder for {} during {} at tick {}", agent.getFullName(), breakWindow, currentTick);

	double preValence = agent.getAffect().getValence();
	String preMood = agent.getAffect().getMoodLabel();

	// Score recent memories against persona anchor
	double ponderValence = computePonderValence(agent, currentTick);

	// Blend with existing affect
	double alpha = memConfig.getPonderBlendAlpha();
	AffectState nudged;
	if ("eveningWind".equals(breakWindow)) {
	    // Evening: wind down activation, boost social drive
	    nudged = agent.getAffect().withEveningNudge(ponderValence, alpha,
		memConfig.getEveningSocialBoost(), memConfig.getEveningActivationDamp(), currentTick);
	} else {
	    nudged = agent.getAffect().withNudge(ponderValence, alpha, currentTick);
	}
	agent.setAffect(nudged);

	// Update ledger index
	MemoryLedgerIndex index = journalWriter.loadOrCreateIndex(agent.getFullName());
	index.setPonderCount(index.getPonderCount() + 1);
	index.setLastPonderTick(currentTick);
	index.setLastAffect(nudged);
	index.setTotalEvents(index.getTotalEvents() + 1);
	journalWriter.writeIndex(agent.getFullName(), index);

	// Write ponder ledger entry
	String desc = String.format("Ponder during %s: valence %.3f -> %.3f, mood %s -> %s",
	    breakWindow, preValence, nudged.getValence(), preMood, nudged.getMoodLabel());
	MemoryLedgerEntry entry = new MemoryLedgerEntry(
	    agent.getFullName(), currentTick, SimulationTime.now(),
	    LedgerEntryType.PONDER, desc, 0, null);
	journalWriter.appendEntry(agent.getFullName(), entry);

	lastPonderTick.put(agent.getFullName(), currentTick);

	LOG.info("Ponder complete for {}: {} -> {}, valence {} -> {}",
	    agent.getFullName(), preMood, nudged.getMoodLabel(), preValence, nudged.getValence());

	return true;
    }

    /**
     * Compute ponder valence from recent memories using the shifted contentment formula.
     * Uses last PONDER_MEMORY_DEPTH ticks of memories scored against persona anchor.
     *
     * Formula: ponderValence = clamp((avgImportance / 5.0) - 0.5, -1, 1)
     * Neutral point at importance=2.5 (vs 5.0 for dreams).
     */
    public double computePonderValence(Agent agent, int currentTick) {
	MemoryStream stream = agent.getMemoryStream();
	String personaAnchor = buildPersonaAnchor(agent);

	List<Memory> recentMemories = new ArrayList<>();
	recentMemories.addAll(stream.getObservations());
	recentMemories.addAll(stream.getPlans());

	// Filter to recent memories only (within PONDER_MEMORY_DEPTH ticks worth of recency)
	// Use recency threshold: only memories with high recency scores
	List<DreamingService.ScoredMemory> scored = recentMemories.stream()
	    .map(m -> new DreamingService.ScoredMemory(m, m.getScore(personaAnchor)))
	    .sorted(Comparator.comparingDouble(DreamingService.ScoredMemory::getScore).reversed())
	    .limit(PONDER_MEMORY_DEPTH)
	    .collect(Collectors.toList());

	if (scored.isEmpty()) {
	    return 0.0;
	}

	double avgImportance = scored.stream()
	    .mapToDouble(sm -> sm.getMemory().getImportance())
	    .average()
	    .orElse(0.0);

	// Shifted contentment formula: neutral at importance=2.5 instead of 5.0
	return clamp((avgImportance / 5.0) - 0.5, -1.0, 1.0);
    }

    public boolean isPonderEligible(String agentName, SimulationFile.MemorySeed config, int currentTick) {
	Integer lastTick = lastPonderTick.get(agentName);
	if (lastTick != null && (currentTick - lastTick) < config.getPonderCooldownTicks()) {
	    return false;
	}
	return true;
    }

    /**
     * Detect which break window the current simulation time falls within.
     * Returns the window name ("morningTea", "lunch", "afternoonTea") or null.
     */
    public String detectBreakWindow(SimulationFile.DailyRhythmSeed rhythm) {
	if (isWithinTimeWindow(rhythm.getMorningTea())) {
	    return "morningTea";
	}
	if (isWithinTimeWindow(rhythm.getLunch())) {
	    return "lunch";
	}
	if (isWithinTimeWindow(rhythm.getAfternoonTea())) {
	    return "afternoonTea";
	}
	if (isWithinTimeWindow(rhythm.getEveningWind())) {
	    return "eveningWind";
	}
	return null;
    }

    public boolean isWithinTimeWindow(String window) {
	if (window == null || !window.contains("-")) {
	    return false;
	}
	try {
	    String[] parts = window.split("-");
	    LocalTime start = LocalTime.parse(parts[0].trim(), TIME_FMT);
	    LocalTime end = LocalTime.parse(parts[1].trim(), TIME_FMT);
	    LocalTime simTime = SimulationTime.now().toLocalTime();

	    if (start.isAfter(end)) {
		return !simTime.isBefore(start) || !simTime.isAfter(end);
	    } else {
		return !simTime.isBefore(start) && !simTime.isAfter(end);
	    }
	} catch (Exception e) {
	    LOG.warn("Could not parse time window: {}", window, e);
	    return false;
	}
    }

    public boolean isWithinDreamWindow(String startStr, String endStr) {
	try {
	    LocalTime start = LocalTime.parse(startStr, TIME_FMT);
	    LocalTime end = LocalTime.parse(endStr, TIME_FMT);
	    LocalTime simTime = SimulationTime.now().toLocalTime();

	    if (start.isAfter(end)) {
		return !simTime.isBefore(start) || !simTime.isAfter(end);
	    } else {
		return !simTime.isBefore(start) && !simTime.isAfter(end);
	    }
	} catch (Exception e) {
	    return false;
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

    /** Restore last ponder tick from persisted index (called on restart). */
    public void restoreLastPonderTick(String agentName, int tick) {
	lastPonderTick.put(agentName, tick);
    }

    private static double clamp(double value, double min, double max) {
	return Math.max(min, Math.min(max, value));
    }
}
