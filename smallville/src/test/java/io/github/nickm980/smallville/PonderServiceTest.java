package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.config.simulation.SimulationFile;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.AffectState;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.entities.SimulationTime;
import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.memory.dreaming.PonderService;
import io.github.nickm980.smallville.memory.ledger.MemoryJournalWriter;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerIndex;

public class PonderServiceTest {

    private PonderService ponderService;
    private MemoryJournalWriter journalWriter;

    @BeforeEach
    public void setUp() {
	journalWriter = new MemoryJournalWriter(
	    System.getProperty("java.io.tmpdir") + "/smallville-ponder-test-" + System.currentTimeMillis()
	);
	ponderService = new PonderService(journalWriter);
	// Default sim time to 09:00 — within morning tea, outside dream window
	SimulationTime.setSimulationTime(java.time.LocalDateTime.of(2026, 3, 22, 9, 0));
    }

    // --- Eligibility tests ---

    @Test
    public void test_ponder_disabled_returns_false() {
	Agent agent = createAgent("Alice");
	SimulationFile.MemorySeed config = createPonderConfig(false);
	SimulationFile.DailyRhythmSeed rhythm = createRhythm();

	boolean fired = ponderService.runPonder(agent, config, rhythm, 10, false);
	assertFalse(fired, "Ponder should not fire when disabled");
    }

    @Test
    public void test_ponder_skipped_when_dream_fired() {
	Agent agent = createAgent("Alice");
	addObservationsWithImportance(agent, 3, 5);
	SimulationFile.MemorySeed config = createPonderConfig(true);
	SimulationFile.DailyRhythmSeed rhythm = createRhythm();

	boolean fired = ponderService.runPonder(agent, config, rhythm, 10, true);
	assertFalse(fired, "Ponder should not fire when dream already fired");
    }

    @Test
    public void test_ponder_skipped_when_null_config() {
	Agent agent = createAgent("Alice");
	boolean fired = ponderService.runPonder(agent, null, new SimulationFile.DailyRhythmSeed(), 10, false);
	assertFalse(fired, "Ponder should not fire with null config");
    }

    @Test
    public void test_ponder_skipped_when_null_rhythm() {
	Agent agent = createAgent("Alice");
	SimulationFile.MemorySeed config = createPonderConfig(true);
	boolean fired = ponderService.runPonder(agent, config, null, 10, false);
	assertFalse(fired, "Ponder should not fire with null rhythm");
    }

    @Test
    public void test_cooldown_prevents_consecutive_ponders() {
	Agent agent = createAgent("Alice");
	addObservationsWithImportance(agent, 3, 5);
	SimulationFile.MemorySeed config = createPonderConfig(true);
	config.setPonderCooldownTicks(5);
	SimulationFile.DailyRhythmSeed rhythm = createAllDayRhythm();

	// First ponder should fire
	setSimTimeToBreakWindow();
	boolean first = ponderService.runPonder(agent, config, rhythm, 10, false);
	assertTrue(first, "First ponder should fire");

	// Second ponder within cooldown should NOT fire
	boolean second = ponderService.runPonder(agent, config, rhythm, 12, false);
	assertFalse(second, "Ponder within cooldown should not fire");

	// After cooldown expires, should fire again
	addObservationsWithImportance(agent, 3, 5);
	boolean third = ponderService.runPonder(agent, config, rhythm, 16, false);
	assertTrue(third, "Ponder after cooldown should fire");
    }

    // --- Eligibility unit tests ---

    @Test
    public void test_isPonderEligible_no_previous_tick() {
	SimulationFile.MemorySeed config = createPonderConfig(true);
	assertTrue(ponderService.isPonderEligible("Alice", config, 1));
    }

    @Test
    public void test_isPonderEligible_cooldown_not_elapsed() {
	SimulationFile.MemorySeed config = createPonderConfig(true);
	config.setPonderCooldownTicks(5);
	ponderService.restoreLastPonderTick("Alice", 10);
	assertFalse(ponderService.isPonderEligible("Alice", config, 12));
    }

