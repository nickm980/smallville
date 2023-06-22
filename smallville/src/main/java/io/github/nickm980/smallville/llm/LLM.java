package io.github.nickm980.smallville.llm;

import io.github.nickm980.smallville.prompts.PromptRequest;

public interface LLM {

    /**
     * Sends a prompt to the Chat Completions API
     * 
     * @param prompt
     * @param temperature Creativity level. Higher values are more creative but less
     *                    coherent
     * @return chat response String
     */
    String sendChat(PromptRequest prompt, double temperature);

    /**
     * Pricing is $0.0004 / thousand tokens for embeddings.
     * 
     * @param input
     * @return
     */
    float[] getTokenEmbeddings(String input);
}