package io.github.nickm980.smallville;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.nickm980.smallville.analytics.Analytics;
import io.github.nickm980.smallville.api.SmallvilleServer;
import io.github.nickm980.smallville.llm.ChatGPT;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

public class EndpointExceptionsTest {
    private Javalin app;

    @BeforeEach
    public void setUp() {
	ChatGPT llm = Mockito.mock(ChatGPT.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn("result");
	app = new SmallvilleServer(new Analytics(), llm, new World()).server();
    }

    @Test
    public void GET_endpoint_not_found() {
	JavalinTest.test(app, (server, client) -> {
	    assertEquals(client.get("/thisendpointdoesnotexist").code(), 404);
	});
    }

    @Test
    public void GET_memories_for_missing_agent_returns_agent_not_found_contract() {
    try {
        int port;
        try (ServerSocket serverSocket = new ServerSocket(0)) {
        port = serverSocket.getLocalPort();
        }
        app.start(port);
        HttpClient httpClient = HttpClient.newHttpClient();
        try {
        HttpResponse<String> response = httpClient.send(
            HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/memories/nonexistant"))
            .GET()
            .build(),
            HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        assertEquals(body, 404, response.statusCode());
        assertEquals(true, body.contains("Agent not found nonexistant"));
        } finally {
        app.stop();
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }

    @Test
    public void POST_memories_for_missing_agent_returns_agent_not_found_contract() {
    try {
        int port;
        try (ServerSocket serverSocket = new ServerSocket(0)) {
        port = serverSocket.getLocalPort();
        }
        app.start(port);
        HttpClient httpClient = HttpClient.newHttpClient();
        try {
        HttpResponse<String> response = httpClient.send(
            HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/memories"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"nonexistant\",\"description\":\"desc\",\"reactable\":false}"))
            .build(),
            HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        assertEquals(body, 404, response.statusCode());
        assertTrue(body.contains("Agent not found nonexistant"));
        } finally {
        app.stop();
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }
}