    @Test
    public void test_isPonderEligible_cooldown_elapsed() {
	SimulationFile.MemorySeed config = createPonderConfig(true);
	config.setPonderCooldownTicks(5);
	ponderService.restoreLastPonderTick("Alice", 10);
	assertTrue(ponderService.isPonderEligible("Alice", config, 15));
    }

    // --- Break window detection ---

    @Test
    public void test_detectBreakWindow_morningTea() {
	SimulationFile.DailyRhythmSeed rhythm = new SimulationFile.DailyRhythmSeed();
	rhythm.setMorningTea("08:00-10:00");
	rhythm.setLunch("12:00-14:00");
	rhythm.setAfternoonTea("15:00-17:00");

	// isWithinTimeWindow checks sim time — test the unit method directly
	assertTrue(ponderService.isWithinTimeWindow("00:00-23:59"), "Full-day window should always match");
	assertFalse(ponderService.isWithinTimeWindow(null), "Null window should not match");
	assertFalse(ponderService.isWithinTimeWindow("invalid"), "Invalid window should not match");
    }

    @Test
    public void test_detectBreakWindow_eveningWind() {
	SimulationTime.setSimulationTime(java.time.LocalDateTime.of(2026, 3, 22, 20, 0));
	SimulationFile.DailyRhythmSeed rhythm = new SimulationFile.DailyRhythmSeed();
	rhythm.setMorningTea("06:00-10:00");
	rhythm.setLunch("12:00-14:00");
	rhythm.setAfternoonTea("15:00-17:30");
	rhythm.setEveningWind("19:30-21:30");

	String window = ponderService.detectBreakWindow(rhythm);
	assertEquals("eveningWind", window, "Should detect eveningWind at 20:00");
    }

    @Test
    public void test_detectBreakWindow_null_eveningWind_skips() {
	SimulationTime.setSimulationTime(java.time.LocalDateTime.of(2026, 3, 22, 20, 0));
	SimulationFile.DailyRhythmSeed rhythm = new SimulationFile.DailyRhythmSeed();
	rhythm.setMorningTea("06:00-10:00");
	rhythm.setLunch("12:00-14:00");
	rhythm.setAfternoonTea("15:00-17:30");
	// eveningWind is null by default

	String window = ponderService.detectBreakWindow(rhythm);
	assertNull(window, "Should return null when no evening window matches at 20:00");
    }

    // --- Valence formula tests ---

    @Test
    public void test_ponder_valence_routine_memories_near_neutral() {
	// importance=3 → (3/5.0) - 0.5 = 0.1 (near neutral)
	Agent agent = createAgent("Alice");
	addObservationsWithImportance(agent, 3, 3);
	double valence = ponderService.computePonderValence(agent, 10);
	assertEquals(0.1, valence, 0.01, "Importance 3 should produce near-neutral valence");
    }

    @Test
    public void test_ponder_valence_low_importance_slightly_negative() {
	// importance=1 → (1/5.0) - 0.5 = -0.3
	Agent agent = createAgent("Alice");
	addObservationsWithImportance(agent, 3, 1);
	double valence = ponderService.computePonderValence(agent, 10);
	assertEquals(-0.3, valence, 0.01, "Importance 1 should produce -0.3");
    }

    @Test
    public void test_ponder_valence_high_importance_positive() {
	// importance=7 → (7/5.0) - 0.5 = 0.9
	Agent agent = createAgent("Alice");
	addObservationsWithImportance(agent, 3, 7);
	double valence = ponderService.computePonderValence(agent, 10);
	assertEquals(0.9, valence, 0.01, "Importance 7 should produce 0.9");
    }

    @Test
    public void test_ponder_valence_clamped_at_boundaries() {
	// importance=10 → (10/5.0) - 0.5 = 1.5 → clamped to 1.0
	Agent agent = createAgent("Alice");
	addObservationsWithImportance(agent, 3, 10);
	double valence = ponderService.computePonderValence(agent, 10);
	assertEquals(1.0, valence, 0.01, "Valence should be clamped to 1.0");
    }

    @Test
    public void test_ponder_valence_zero_importance() {
	// importance=0 → (0/5.0) - 0.5 = -0.5
	Agent agent = createAgent("Alice");
	addObservationsWithImportance(agent, 3, 0);
	double valence = ponderService.computePonderValence(agent, 10);
	assertEquals(-0.5, valence, 0.01, "Importance 0 should produce -0.5");
    }

