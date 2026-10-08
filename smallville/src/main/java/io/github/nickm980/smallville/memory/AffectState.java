package io.github.nickm980.smallville.memory;

import java.util.ArrayList;
import java.util.List;

public class AffectState {
    private String moodLabel = "steady";
    private double valence;
    private double activation;
    private double socialDrive;
    private String focusTarget = "";
    private List<String> drivers = new ArrayList<String>();
    private int updatedAtTick;

    public String getMoodLabel() {
	return moodLabel;
    }

    public void setMoodLabel(String moodLabel) {
	this.moodLabel = moodLabel;
    }

    public double getValence() {
	return valence;
    }

    public void setValence(double valence) {
	this.valence = valence;
    }

    public double getActivation() {
	return activation;
    }

    public void setActivation(double activation) {
	this.activation = activation;
    }

    public double getSocialDrive() {
	return socialDrive;
    }

    public void setSocialDrive(double socialDrive) {
	this.socialDrive = socialDrive;
    }

    public String getFocusTarget() {
	return focusTarget;
    }

    public void setFocusTarget(String focusTarget) {
	this.focusTarget = focusTarget;
    }

    public List<String> getDrivers() {
	return drivers;
    }

    public void setDrivers(List<String> drivers) {
	this.drivers = drivers == null ? new ArrayList<String>() : new ArrayList<String>(drivers);
    }

    public int getUpdatedAtTick() {
	return updatedAtTick;
    }

    public void setUpdatedAtTick(int updatedAtTick) {
	this.updatedAtTick = updatedAtTick;
    }
}
