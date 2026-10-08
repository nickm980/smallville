package io.github.nickm980.smallville.api.v1.dto;

import java.util.ArrayList;
import java.util.List;

public class WorldSnapshotResponse {

    private int step;
    private int tick;
    private String time;
    private WorldBuildRulesResponse worldBuilding = new WorldBuildRulesResponse();
    private DailyRhythmResponse dailyRhythm = new DailyRhythmResponse();
    private List<WorldLocationResponse> locations = new ArrayList<WorldLocationResponse>();
    private List<WorldAgentResponse> agents = new ArrayList<WorldAgentResponse>();
    private List<ConversationResponse> conversations = new ArrayList<ConversationResponse>();
    private List<ActionLogResponse> actionLog = new ArrayList<ActionLogResponse>();
    private List<WorldProposalResponse> pendingProposals = new ArrayList<WorldProposalResponse>();

    public int getStep() {
	return step;
    }

    public void setStep(int step) {
	this.step = step;
    }

    public int getTick() {
	return tick;
    }

    public void setTick(int tick) {
	this.tick = tick;
    }

    public String getTime() {
	return time;
    }

    public void setTime(String time) {
	this.time = time;
    }

    public WorldBuildRulesResponse getWorldBuilding() {
	return worldBuilding;
    }

    public void setWorldBuilding(WorldBuildRulesResponse worldBuilding) {
	this.worldBuilding = worldBuilding;
    }

    public DailyRhythmResponse getDailyRhythm() {
	return dailyRhythm;
    }

    public void setDailyRhythm(DailyRhythmResponse dailyRhythm) {
	this.dailyRhythm = dailyRhythm;
    }

    public List<WorldLocationResponse> getLocations() {
	return locations;
    }

    public void setLocations(List<WorldLocationResponse> locations) {
	this.locations = locations;
    }

    public List<WorldAgentResponse> getAgents() {
	return agents;
    }

    public void setAgents(List<WorldAgentResponse> agents) {
	this.agents = agents;
    }

    public List<ConversationResponse> getConversations() {
	return conversations;
    }

    public void setConversations(List<ConversationResponse> conversations) {
	this.conversations = conversations;
    }

    public List<ActionLogResponse> getActionLog() {
	return actionLog;
    }

    public void setActionLog(List<ActionLogResponse> actionLog) {
	this.actionLog = actionLog;
    }

    public List<WorldProposalResponse> getPendingProposals() {
	return pendingProposals;
    }

    public void setPendingProposals(List<WorldProposalResponse> pendingProposals) {
	this.pendingProposals = pendingProposals;
    }

    public static class DailyRhythmResponse {
	private String breakfast = "";
	private String lunch = "";
	private String dinner = "";
	private String morningTea = "";
	private String afternoonTea = "";
	private String snack = "";

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

	public String getSnack() {
	    return snack;
	}

	public void setSnack(String snack) {
	    this.snack = snack;
	}
    }

    public static class WorldBuildRulesResponse {
	private String summary = "";
	private List<String> rules = new ArrayList<String>();
	private List<String> allowedLocationStates = new ArrayList<String>();

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

    public static class WorldLocationResponse {
	private String name;
	private String parent;
	private String state;
	private int depth;
	private List<String> agents = new ArrayList<String>();

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getParent() {
	    return parent;
	}

	public void setParent(String parent) {
	    this.parent = parent;
	}

	public String getState() {
	    return state;
	}

	public void setState(String state) {
	    this.state = state;
	}

	public int getDepth() {
	    return depth;
	}

	public void setDepth(int depth) {
	    this.depth = depth;
	}

	public List<String> getAgents() {
	    return agents;
	}

	public void setAgents(List<String> agents) {
	    this.agents = agents;
	}
    }

    public static class WorldAgentResponse {
	private String name;
	private String location;
	private String action;
	private String emoji;
	private String traits;
	private String model;
	private String socialPreference;
	private boolean canProposeWorldChanges;
	private List<String> persona = new ArrayList<String>();
	private List<String> workingMemories = new ArrayList<String>();
	private List<String> recentMemories = new ArrayList<String>();
	private List<String> shortPlans = new ArrayList<String>();
	private List<String> longPlans = new ArrayList<String>();
	private List<String> goals = new ArrayList<String>();
	private List<String> rituals = new ArrayList<String>();
	private AffectSummary affect;
	private MemoryLedgerSummary memoryLedgerSummary;

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getLocation() {
	    return location;
	}

	public void setLocation(String location) {
	    this.location = location;
	}

	public String getAction() {
	    return action;
	}

	public void setAction(String action) {
	    this.action = action;
	}

	public String getEmoji() {
	    return emoji;
	}

