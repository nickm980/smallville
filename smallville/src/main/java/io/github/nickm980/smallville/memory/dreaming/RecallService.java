package io.github.nickm980.smallville.memory.dreaming;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.nickm980.smallville.config.simulation.SimulationFile;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.SimulationTime;
import io.github.nickm980.smallville.memory.Memory;
import io.github.nickm980.smallville.memory.MemoryStream;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.memory.ledger.DreamPack;
import io.github.nickm980.smallville.memory.ledger.LedgerEntryType;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerEntry;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerIndex;
import io.github.nickm980.smallville.memory.ledger.MemoryJournalWriter;
import io.github.nickm980.smallville.memory.ledger.RecallEvent;

/**
 * Layered recall service — searches working → hot/recent → dream-pack summaries
 * → archived raw memories.  Promotes the best match into working memory.
 * <p>
 * Wired as an enhanced path alongside existing
 * {@code MemoryStream.getRelevantMemories()} — does not replace it.
 */
public class RecallService {

    private static final Logger LOG = LoggerFactory.getLogger(RecallService.class);

    private final MemoryJournalWriter journalWriter;

    public RecallService(MemoryJournalWriter journalWriter) {
	this.journalWriter = journalWriter;
    }

    /**
     * Perform a layered recall for the given query.
     *
     * @return the recall event describing what was selected
     */
    public RecallEvent recall(Agent agent, String query, SimulationFile.MemorySeed config, int currentTick) {
	if (config == null) {
	    config = new SimulationFile.MemorySeed();
	}

	int topK = config.getRecallTopK();
	MemoryStream stream = agent.getMemoryStream();

	// Layer 1: working memory
	RecallCandidate best = searchWorkingMemory(stream, query);
	if (best != null && best.score > 0.5) {
	    return finalizeRecall(agent, query, best, currentTick);
	}

	// Layer 2: hot/recent memories
	RecallCandidate hotBest = searchHotMemories(stream, query, topK);
	if (hotBest != null && (best == null || hotBest.score > best.score)) {
	    best = hotBest;
	}

	// Layer 3: dream-pack summaries
	RecallCandidate dreamBest = searchDreamPacks(agent.getFullName(), query);
	if (dreamBest != null && (best == null || dreamBest.score > best.score)) {
	    best = dreamBest;
	}

	// Layer 4: full archived memories (all non-working)
	if (best == null) {
	    RecallCandidate archiveBest = searchArchive(stream, query, topK);
	    if (archiveBest != null) {
		best = archiveBest;
	    }
	}

	if (best == null) {
	    LOG.debug("No recall match for agent={} query={}", agent.getFullName(), query);
	    return new RecallEvent(agent.getFullName(), currentTick, query, "", "archive", false);
	}

	return finalizeRecall(agent, query, best, currentTick);
    }

    private RecallEvent finalizeRecall(Agent agent, String query, RecallCandidate candidate, int tick) {
	boolean promoted = false;

	// Promote to working memory if it's a strong match and came from a deeper layer
	if (candidate.description != null && !"working".equals(candidate.sourceLayer)) {
	    agent.getMemoryStream().addWorkingMemory(candidate.description);
	    promoted = true;
	}

	String hash = MemoryLedgerEntry.sha256(candidate.description);
	RecallEvent event = new RecallEvent(
	    agent.getFullName(), tick, query, hash, candidate.sourceLayer, promoted);

	// Log to journal
	journalWriter.appendEntry(agent.getFullName(), new MemoryLedgerEntry(
	    agent.getFullName(), tick, SimulationTime.now(),
	    LedgerEntryType.RECALL, "Recall: " + query + " \u2192 " + truncate(candidate.description, 80),
	    candidate.score, candidate.sourceLayer));

	// Update index
	MemoryLedgerIndex index = journalWriter.loadOrCreateIndex(agent.getFullName());
	index.setLastRecallTick(tick);
	index.setTotalEvents(index.getTotalEvents() + 1);
	journalWriter.writeIndex(agent.getFullName(), index);

	LOG.debug("Recall for {} query='{}' → layer={} promoted={}", 
	    agent.getFullName(), query, candidate.sourceLayer, promoted);

	return event;
    }

    private RecallCandidate searchWorkingMemory(MemoryStream stream, String query) {
	return stream.getWorkingMemories().stream()
	    .map(m -> new RecallCandidate(m.getDescription(), m.getScore(query), "working"))
	    .max(Comparator.comparingDouble(c -> c.score))
	    .orElse(null);
    }

    private RecallCandidate searchHotMemories(MemoryStream stream, String query, int topK) {
	return stream.getRecentMemories().stream()
	    .map(m -> new RecallCandidate(m.getDescription(), m.getScore(query), "hot"))
	    .sorted(Comparator.comparingDouble((RecallCandidate c) -> c.score).reversed())
	    .limit(topK)
	    .findFirst()
	    .orElse(null);
    }

    private RecallCandidate searchDreamPacks(String agentName, String query) {
	List<DreamPack> packs = journalWriter.loadDreamPacks(agentName);
	if (packs.isEmpty()) {
	    return null;
	}

	RecallCandidate best = null;
	for (DreamPack pack : packs) {
	    String summary = pack.getSummaryText();
	    if (summary == null || summary.isBlank()) continue;

	    // Simple text-match score for dream summaries
	    double score = calculateSimpleRelevance(query, summary);
	    if (best == null || score > best.score) {
		best = new RecallCandidate(summary, score, "dream-summary");
	    }
	}
	return best;
    }

    private RecallCandidate searchArchive(MemoryStream stream, String query, int topK) {
	List<Memory> all = new ArrayList<>(stream.getMemories());
	return all.stream()
	    .map(m -> new RecallCandidate(m.getDescription(), m.getScore(query), "archive"))
	    .sorted(Comparator.comparingDouble((RecallCandidate c) -> c.score).reversed())
	    .limit(topK)
	    .findFirst()
	    .orElse(null);
    }

    private double calculateSimpleRelevance(String query, String text) {
	if (query == null || text == null) return 0;
	String[] queryWords = query.toLowerCase().split("\\s+");
	String lowerText = text.toLowerCase();
	int matches = 0;
	for (String word : queryWords) {
	    if (word.length() > 2 && lowerText.contains(word)) {
		matches++;
	    }
	}
	return queryWords.length > 0 ? (double) matches / queryWords.length : 0;
    }

    private String truncate(String text, int maxLen) {
	if (text == null) return "";
	return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }

    static class RecallCandidate {
	final String description;
	final double score;
	final String sourceLayer;

	RecallCandidate(String description, double score, String sourceLayer) {
	    this.description = description;
	    this.score = score;
	    this.sourceLayer = sourceLayer;
	}
    }
}
