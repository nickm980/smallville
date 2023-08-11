package io.github.nickm980.smallville.mocks;

import io.github.nickm980.smallville.llm.LLM;
import io.github.nickm980.smallville.prompts.PromptRequest;

public class MockLLM implements LLM {

    @Override
    public String sendChat(PromptRequest prompt, double temperature) {
	return "result";
    }

    @Override
    public float[] getTokenEmbeddings(String input) {
	return null;
    }

}
