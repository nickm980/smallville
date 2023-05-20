package io.github.nickm980.smallville;

public class Util {

    public static String[] parseLocation(String fullName) {
	String[] locations = fullName.split(":");
	String[] result = new String[2];
	result[0] = locations[0].trim();
	result[1] = locations[1].trim();
	return result;
    }
}
