package io.github.nickm980.smallville.llm.api;

import io.github.nickm980.smallville.llm.Prompt;

public interface LLM {

    /**
     * Sends a prompt to the Chat Completions API
     * 
     * @param prompt
     * @param temperature Creativity level. Higher values are more creative but less
     *                    coherent
     * @return chat response String
     */
    String sendChat(Prompt prompt, double temperature);

    /**
     * Pricing is $0.0004 / thousand tokens for embeddings.
     * 
     * @param input
     * @return
     */
    float[] getTokenEmbeddings(String input);
}