package io.github.nickm980.smallville.llm.api;

import io.github.nickm980.smallville.llm.Prompt;

public class MockGPT implements LLM {

    @Override
    public String sendChat(Prompt query, double temp) {
	return query.toString();
    }

    @Override
    public float[] getTokenEmbeddings(String input) {
	return null;
    }

}
