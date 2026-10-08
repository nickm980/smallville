package io.github.nickm980.smallville.config.simulation;

import java.util.List;

public class SimulationFile {

    private WorldSeed world;
    private List<AgentSeed> agents;
    private WorldBuildingSeed worldBuilding;
    private DailyRhythmSeed dailyRhythm;
    private MemorySeed memory;

    public WorldSeed getWorld() {
	return world;
    }

    public void setWorld(WorldSeed world) {
	this.world = world;
    }

    public List<AgentSeed> getAgents() {
	return agents;
    }

    public void setAgents(List<AgentSeed> agents) {
	this.agents = agents;
    }

    public WorldBuildingSeed getWorldBuilding() {
	return worldBuilding;
    }

    public void setWorldBuilding(WorldBuildingSeed worldBuilding) {
	this.worldBuilding = worldBuilding;
    }

    public DailyRhythmSeed getDailyRhythm() {
	return dailyRhythm;
    }

    public void setDailyRhythm(DailyRhythmSeed dailyRhythm) {
	this.dailyRhythm = dailyRhythm;
    }

    public MemorySeed getMemory() {
	return memory;
    }

    public void setMemory(MemorySeed memory) {
	this.memory = memory;
    }

    public static class WorldSeed {
	private List<LocationSeed> locations;

	public List<LocationSeed> getLocations() {
	    return locations;
	}

	public void setLocations(List<LocationSeed> locations) {
	    this.locations = locations;
	}
    }

    public static class LocationSeed {
	private String name;
	private String state;
	private List<LocationSeed> objects;

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getState() {
	    return state;
	}

	public void setState(String state) {
	    this.state = state;
	}

	public List<LocationSeed> getObjects() {
	    return objects;
	}

	public void setObjects(List<LocationSeed> objects) {
	    this.objects = objects;
	}
    }

    public static class AgentSeed {
	private String name;
	private List<String> memories;
	private List<String> persona;
	private List<String> workingMemories;
	private List<String> goals;
	private List<String> rituals;
	private String activity;
	private String location;
	private String traits;
	private String model;
	private String socialPreference;
	private boolean canProposeWorldChanges;

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public List<String> getMemories() {
	    return memories;
	}

	public void setMemories(List<String> memories) {
	    this.memories = memories;
	}

	public List<String> getPersona() {
	    return persona;
	}

	public void setPersona(List<String> persona) {
	    this.persona = persona;
	}

	public List<String> getWorkingMemories() {
	    return workingMemories;
	}

	public void setWorkingMemories(List<String> workingMemories) {
	    this.workingMemories = workingMemories;
	}

	public List<String> getGoals() {
	    return goals;
	}

	public void setGoals(List<String> goals) {
	    this.goals = goals;
	}

	public List<String> getRituals() {
	    return rituals;
	}

	public void setRituals(List<String> rituals) {
	    this.rituals = rituals;
	}

	public String getActivity() {
	    return activity;
	}

	public void setActivity(String activity) {
	    this.activity = activity;
	}

	public String getLocation() {
	    return location;
	}

	public void setLocation(String location) {
	    this.location = location;
	}

	public String getTraits() {
	    return traits;
	}

	public void setTraits(String traits) {
	    this.traits = traits;
	}

	public String getModel() {
	    return model;
	}

	public void setModel(String model) {
	    this.model = model;
	}

	public String getSocialPreference() {
	    return socialPreference;
	}

	public void setSocialPreference(String socialPreference) {
	    this.socialPreference = socialPreference;
	}

	public boolean isCanProposeWorldChanges() {
	    return canProposeWorldChanges;
	}

	public void setCanProposeWorldChanges(boolean canProposeWorldChanges) {
	    this.canProposeWorldChanges = canProposeWorldChanges;
	}
    }

    public static class WorldBuildingSeed {
	private String summary;
	private List<String> rules;
	private List<String> allowedLocationStates;

	public String getSummary() {
	    return summary;
	}

	public void setSummary(String summary) {
	    this.summary = summary;
	}

	public List<String> getRules() {
	    return rules;
	}

	public void setRules(List<String> rules) {
	    this.rules = rules;
	}

	public List<String> getAllowedLocationStates() {
	    return allowedLocationStates;
	}

	public void setAllowedLocationStates(List<String> allowedLocationStates) {
	    this.allowedLocationStates = allowedLocationStates;
	}
    }

