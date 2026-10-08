package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.entities.AffectState;

public class AffectStateTest {

    @Test
    public void test_neutral_factory_returns_expected_defaults() {
	AffectState state = AffectState.neutral(0);

	assertEquals("neutral", state.getMoodLabel());
	assertEquals(0.0, state.getValence(), 0.001);
	assertEquals(0.5, state.getActivation(), 0.001);
	assertEquals(0.5, state.getSocialDrive(), 0.001);
	assertNull(state.getFocusTarget());
	assertNotNull(state.getDrivers());
	assertTrue(state.getDrivers().isEmpty());
	assertEquals(0, state.getUpdatedAtTick());
    }

    @Test
    public void test_neutral_at_specific_tick() {
	AffectState state = AffectState.neutral(42);
	assertEquals(42, state.getUpdatedAtTick());
    }

    @Test
    public void test_from_dream_cycle_high_importance_yields_content_mood() {
	// avgImportance=8 → valence = (8/5)-1 = 0.6 → "content"
	AffectState state = AffectState.fromDreamCycle(
	    8.0, 0.7, "focus on planting", List.of("gardening", "tea"), 10
	);

	assertEquals("content", state.getMoodLabel());
	assertTrue(state.getValence() > 0.5);
	assertTrue(state.getActivation() > 0.5);
	assertEquals(0.7, state.getSocialDrive(), 0.001);
	assertEquals("focus on planting", state.getFocusTarget());
	assertEquals(10, state.getUpdatedAtTick());
    }

    @Test
    public void test_from_dream_cycle_low_importance_yields_distressed_mood() {
	// avgImportance=0 → valence = (0/5)-1 = -1.0 → "distressed"
	AffectState state = AffectState.fromDreamCycle(
	    0.0, 0.1, null, List.of(), 5
	);

	assertEquals("distressed", state.getMoodLabel());
	assertTrue(state.getValence() <= -0.5);
	assertNull(state.getFocusTarget());
    }

    @Test
    public void test_from_dream_cycle_moderate_importance_yields_calm_mood() {
	// avgImportance=5.5 → valence = (5.5/5)-1 = 0.1 → "calm" (>=0.1)
	AffectState state = AffectState.fromDreamCycle(
	    5.5, 0.3, "reviewing notes", List.of("review"), 15
	);

	assertEquals("calm", state.getMoodLabel());
    }

    @Test
    public void test_mood_from_valence_buckets() {
	assertEquals("content", AffectState.moodFromValence(0.8));
	assertEquals("content", AffectState.moodFromValence(0.5));
	assertEquals("calm", AffectState.moodFromValence(0.3));
	assertEquals("calm", AffectState.moodFromValence(0.1));
	assertEquals("neutral", AffectState.moodFromValence(0.0));
	assertEquals("neutral", AffectState.moodFromValence(-0.05));
	assertEquals("uneasy", AffectState.moodFromValence(-0.3));
	assertEquals("uneasy", AffectState.moodFromValence(-0.5));
	assertEquals("distressed", AffectState.moodFromValence(-0.7));
	assertEquals("distressed", AffectState.moodFromValence(-1.0));
    }

    @Test
    public void test_valence_clamped_to_bounds() {
	AffectState state = new AffectState(
	    "test", 5.0, 2.0, -1.0, null, List.of(), 0
	);

	assertEquals(1.0, state.getValence(), 0.001);
	assertEquals(1.0, state.getActivation(), 0.001);
	assertEquals(0.0, state.getSocialDrive(), 0.001);
    }

    @Test
    public void test_null_drivers_defaults_to_empty_list() {
	AffectState state = new AffectState(
	    "neutral", 0.0, 0.5, 0.5, null, null, 0
	);

	assertNotNull(state.getDrivers());
	assertTrue(state.getDrivers().isEmpty());
    }

    @Test
    public void test_setters_for_jackson_deserialization() {
	AffectState state = AffectState.neutral(0);
	state.setMoodLabel("calm");
	state.setValence(0.3);
	state.setActivation(0.6);
	state.setSocialDrive(0.4);
	state.setFocusTarget("tea time");
	state.setDrivers(List.of("routine"));
	state.setUpdatedAtTick(99);

	assertEquals("calm", state.getMoodLabel());
	assertEquals(0.3, state.getValence(), 0.001);
	assertEquals(0.6, state.getActivation(), 0.001);
	assertEquals(0.4, state.getSocialDrive(), 0.001);
	assertEquals("tea time", state.getFocusTarget());
	assertEquals(List.of("routine"), state.getDrivers());
	assertEquals(99, state.getUpdatedAtTick());
    }
}
