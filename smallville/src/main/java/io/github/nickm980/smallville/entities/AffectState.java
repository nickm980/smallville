package io.github.nickm980.smallville.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Basic affect state for an agent. Dream-cycle derivation with optional
 * daytime ponder nudges — valence/activation/socialDrive are computed from
 * memory scores during the dream cycle and gently adjusted during rest.
 */
public class AffectState {

    private String moodLabel;
    private double valence;       // -1.0 .. 1.0
    private double activation;    //  0.0 .. 1.0
    private double socialDrive;   //  0.0 .. 1.0
    private String focusTarget;   // nullable
    private List<String> drivers;
    private int updatedAtTick;

    /** No-arg constructor for Jackson deserialization. */
    AffectState() {
	this.drivers = new ArrayList<>();
    }

    public AffectState(String moodLabel, double valence, double activation, double socialDrive,
		       String focusTarget, List<String> drivers, int updatedAtTick) {
	this.moodLabel = moodLabel;
	this.valence = clamp(valence, -1.0, 1.0);
	this.activation = clamp(activation, 0.0, 1.0);
	this.socialDrive = clamp(socialDrive, 0.0, 1.0);
	this.focusTarget = focusTarget;
	this.drivers = drivers == null ? new ArrayList<>() : new ArrayList<>(drivers);
	this.updatedAtTick = updatedAtTick;
    }

    /** Neutral baseline — used on fresh agents before any dream cycle runs. */
    public static AffectState neutral(int tick) {
	return new AffectState("neutral", 0.0, 0.5, 0.5, null, List.of(), tick);
    }

    /**
     * Derive affect from dream-cycle statistics.
     *
     * @param avgImportance    average importance of hot memories (0..10 scale)
     * @param socialMemoryRatio fraction of hot memories that mention social interactions (0..1)
     * @param topFocusTarget    description of the highest-scored hot memory (nullable)
     * @param driverDescriptions short descriptions that contributed to this state
     * @param tick              current simulation tick
     */
    public static AffectState fromDreamCycle(double avgImportance, double socialMemoryRatio,
					     String topFocusTarget, List<String> driverDescriptions, int tick) {
	// valence: map avg importance (0..10) to (-1..1) — higher importance = more positive
	double valence = clamp((avgImportance / 5.0) - 1.0, -1.0, 1.0);

	// activation: higher importance = more activated
	double activation = clamp(avgImportance / 10.0, 0.0, 1.0);

	// socialDrive: directly from social memory ratio
	double socialDrive = clamp(socialMemoryRatio, 0.0, 1.0);

	String mood = moodFromValence(valence);

	return new AffectState(mood, valence, activation, socialDrive, topFocusTarget, driverDescriptions, tick);
    }

    /** Simple valence-to-mood bucket mapping. */
    public static String moodFromValence(double valence) {
	if (valence >= 0.5) return "content";
	if (valence >= 0.1) return "calm";
	if (valence >= -0.1) return "neutral";
	if (valence >= -0.5) return "uneasy";
	return "distressed";
    }

    /**
     * Create a nudged copy of this affect state by blending the current valence
     * with a ponder-derived valence.  Preserves activation, socialDrive, drivers,
     * and focusTarget from the original.
     *
     * @param ponderValence the valence derived from the ponder cycle
     * @param alpha         blend weight for the EXISTING affect (0..1); (1-alpha) goes to ponder
     * @param tick          the tick at which the nudge occurs
     * @return a new AffectState with blended valence and recalculated mood
     */
    public AffectState withNudge(double ponderValence, double alpha, int tick) {
	double blended = clamp(alpha * this.valence + (1.0 - alpha) * ponderValence, -1.0, 1.0);
	String mood = moodFromValence(blended);
	return new AffectState(mood, blended, this.activation, this.socialDrive,
			       this.focusTarget, this.drivers, tick);
    }

    /**
     * Create a nudged copy with evening-wind social modulation.
     * In addition to valence blending, reduces activation (task wind-down)
     * and boosts socialDrive (social availability before sleep).
     *
     * @param ponderValence the valence derived from the ponder cycle
     * @param alpha         blend weight for the EXISTING affect (0..1)
     * @param socialBoost   amount to add to socialDrive (typically 0.1-0.3)
     * @param activationDamp amount to subtract from activation (typically 0.05-0.15)
     * @param tick          the tick at which the nudge occurs
     * @return a new AffectState with blended valence, boosted socialDrive, and damped activation
     */
    public AffectState withEveningNudge(double ponderValence, double alpha,
					double socialBoost, double activationDamp, int tick) {
	double blended = clamp(alpha * this.valence + (1.0 - alpha) * ponderValence, -1.0, 1.0);
	double newActivation = clamp(this.activation - activationDamp, 0.0, 1.0);
	double newSocialDrive = clamp(this.socialDrive + socialBoost, 0.0, 1.0);
	String mood = moodFromValence(blended);
	return new AffectState(mood, blended, newActivation, newSocialDrive,
			       this.focusTarget, this.drivers, tick);
    }

    // --- Getters ---

    public String getMoodLabel() {
	return moodLabel;
    }

    public double getValence() {
	return valence;
    }

    public double getActivation() {
	return activation;
    }

    public double getSocialDrive() {
	return socialDrive;
    }

    public String getFocusTarget() {
	return focusTarget;
    }

    public List<String> getDrivers() {
	return Collections.unmodifiableList(drivers);
    }

    public int getUpdatedAtTick() {
	return updatedAtTick;
    }

    // --- Setters for Jackson deserialization (memory reload) ---

    public void setMoodLabel(String moodLabel) {
	this.moodLabel = moodLabel;
    }

    public void setValence(double valence) {
	this.valence = clamp(valence, -1.0, 1.0);
    }

    public void setActivation(double activation) {
	this.activation = clamp(activation, 0.0, 1.0);
    }

    public void setSocialDrive(double socialDrive) {
	this.socialDrive = clamp(socialDrive, 0.0, 1.0);
    }

    public void setFocusTarget(String focusTarget) {
	this.focusTarget = focusTarget;
    }

    public void setDrivers(List<String> drivers) {
	this.drivers = drivers == null ? new ArrayList<>() : new ArrayList<>(drivers);
    }

    public void setUpdatedAtTick(int updatedAtTick) {
	this.updatedAtTick = updatedAtTick;
    }

    private static double clamp(double value, double min, double max) {
	return Math.max(min, Math.min(max, value));
    }
}
