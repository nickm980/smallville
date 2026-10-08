package io.github.nickm980.smallville.api.v1.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.nickm980.smallville.entities.AffectState;
import io.github.nickm980.smallville.memory.ledger.DreamPack;

/**
 * Response DTO for {@code GET /agents/{name}/memory-ledger}.
 */
public class AgentMemoryLedgerResponse {

    private String agent;
    private PersonaAnchors personaAnchors;
    private AffectState affect;
    private List<String> working = new ArrayList<>();
    private List<String> recent = new ArrayList<>();
    private List<String> archived = new ArrayList<>();
    private List<DreamPack> dreamPacks = new ArrayList<>();
    private Map<String, Object> recallPolicy;

    // --- Getters / Setters ---

    public String getAgent() {
	return agent;
    }

    public void setAgent(String agent) {
	this.agent = agent;
    }

    public PersonaAnchors getPersonaAnchors() {
	return personaAnchors;
    }

    public void setPersonaAnchors(PersonaAnchors personaAnchors) {
	this.personaAnchors = personaAnchors;
    }

    public AffectState getAffect() {
	return affect;
    }

    public void setAffect(AffectState affect) {
	this.affect = affect;
    }

    public List<String> getWorking() {
	return working;
    }

    public void setWorking(List<String> working) {
	this.working = working;
    }

    public List<String> getRecent() {
	return recent;
    }

    public void setRecent(List<String> recent) {
	this.recent = recent;
    }

    public List<String> getArchived() {
	return archived;
    }

    public void setArchived(List<String> archived) {
	this.archived = archived;
    }

    public List<DreamPack> getDreamPacks() {
	return dreamPacks;
    }

    public void setDreamPacks(List<DreamPack> dreamPacks) {
	this.dreamPacks = dreamPacks;
    }

    public Map<String, Object> getRecallPolicy() {
	return recallPolicy;
    }

    public void setRecallPolicy(Map<String, Object> recallPolicy) {
	this.recallPolicy = recallPolicy;
    }

    /**
     * Persona anchors — traits, goals, rituals, persona characteristics.
     */
    public static class PersonaAnchors {
	private String traits;
	private List<String> goals = new ArrayList<>();
	private List<String> rituals = new ArrayList<>();
	private List<String> persona = new ArrayList<>();
	private String socialPreference;

	public String getTraits() {
	    return traits;
	}

	public void setTraits(String traits) {
	    this.traits = traits;
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

	public List<String> getPersona() {
	    return persona;
	}

	public void setPersona(List<String> persona) {
	    this.persona = persona;
	}

	public String getSocialPreference() {
	    return socialPreference;
	}

	public void setSocialPreference(String socialPreference) {
	    this.socialPreference = socialPreference;
	}
    }
}
