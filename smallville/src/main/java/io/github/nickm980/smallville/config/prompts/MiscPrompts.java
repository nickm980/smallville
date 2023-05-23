package io.github.nickm980.smallville.config.prompts;

public class MiscPrompts {

    private String rankMemories;
    private String combineSentences;
    private String debug;
    private String relationship;

    public String getRelationship() {
	return relationship;
    }

    public void setRelationship(String relationship) {
	this.relationship = relationship;
    }

    public String getRankMemories() {
	return rankMemories;
    }

    public void setRankMemories(String rankMemories) {
	this.rankMemories = rankMemories;
    }

    public String getCombineSentences() {
	return combineSentences;
    }

    public void setCombineSentences(String combineSentences) {
	this.combineSentences = combineSentences;
    }

    public String getDebug() {
	return debug;
    }

    public void setDebug(String debug) {
	this.debug = debug;
    }
}
