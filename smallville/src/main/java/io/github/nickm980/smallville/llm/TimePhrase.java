package io.github.nickm980.smallville.llm;

public enum TimePhrase {

    MINUTES("the next 15 minutes"), HOURS("the next few hours"), DAY("the rest of the day");

    String phrase;

    TimePhrase(String time) {
	this.phrase = time;
    }

    public String getPhrase() {
	return phrase;
    }
}
