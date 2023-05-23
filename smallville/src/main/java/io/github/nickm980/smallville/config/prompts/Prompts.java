package io.github.nickm980.smallville.config.prompts;

public class Prompts {

    private ReactionPrompts reactions;
    private WorldPrompts world;
    private PlanPrompts plans;
    private AgentPrompts agent;
    private MiscPrompts misc;

    public ReactionPrompts getReactions() {
	return reactions;
    }

    public void setReactions(ReactionPrompts reactions) {
	this.reactions = reactions;
    }

    public WorldPrompts getWorld() {
	return world;
    }

    public void setWorld(WorldPrompts activity) {
	this.world = activity;
    }

    public PlanPrompts getPlans() {
	return plans;
    }

    public void setPlans(PlanPrompts plans) {
	this.plans = plans;
    }

    public AgentPrompts getAgent() {
	return agent;
    }

    public void setAgent(AgentPrompts agent) {
	this.agent = agent;
    }

    public MiscPrompts getMisc() {
	return misc;
    }

    public void setMisc(MiscPrompts misc) {
	this.misc = misc;
    }
}