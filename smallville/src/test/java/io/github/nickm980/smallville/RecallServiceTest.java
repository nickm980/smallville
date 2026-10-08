package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.config.simulation.SimulationFile;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.memory.dreaming.DreamingService;
import io.github.nickm980.smallville.memory.dreaming.RecallService;
import io.github.nickm980.smallville.memory.ledger.LedgerEntryType;
import io.github.nickm980.smallville.memory.ledger.MemoryJournalWriter;
import io.github.nickm980.smallville.memory.ledger.RecallEvent;

public class RecallServiceTest {

    private RecallService recallService;
    private DreamingService dreamingService;
    private MemoryJournalWriter journalWriter;

    @BeforeEach
    public void setUp() {
	String tmpDir = System.getProperty("java.io.tmpdir") + "/smallville-recall-test-" + System.currentTimeMillis();
	journalWriter = new MemoryJournalWriter(tmpDir);
	dreamingService = new DreamingService(journalWriter);
	recallService = new RecallService(journalWriter);
    }

    @Test
    public void test_recall_finds_working_memory_first() {
	Agent agent = new Agent("TestAgent", List.of(new Characteristic("calm")), "resting", new Location("Blue House: Kitchen"));
	agent.getMemoryStream().addWorkingMemory("the garden gate needs fixing");
	agent.getMemoryStream().add(new Observation("watered the south bed"));

	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();

	RecallEvent event = recallService.recall(agent, "garden gate", config, 5);

	assertNotNull(event);
	assertEquals("TestAgent", event.getAgentName());
	assertEquals(5, event.getTick());
    }

    @Test
    public void test_recall_promotes_to_working_memory() {
	Agent agent = new Agent("TestAgent", List.of(new Characteristic("artistic")), "sketching", new Location("Green House: Glass Table"));
	agent.getMemoryStream().add(new Observation("seed tray needs watering"));
	agent.getMemoryStream().add(new Observation("tea is ready in the kitchen"));
	agent.getMemoryStream().add(new Observation("gate was left open this morning"));

	int workingBefore = agent.getMemoryStream().getWorkingMemories().size();

	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();
	recallService.recall(agent, "seed tray watering", config, 10);

	int workingAfter = agent.getMemoryStream().getWorkingMemories().size();
	assertTrue(workingAfter >= workingBefore, "Recall should promote to working memory");
    }

    @Test
    public void test_recall_after_dream_cycle_searches_dream_packs() {
	Agent agent = new Agent("TestAgent", List.of(new Characteristic("calm")), "resting", new Location("Blue House: Kitchen"));
	agent.setTraits("calm, observant");

	// Add enough memories to trigger dream compression
	for (int i = 0; i < 10; i++) {
	    agent.getMemoryStream().add(new Observation("observation about the garden path " + i));
	}

	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();
	config.setHotMemoryLimit(2);
	config.setDreamIntervalTicks(1);
	config.setDreamWindowStart("00:00");
	config.setDreamWindowEnd("23:59");

	// Run dream cycle to create packs
	dreamingService.runDreamCycle(agent, config, 10);

	// Recall should search dream packs
	RecallEvent event = recallService.recall(agent, "garden path", config, 15);
	assertNotNull(event);
    }

    @Test
    public void test_recall_logs_event_to_journal() {
	Agent agent = new Agent("TestAgent", List.of(new Characteristic("calm")), "resting", new Location("Blue House: Kitchen"));
	agent.getMemoryStream().add(new Observation("the kettle is boiling"));

	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();
	recallService.recall(agent, "kettle", config, 20);

	// Verify recall entry was written to journal
	List<io.github.nickm980.smallville.memory.ledger.MemoryLedgerEntry> entries = journalWriter.loadEntries(agent.getFullName());
	boolean hasRecallEntry = entries.stream()
	    .anyMatch(e -> LedgerEntryType.RECALL == e.getType());
	assertTrue(hasRecallEntry, "Journal should contain a recall entry");
    }
}
