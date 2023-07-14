package io.github.nickm980.smallville.entities;

public class Dialog {

    private final String name;
    private final String message;

    public Dialog(String name, String message) {
	super();
	this.name = name;
	this.message = message;
    }

    public String getName() {
	return name;
    }

    public String getMessage() {
	return message;
    }
}
