package io.github.nickm980.smallville;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

final class MockSmallvilleHttpServer {
    private final Map<UUID, List<String>> memoryStreams = new ConcurrentHashMap<UUID, List<String>>();
    private HttpServer server;

    void start() throws IOException {
	server = HttpServer.create(new InetSocketAddress(0), 0);
	server.createContext("/locations", exchange -> {
	    if ("POST".equals(exchange.getRequestMethod())) {
		writeJson(exchange, 200, new JSONObject().put("success", true));
		return;
	    }
	    writeJson(exchange, 405, new JSONObject().put("success", false));
	});
	server.createContext("/agents", exchange -> {
	    if ("POST".equals(exchange.getRequestMethod())) {
		writeJson(exchange, 200, new JSONObject().put("success", true));
		return;
	    }
	    writeJson(exchange, 405, new JSONObject().put("success", false));
	});
	server.createContext("/memories", exchange -> {
	    if ("POST".equals(exchange.getRequestMethod())) {
		writeJson(exchange, 200, new JSONObject().put("success", true));
		return;
	    }
	    writeJson(exchange, 405, new JSONObject().put("success", false));
	});
	server.createContext("/memories/stream", this::handleMemoryStreamRequest);
	server.setExecutor(Executors.newCachedThreadPool());
	server.start();
    }

    void stop() {
	if (server != null) {
	    server.stop(0);
	    server = null;
	}
    }

    String getBaseUrl() {
	return "http://localhost:" + server.getAddress().getPort();
    }

    private void handleMemoryStreamRequest(HttpExchange exchange) throws IOException {
	String method = exchange.getRequestMethod();
	String path = exchange.getRequestURI().getPath();

	if ("POST".equals(method) && "/memories/stream".equals(path)) {
	    UUID uuid = UUID.randomUUID();
	    memoryStreams.put(uuid, new ArrayList<String>());
	    writeJson(exchange, 200, new JSONObject().put("success", true).put("uuid", uuid.toString()));
	    return;
	}

	if (!"POST".equals(method)) {
	    writeJson(exchange, 405, new JSONObject().put("success", false));
	    return;
	}

	String prefix = "/memories/stream/";
	if (!path.startsWith(prefix)) {
	    writeJson(exchange, 404, new JSONObject().put("success", false));
	    return;
	}

	UUID uuid = UUID.fromString(path.substring(prefix.length()));
	List<String> memories = memoryStreams.get(uuid);
	if (memories == null) {
	    writeJson(exchange, 404, new JSONObject().put("success", false));
	    return;
	}

	JSONObject body = readJson(exchange);
	String memory = body.optString("memory", "");
	if (!memory.isBlank()) {
	    memories.add(memory);
	    writeJson(exchange, 200, new JSONObject().put("success", true));
	    return;
	}

	String query = body.optString("query", "").toLowerCase();
	List<String> matches = memories
	    .stream()
	    .filter(value -> value.toLowerCase().contains(query))
	    .limit(3)
	    .collect(Collectors.toList());
	writeJson(exchange, 200, new JSONObject().put("success", true).put("memories", new JSONArray(matches)));
    }

    private JSONObject readJson(HttpExchange exchange) throws IOException {
	byte[] payload = exchange.getRequestBody().readAllBytes();
	if (payload.length == 0) {
	    return new JSONObject();
	}

	return new JSONObject(new String(payload, StandardCharsets.UTF_8));
    }

    private void writeJson(HttpExchange exchange, int status, JSONObject body) throws IOException {
	byte[] responseBytes = body.toString().getBytes(StandardCharsets.UTF_8);
	exchange.getResponseHeaders().add("Content-Type", "application/json");
	exchange.sendResponseHeaders(status, responseBytes.length);
	try (OutputStream responseBody = exchange.getResponseBody()) {
	    responseBody.write(responseBytes);
	}
    }
}
