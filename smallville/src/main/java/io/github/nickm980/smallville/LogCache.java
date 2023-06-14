package io.github.nickm980.smallville;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LogCache {

    private LogCache() {
    }

    private volatile static List<String> prompts = new ArrayList<String>();
    private volatile static List<String> messages = new ArrayList<String>();

    public static synchronized void addPrompt(String prompt) {
	prompts.add(prompt);
    }

    public static synchronized List<String> getPrompts() {
	return Collections.unmodifiableList(prompts);
    }

    public static synchronized List<String> getMessages() {
	return Collections.unmodifiableList(messages);
    }

    public static void refresh() {
	prompts.clear();
	messages.clear();
    }

}
