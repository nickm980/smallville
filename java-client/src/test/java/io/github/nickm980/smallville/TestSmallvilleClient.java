package io.github.nickm980.smallville;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TestSmallvilleClient {

    @Test
    public void testSmallvilleClient() {
	SmallvilleClient client = SmallvilleClient.create("http://localhost:8080", new AgentHandlerCallback() {
	    @Override
	    public void handle(List<SmallvilleAgent> agents, List<SmallvilleLocation> locations) {
		System.out.println(agents);
	    }
	});
	
	client.createAgent("name", List.of("memory1", "memory2"), null, null);
    }
}