	public void setEmoji(String emoji) {
	    this.emoji = emoji;
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

	public List<String> getRecentMemories() {
	    return recentMemories;
	}

	public void setRecentMemories(List<String> recentMemories) {
	    this.recentMemories = recentMemories;
	}

	public List<String> getShortPlans() {
	    return shortPlans;
	}

	public void setShortPlans(List<String> shortPlans) {
	    this.shortPlans = shortPlans;
	}

	public List<String> getLongPlans() {
	    return longPlans;
	}

	public void setLongPlans(List<String> longPlans) {
	    this.longPlans = longPlans;
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

	public AffectSummary getAffect() {
	    return affect;
	}

	public void setAffect(AffectSummary affect) {
	    this.affect = affect;
	}

	public MemoryLedgerSummary getMemoryLedgerSummary() {
	    return memoryLedgerSummary;
	}

	public void setMemoryLedgerSummary(MemoryLedgerSummary memoryLedgerSummary) {
	    this.memoryLedgerSummary = memoryLedgerSummary;
	}
    }

    public static class AffectSummary {
	private String moodLabel;
	private double valence;
	private double activation;

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
    }

    public static class MemoryLedgerSummary {
	private int hotCount;
	private int archivedCount;
	private int dreamPackCount;

	public int getHotCount() {
	    return hotCount;
	}

	public void setHotCount(int hotCount) {
	    this.hotCount = hotCount;
	}

	public int getArchivedCount() {
	    return archivedCount;
	}

	public void setArchivedCount(int archivedCount) {
	    this.archivedCount = archivedCount;
	}

	public int getDreamPackCount() {
	    return dreamPackCount;
	}

	public void setDreamPackCount(int dreamPackCount) {
	    this.dreamPackCount = dreamPackCount;
	}
    }

    public static class ActionLogResponse {
	private int tick;
	private String time;
	private String actor;
	private String type;
	private String summary;
	private String fromLocation;
	private String toLocation;

	public int getTick() {
	    return tick;
	}

	public void setTick(int tick) {
	    this.tick = tick;
	}

	public String getTime() {
	    return time;
	}

	public void setTime(String time) {
	    this.time = time;
	}

	public String getActor() {
	    return actor;
	}

	public void setActor(String actor) {
	    this.actor = actor;
	}

	public String getType() {
	    return type;
	}

	public void setType(String type) {
	    this.type = type;
	}

	public String getSummary() {
	    return summary;
	}

	public void setSummary(String summary) {
	    this.summary = summary;
	}

	public String getFromLocation() {
	    return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
	    this.fromLocation = fromLocation;
	}

	public String getToLocation() {
	    return toLocation;
	}

	public void setToLocation(String toLocation) {
	    this.toLocation = toLocation;
	}
    }

    public static class WorldProposalResponse {
	private String id;
	private String agent;
	private String type;
	private String parentLocation;
	private String name;
	private String proposedState;
	private String reason;
	private String status;
	private int createdAtTick;

	public String getId() {
	    return id;
	}

	public void setId(String id) {
	    this.id = id;
	}

	public String getAgent() {
	    return agent;
	}

	public void setAgent(String agent) {
	    this.agent = agent;
	}

	public String getType() {
	    return type;
	}

	public void setType(String type) {
	    this.type = type;
	}

	public String getParentLocation() {
	    return parentLocation;
	}

	public void setParentLocation(String parentLocation) {
	    this.parentLocation = parentLocation;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getProposedState() {
	    return proposedState;
	}

	public void setProposedState(String proposedState) {
	    this.proposedState = proposedState;
	}

	public String getReason() {
	    return reason;
	}

	public void setReason(String reason) {
	    this.reason = reason;
	}

	public String getStatus() {
	    return status;
	}

	public void setStatus(String status) {
	    this.status = status;
	}

	public int getCreatedAtTick() {
	    return createdAtTick;
	}

	public void setCreatedAtTick(int createdAtTick) {
	    this.createdAtTick = createdAtTick;
	}
    }

    public static class GovernanceBlockRuleResponse {
	private String id;
	private String type;
	private String parentLocation;
	private String name;
	private String targetLocation;
	private String createdAtUtc;
	private String reason;

	public String getId() {
	    return id;
	}

	public void setId(String id) {
	    this.id = id;
	}

	public String getType() {
	    return type;
	}

	public void setType(String type) {
	    this.type = type;
	}

	public String getParentLocation() {
	    return parentLocation;
	}

	public void setParentLocation(String parentLocation) {
	    this.parentLocation = parentLocation;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getTargetLocation() {
	    return targetLocation;
	}

	public void setTargetLocation(String targetLocation) {
	    this.targetLocation = targetLocation;
	}

	public String getCreatedAtUtc() {
	    return createdAtUtc;
	}

	public void setCreatedAtUtc(String createdAtUtc) {
	    this.createdAtUtc = createdAtUtc;
	}

	public String getReason() {
	    return reason;
	}

	public void setReason(String reason) {
	    this.reason = reason;
	}
    }
}
