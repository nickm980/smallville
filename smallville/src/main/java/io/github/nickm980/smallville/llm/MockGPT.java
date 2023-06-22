package io.github.nickm980.smallville.llm;

import io.github.nickm980.smallville.prompts.PromptRequest;

public class MockGPT implements LLM {

    @Override
    public String sendChat(PromptRequest query, double temp) {
	return query.toString();
    }

    @Override
    public float[] getTokenEmbeddings(String input) {
	return null;
    }

}
