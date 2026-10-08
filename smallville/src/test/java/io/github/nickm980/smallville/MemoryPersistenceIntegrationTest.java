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
import io.github.nickm980.smallville.memory.ledger.LedgerEntryType;
import io.github.nickm980.smallville.memory.ledger.MemoryJournalWriter;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerEntry;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerIndex;

/**
 * Integration tests verifying persistence round-trips:
 * dream cycle writes → journal files → load-back.
 */
public class MemoryPersistenceIntegrationTest {

    private MemoryJournalWriter journalWriter;
    private DreamingService dreamingService;

    @BeforeEach
    public void setUp() {
	String tmpRoot = System.getProperty("java.io.tmpdir") + "/smallville-persist-test-" + System.currentTimeMillis();
	journalWriter = new MemoryJournalWriter(tmpRoot);
	dreamingService = new DreamingService(journalWriter);
    }

    @Test
    public void dream_cycle_persists_and_reloads_index() {
	Agent agent = new Agent("PersistAgent", List.of(new Characteristic("steady")), "idle", new Location("Blue House: Kitchen"));
	agent.setTraits("steady, thoughtful");
	for (int i = 0; i < 6; i++) {
	    Observation obs = new Observation("daily task " + i);
	    obs.setImportance(4 + i);
	    agent.getMemoryStream().add(obs);
	}

	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();
	config.setHotMemoryLimit(2);
	config.setDreamIntervalTicks(1);
	config.setDreamWindowStart("00:00");
	config.setDreamWindowEnd("23:59");

	boolean ran = dreamingService.runDreamCycle(agent, config, 10);
	assertTrue(ran, "Dream cycle should have run");

	// Reload from disk using a fresh writer on the same root
	MemoryJournalWriter reloadWriter = new MemoryJournalWriter(getRootPath());
	MemoryLedgerIndex index = reloadWriter.loadOrCreateIndex(agent.getFullName());
	assertEquals(10, index.getLastDreamTick());
	assertTrue(index.getDreamPackCount() > 0);
	assertNotNull(index.getLastAffect());
    }

    @Test
    public void dream_cycle_persists_and_reloads_dream_packs() {
	Agent agent = new Agent("DreamAgent", List.of(new Characteristic("creative")), "idle", new Location("Green House: Studio"));
	agent.setTraits("creative");
	for (int i = 0; i < 7; i++) {
	    agent.getMemoryStream().add(new Observation("inspiration " + i));
	}

	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();
	config.setHotMemoryLimit(2);
	config.setDreamIntervalTicks(1);
	config.setDreamWindowStart("00:00");
	config.setDreamWindowEnd("23:59");

	dreamingService.runDreamCycle(agent, config, 20);

	List<DreamPack> packs = journalWriter.loadDreamPacks(agent.getFullName());
	assertFalse(packs.isEmpty(), "Should have at least one dream pack on disk");
	DreamPack pack = packs.get(0);
	assertNotNull(pack.getSummaryText());
	assertFalse(pack.getSourceHashes().isEmpty());
	assertEquals(agent.getFullName(), pack.getAgentName());
    }

    @Test
    public void dream_cycle_writes_ledger_entries() {
	Agent agent = new Agent("LedgerAgent", List.of(new Characteristic("disciplined")), "working", new Location("Blue House: Study"));
	agent.setTraits("disciplined");
	for (int i = 0; i < 5; i++) {
	    agent.getMemoryStream().add(new Observation("journal event " + i));
	}

	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();
	config.setHotMemoryLimit(2);
	config.setDreamIntervalTicks(1);
	config.setDreamWindowStart("00:00");
	config.setDreamWindowEnd("23:59");

	dreamingService.runDreamCycle(agent, config, 15);

	List<MemoryLedgerEntry> entries = journalWriter.loadEntries(agent.getFullName());
	assertFalse(entries.isEmpty(), "Should have at least one ledger entry");
	boolean hasDreamCompress = entries.stream().anyMatch(e -> LedgerEntryType.DREAM_COMPRESS == e.getType());
	assertTrue(hasDreamCompress, "Should have a dream-compress entry");
    }

    @Test
    public void affect_state_survives_serialization_round_trip() {
	AffectState affect = AffectState.fromDreamCycle(8.0, 0.4, "watered the garden", List.of("routine", "gardening"), 25);

	MemoryLedgerIndex index = new MemoryLedgerIndex("RoundTripAgent");
	index.setTotalEvents(5);
	index.setLastAffect(affect);
	journalWriter.writeIndex("RoundTripAgent", index);

	MemoryLedgerIndex loaded = journalWriter.loadOrCreateIndex("RoundTripAgent");
	assertNotNull(loaded.getLastAffect());
	assertEquals("content", loaded.getLastAffect().getMoodLabel());
	assertEquals(25, loaded.getLastAffect().getUpdatedAtTick());
	assertEquals(5, loaded.getTotalEvents());
    }

    private String getRootPath() {
	// Extract the root path from the journalWriter's tmp directory
	// The root is the same directory used by setUp
	try {
	    java.lang.reflect.Field f = MemoryJournalWriter.class.getDeclaredField("rootPath");
	    f.setAccessible(true);
	    return ((java.nio.file.Path) f.get(journalWriter)).toString();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }
}