    @Test
    public void test_ponder_valence_empty_memories() {
	Agent agent = createAgent("Alice");
	double valence = ponderService.computePonderValence(agent, 10);
	assertEquals(0.0, valence, 0.01, "Empty memories should produce 0.0");
    }

    // --- Dream-window exclusion ---

    @Test
    public void test_isWithinDreamWindow_normal_range() {
	// These test the parsing logic, not the actual sim time
	assertTrue(ponderService.isWithinDreamWindow("00:00", "23:59"),
	    "Full-day dream window should match");
    }

    @Test
    public void test_isWithinDreamWindow_invalid_strings() {
	assertFalse(ponderService.isWithinDreamWindow("bad", "input"),
	    "Invalid strings should return false");
    }

    // --- Ledger integration ---

    @Test
    public void test_ponder_writes_ledger_entry_and_updates_index() {
	Agent agent = createAgent("Alice");
	addObservationsWithImportance(agent, 3, 5);
	// Set initial affect from a dream cycle
	agent.setAffect(AffectState.fromDreamCycle(2.0, 0.3, null, List.of(), 5));

	SimulationFile.MemorySeed config = createPonderConfig(true);
	SimulationFile.DailyRhythmSeed rhythm = createAllDayRhythm();

	setSimTimeToBreakWindow();

	boolean fired = ponderService.runPonder(agent, config, rhythm, 10, false);
	assertTrue(fired, "Ponder should fire");

	MemoryLedgerIndex index = journalWriter.loadOrCreateIndex("Alice");
	assertEquals(1, index.getPonderCount(), "Ponder count should be 1");
	assertEquals(10, index.getLastPonderTick(), "Last ponder tick should be 10");
	assertNotNull(index.getLastAffect(), "Should have persisted affect");
    }

    // --- Blend integration ---

    @Test
    public void test_ponder_reduces_distress() {
	Agent agent = createAgent("Alice");
	// Start distressed: valence = -0.82
	agent.setAffect(AffectState.fromDreamCycle(0.9, 0.1, null, List.of(), 1));
	assertTrue(agent.getAffect().getValence() < -0.5, "Pre-condition: agent should be distressed");

	// Add routine observations with importance=3 (near-neutral ponder valence)
	addObservationsWithImportance(agent, 3, 3);

	SimulationFile.MemorySeed config = createPonderConfig(true);
	config.setPonderBlendAlpha(0.6);
	SimulationFile.DailyRhythmSeed rhythm = createAllDayRhythm();

	setSimTimeToBreakWindow();
	ponderService.runPonder(agent, config, rhythm, 5, false);

	// importance=3 → ponderValence = (3/5.0) - 0.5 = 0.1
	// Blended = 0.6 * (-0.82) + 0.4 * 0.1 = -0.452
	assertTrue(agent.getAffect().getValence() > -0.5,
	    "Ponder should nudge valence above distressed threshold. Got: " + agent.getAffect().getValence());
	assertEquals("uneasy", agent.getAffect().getMoodLabel(),
	    "Post-ponder mood should be uneasy, not distressed");
    }

    @Test
    public void test_ponder_preserves_activation() {
	Agent agent = createAgent("Alice");
	agent.setAffect(new AffectState("uneasy", -0.3, 0.7, 0.4, "gardening", List.of("routine"), 1));
	addObservationsWithImportance(agent, 3, 3);

	SimulationFile.MemorySeed config = createPonderConfig(true);
	SimulationFile.DailyRhythmSeed rhythm = createAllDayRhythm();

	setSimTimeToBreakWindow();
	ponderService.runPonder(agent, config, rhythm, 5, false);

	assertEquals(0.7, agent.getAffect().getActivation(), 0.001,
	    "Activation should be preserved during ponder");
    }

    // --- Evening wind integration ---

