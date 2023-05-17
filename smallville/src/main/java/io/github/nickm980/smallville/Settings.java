package io.github.nickm980.smallville;

public final class Settings {

    private Settings() {
    }

    public static enum TokenUsage {
	LOW, HIGH
    }

    public final static TokenUsage TOKEN_USAGE = TokenUsage.LOW;
    private static String API_KEY;

    public static void setApiKey(String key) {
	API_KEY = key;
    }

    public static String getApiKey() {
	return API_KEY;
    }
}
