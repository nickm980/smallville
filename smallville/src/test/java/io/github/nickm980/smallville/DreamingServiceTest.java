package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.config.simulation.SimulationFile;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.AffectState;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.memory.dreaming.DreamingService;
import io.github.nickm980.smallville.memory.ledger.DreamPack;
import io.github.nickm980.smallville.memory.ledger.MemoryJournalWriter;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerIndex;

public class DreamingServiceTest {

    private DreamingService dreamingService;
    private MemoryJournalWriter journalWriter;

    @BeforeEach
    public void setUp() {
	journalWriter = new MemoryJournalWriter(
	    System.getProperty("java.io.tmpdir") + "/smallville-test-" + System.currentTimeMillis()
	);
	dreamingService = new DreamingService(journalWriter);
    }

    @Test
    public void test_dream_cycle_creates_dream_pack_when_enough_memories() {
	Agent agent = new Agent("TestAgent", List.of(new Characteristic("calm")), "resting", new Location("Blue House: Kitchen"));
	agent.setTraits("calm, observant");
	agent.getMemoryStream().add(new Observation("watered the garden"));
	agent.getMemoryStream().add(new Observation("brewed morning tea"));
	agent.getMemoryStream().add(new Observation("checked the gate"));
	agent.getMemoryStream().add(new Observation("sketched a planting layout"));
	agent.getMemoryStream().add(new Observation("reviewed tray notes"));

	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();
	config.setHotMemoryLimit(2);
	config.setDreamIntervalTicks(1);
	config.setDreamWindowStart("00:00");
	config.setDreamWindowEnd("23:59");

	dreamingService.runDreamCycle(agent, config, 10);

	MemoryLedgerIndex index = journalWriter.loadOrCreateIndex(agent.getFullName());
	assertTrue(index.getDreamPackCount() > 0, "Should have created at least one dream pack");
	assertEquals(10, index.getLastDreamTick());
    }

    @Test
    public void test_dream_cycle_updates_affect_state() {
	Agent agent = new Agent("TestAgent", List.of(new Characteristic("calm")), "resting", new Location("Blue House: Kitchen"));
	agent.setTraits("calm, observant");
	for (int i = 0; i < 5; i++) {
	    Observation obs = new Observation("memory " + i);
	    obs.setImportance(6);
	    agent.getMemoryStream().add(obs);
	}

	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();
	config.setHotMemoryLimit(3);
	config.setDreamIntervalTicks(1);
	config.setDreamWindowStart("00:00");
	config.setDreamWindowEnd("23:59");

	dreamingService.runDreamCycle(agent, config, 5);

	AffectState affect = agent.getAffect();
	assertNotNull(affect);
	assertEquals(5, affect.getUpdatedAtTick());
	assertNotNull(affect.getMoodLabel());
	assertFalse(affect.getMoodLabel().isEmpty());
    }

    @Test
    public void test_hot_cold_partitioning_respects_limit() {
	Agent agent = new Agent("TestAgent", List.of(new Characteristic("grounded")), "resting", new Location("Blue House: Kitchen"));
	agent.setTraits("grounded");

	// Add 10 observations with varying importance
	for (int i = 0; i < 10; i++) {
	    Observation obs = new Observation("observation " + i);
	    obs.setImportance(i);
	    agent.getMemoryStream().add(obs);
	}

	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();
	config.setHotMemoryLimit(3);
	config.setDreamIntervalTicks(1);
	config.setDreamWindowStart("00:00");
	config.setDreamWindowEnd("23:59");

	dreamingService.runDreamCycle(agent, config, 20);

	MemoryLedgerIndex index = journalWriter.loadOrCreateIndex(agent.getFullName());
	assertTrue(index.getHotCount() <= 3, "Hot count should not exceed hotMemoryLimit");
    }

    @Test
    public void test_dream_pack_contains_source_hashes() {
	Agent agent = new Agent("TestAgent", List.of(new Characteristic("artistic")), "working", new Location("Green House: Potting Bench"));
	agent.setTraits("artistic");
	for (int i = 0; i < 8; i++) {
	    agent.getMemoryStream().add(new Observation("cold memory " + i));
	}

	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();
	config.setHotMemoryLimit(2);
	config.setDreamIntervalTicks(1);
	config.setDreamWindowStart("00:00");
	config.setDreamWindowEnd("23:59");

	dreamingService.runDreamCycle(agent, config, 30);

	List<DreamPack> packs = journalWriter.loadDreamPacks(agent.getFullName());
	assertFalse(packs.isEmpty(), "Should have dream packs");
	DreamPack pack = packs.get(0);
	assertNotNull(pack.getSourceHashes());
	assertFalse(pack.getSourceHashes().isEmpty());
	assertNotNull(pack.getSummaryText());
	assertTrue(pack.getCompressedEntryCount() > 0);
    }

    @Test
    public void test_no_dream_cycle_when_insufficient_memories() {
	Agent agent = new Agent("TestAgent", List.of(new Characteristic("calm")), "resting", new Location("Blue House: Kitchen"));
	agent.setTraits("calm");
	// Only working memories, no observations to dream about

	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();
	config.setHotMemoryLimit(20);
	config.setDreamIntervalTicks(1);
	config.setDreamWindowStart("00:00");
	config.setDreamWindowEnd("23:59");

	dreamingService.runDreamCycle(agent, config, 5);

	MemoryLedgerIndex index = journalWriter.loadOrCreateIndex(agent.getFullName());
	assertEquals(0, index.getDreamPackCount());
    }
}