    public static class DailyRhythmSeed {
	private String breakfast = "06:00-09:30";
	private String lunch = "12:00-14:00";
	private String dinner = "18:00-20:30";
	private String morningTea = "06:00-10:00";
	private String afternoonTea = "15:00-17:30";
	private String eveningWind = null;
	private String snack = "Flexible morning or afternoon";

	public String getBreakfast() {
	    return breakfast;
	}

	public void setBreakfast(String breakfast) {
	    this.breakfast = breakfast;
	}

	public String getLunch() {
	    return lunch;
	}

	public void setLunch(String lunch) {
	    this.lunch = lunch;
	}

	public String getDinner() {
	    return dinner;
	}

	public void setDinner(String dinner) {
	    this.dinner = dinner;
	}

	public String getMorningTea() {
	    return morningTea;
	}

	public void setMorningTea(String morningTea) {
	    this.morningTea = morningTea;
	}

	public String getAfternoonTea() {
	    return afternoonTea;
	}

	public void setAfternoonTea(String afternoonTea) {
	    this.afternoonTea = afternoonTea;
	}

	public String getEveningWind() {
	    return eveningWind;
	}

	public void setEveningWind(String eveningWind) {
	    this.eveningWind = eveningWind;
	}

	public String getSnack() {
	    return snack;
	}

	public void setSnack(String snack) {
	    this.snack = snack;
	}
    }

    public static class MemorySeed {
	private int dreamIntervalTicks = 10;
	private String dreamWindowStart = "22:00";
	private String dreamWindowEnd = "06:00";
	private int hotMemoryLimit = 20;
	private int recallTopK = 5;
	private boolean archiveCompression = true;
	private boolean ponderEnabled = false;
	private double ponderBlendAlpha = 0.6;
	private int ponderCooldownTicks = 5;
	private double eveningSocialBoost = 0.2;
	private double eveningActivationDamp = 0.1;

	public int getDreamIntervalTicks() {
	    return dreamIntervalTicks;
	}

	public void setDreamIntervalTicks(int dreamIntervalTicks) {
	    this.dreamIntervalTicks = dreamIntervalTicks;
	}

	public String getDreamWindowStart() {
	    return dreamWindowStart;
	}

	public void setDreamWindowStart(String dreamWindowStart) {
	    this.dreamWindowStart = dreamWindowStart;
	}

	public String getDreamWindowEnd() {
	    return dreamWindowEnd;
	}

	public void setDreamWindowEnd(String dreamWindowEnd) {
	    this.dreamWindowEnd = dreamWindowEnd;
	}

	public int getHotMemoryLimit() {
	    return hotMemoryLimit;
	}

	public void setHotMemoryLimit(int hotMemoryLimit) {
	    this.hotMemoryLimit = hotMemoryLimit;
	}

	public int getRecallTopK() {
	    return recallTopK;
	}

	public void setRecallTopK(int recallTopK) {
	    this.recallTopK = recallTopK;
	}

	public boolean isArchiveCompression() {
	    return archiveCompression;
	}

	public void setArchiveCompression(boolean archiveCompression) {
	    this.archiveCompression = archiveCompression;
	}

	public boolean isPonderEnabled() {
	    return ponderEnabled;
	}

	public void setPonderEnabled(boolean ponderEnabled) {
	    this.ponderEnabled = ponderEnabled;
	}

	public double getPonderBlendAlpha() {
	    return ponderBlendAlpha;
	}

	public void setPonderBlendAlpha(double ponderBlendAlpha) {
	    this.ponderBlendAlpha = ponderBlendAlpha;
	}

	public int getPonderCooldownTicks() {
	    return ponderCooldownTicks;
	}

	public void setPonderCooldownTicks(int ponderCooldownTicks) {
	    this.ponderCooldownTicks = ponderCooldownTicks;
	}

	public double getEveningSocialBoost() {
	    return eveningSocialBoost;
	}

	public void setEveningSocialBoost(double eveningSocialBoost) {
	    this.eveningSocialBoost = eveningSocialBoost;
	}

	public double getEveningActivationDamp() {
	    return eveningActivationDamp;
	}

	public void setEveningActivationDamp(double eveningActivationDamp) {
	    this.eveningActivationDamp = eveningActivationDamp;
	}
    }
}
