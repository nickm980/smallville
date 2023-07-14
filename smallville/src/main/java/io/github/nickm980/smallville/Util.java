package io.github.nickm980.smallville;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;

public class Util {
    private static final Gson gson = new Gson();

    public static <T> T parseAsClass(String input, Class<T> clazz) {
	Map<String, String> data = parseCson(input);

	return gson.fromJson(gson.toJson(data), clazz);
    }

    public static Map<String, String> parseCson(String input) {
	HashMap<String, String> result = new HashMap<String, String>();

	for (String line : input.split("\n")) {
	    if (line.contains(":")) {
		String[] values = line.split(":", 2);
		result.put(values[0].toLowerCase(), values[1].trim());
	    }
	}

	return result;
    }
}