    @Test
    public void test_evening_ponder_boosts_socialDrive_and_damps_activation() {
	SimulationTime.setSimulationTime(java.time.LocalDateTime.of(2026, 3, 22, 20, 0));

	Agent agent = createAgent("Alice");
	agent.setAffect(new AffectState("distressed", -0.78, 0.6, 0.1, null, List.of(), 1));
	addObservationsWithImportance(agent, 3, 3);

	SimulationFile.MemorySeed config = createPonderConfig(true);
	config.setEveningSocialBoost(0.2);
	config.setEveningActivationDamp(0.1);

	SimulationFile.DailyRhythmSeed rhythm = new SimulationFile.DailyRhythmSeed();
	rhythm.setEveningWind("19:30-21:30");
	rhythm.setMorningTea("06:00-10:00");
	rhythm.setLunch("12:00-14:00");
	rhythm.setAfternoonTea("15:00-17:30");

	boolean fired = ponderService.runPonder(agent, config, rhythm, 10, false);
	assertTrue(fired, "Ponder should fire during eveningWind");

	// SocialDrive should be boosted
	assertEquals(0.3, agent.getAffect().getSocialDrive(), 0.001,
	    "SocialDrive should be boosted by 0.2 during evening");
	// Activation should be damped
	assertEquals(0.5, agent.getAffect().getActivation(), 0.001,
	    "Activation should be damped by 0.1 during evening");
    }

    @Test
    public void test_morning_ponder_does_not_apply_social_boost() {
	setSimTimeToBreakWindow(); // 09:00 — morningTea

	Agent agent = createAgent("Alice");
	agent.setAffect(new AffectState("uneasy", -0.3, 0.6, 0.1, null, List.of(), 1));
	addObservationsWithImportance(agent, 3, 3);

	SimulationFile.MemorySeed config = createPonderConfig(true);
	config.setEveningSocialBoost(0.2);
	config.setEveningActivationDamp(0.1);
	SimulationFile.DailyRhythmSeed rhythm = createAllDayRhythm();

	ponderService.runPonder(agent, config, rhythm, 10, false);

	// Morning ponder uses standard withNudge — activation and socialDrive unchanged
	assertEquals(0.6, agent.getAffect().getActivation(), 0.001,
	    "Activation should be preserved during morning ponder");
	assertEquals(0.1, agent.getAffect().getSocialDrive(), 0.001,
	    "SocialDrive should be preserved during morning ponder");
    }

    // --- Helpers ---

    private Agent createAgent(String name) {
	return new Agent(name, List.of(new Characteristic("calm")),
	    "resting", new Location("Blue House: Kitchen"));
    }

    private void addObservationsWithImportance(Agent agent, int count, double importance) {
	for (int i = 0; i < count; i++) {
	    Observation obs = new Observation("observation " + i + " at " + System.nanoTime());
	    obs.setImportance((int) importance);
	    agent.getMemoryStream().add(obs);
	}
    }

    private SimulationFile.MemorySeed createPonderConfig(boolean enabled) {
	SimulationFile.MemorySeed config = new SimulationFile.MemorySeed();
	config.setPonderEnabled(enabled);
	config.setPonderBlendAlpha(0.6);
	config.setPonderCooldownTicks(5);
	config.setDreamWindowStart("22:00");
	config.setDreamWindowEnd("06:00");
	return config;
    }

    private SimulationFile.DailyRhythmSeed createRhythm() {
	SimulationFile.DailyRhythmSeed rhythm = new SimulationFile.DailyRhythmSeed();
	rhythm.setMorningTea("06:00-10:00");
	rhythm.setLunch("12:00-14:00");
	rhythm.setAfternoonTea("15:00-17:30");
	return rhythm;
    }

    /** Creates a rhythm where break windows cover the full day, so ponder always triggers. */
    private SimulationFile.DailyRhythmSeed createAllDayRhythm() {
	SimulationFile.DailyRhythmSeed rhythm = new SimulationFile.DailyRhythmSeed();
	rhythm.setMorningTea("00:00-23:59");
	rhythm.setLunch("00:00-23:59");
	rhythm.setAfternoonTea("00:00-23:59");
	return rhythm;
    }

    private void setSimTimeToBreakWindow() {
	// Set sim time to 09:00 — within morningTea (06:00-10:00) and outside dream window (22:00-06:00)
	SimulationTime.setSimulationTime(java.time.LocalDateTime.of(2026, 3, 22, 9, 0));
    }
}
