package io.github.nickm980.smallville.api;

import java.util.Map;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import io.github.nickm980.smallville.exceptions.AgentNotFoundException;
import io.github.nickm980.smallville.exceptions.LocationNotFoundException;
import io.github.nickm980.smallville.exceptions.SmallvilleException;
import io.javalin.Javalin;

public final class ExceptionRoutes {

    private ExceptionRoutes() {
    }

    public static void registerExceptions(Javalin app) {
	app.exception(UnrecognizedPropertyException.class, (e, ctx) -> {
	    ctx.json(Map.of("error", e.getPropertyName() + " was not recognized"));
	});

	app.exception(JsonParseException.class, (e, ctx) -> {
	    ctx.status(400).json(Map.of("error", "Invalid json request body"));
	});

	app.exception(UnrecognizedPropertyException.class, (e, ctx) -> {
	    ctx.status(400).json(Map.of("error", "Field not recognized {" + e.getPropertyName() + "}"));
	});

	app.exception(SmallvilleException.class, (e, ctx) -> {
	    ctx.status(400).json(Map.of("error", e.getMessage()));
	});

	app.exception(NoSuchElementException.class, (e, ctx) -> {
	    ctx.status(404).json(Map.of("error", "Resource does not exist"));
	});

	app.exception(IllegalArgumentException.class, (e, ctx) -> {
	    ctx.status(400).json(Map.of("error", e.getMessage()));
	});
	
	app.exception(LocationNotFoundException.class, (e, ctx) -> {
	    ctx.status(404).json(Map.of("error", e.getMessage()));
	});
	
	app.exception(AgentNotFoundException.class, (e, ctx) -> {
	    ctx.status(404).json(Map.of("error", e.getMessage()));
	});
	app.exception(Exception.class, (e, ctx) -> {
	    e.printStackTrace();
	    ctx.status(400).json(Map.of("error", "An unexpected error has occured"));
	});
    }
}
