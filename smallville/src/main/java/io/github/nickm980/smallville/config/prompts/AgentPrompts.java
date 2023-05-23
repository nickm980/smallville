package io.github.nickm980.smallville.config.prompts;

public class AgentPrompts {

    private String characteristics;
    private String summary;
    private String askQuestion;

    public String getAskQuestion() {
	return askQuestion;
    }

    public void setAskQuestion(String askQuestion) {
	this.askQuestion = askQuestion;
    }

    public String getCharacteristics() {
	return characteristics;
    }

    public void setCharacteristics(String characteristics) {
	this.characteristics = characteristics;
    }

    public String getSummary() {
	return summary;
    }

    public void setSummary(String summary) {
	this.summary = summary;
    }
}
