package io.github.nickm980.smallville.config.prompts;

public class AgentPrompts {

    private String characteristics;
    private String summary;
    private String askQuestion;
    private String reflectionQuestion;
    private String reflectionResult;

    public String getReflectionQuestion() {
	return reflectionQuestion;
    }

    public void setReflectionQuestion(String reflectionQuestion) {
	this.reflectionQuestion = reflectionQuestion;
    }

    public String getReflectionResult() {
	return reflectionResult;
    }

    public void setReflectionResult(String reflectionResult) {
	this.reflectionResult = reflectionResult;
    }

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
