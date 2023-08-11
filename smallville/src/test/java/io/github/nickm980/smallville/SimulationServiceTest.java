package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.github.nickm980.smallville.api.v1.SimulationService;
import io.github.nickm980.smallville.api.v1.dto.CreateAgentRequest;
import io.github.nickm980.smallville.api.v1.dto.CreateLocationRequest;
import io.github.nickm980.smallville.api.v1.dto.CreateMemoryRequest;
import io.github.nickm980.smallville.llm.ChatGPT;

public class SimulationServiceTest {

    private SimulationService service;

    @BeforeEach
    public void setUp() {
	ChatGPT llm = Mockito.mock(ChatGPT.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyInt())).thenReturn("result");
	service = new SimulationService(llm, new World());
    }

    @Test
    public void test_location_creation_is_added_to_list() {
	CreateLocationRequest request = new CreateLocationRequest();
	request.setName("location");

	service.createLocation(request);

	assertEquals(1, service.getAllLocations().size());
    }

    @Test
    public void test_agent_creation_is_added_to_list() {
	CreateLocationRequest request = new CreateLocationRequest();
	request.setName("location");

	service.createLocation(request);

	CreateAgentRequest createAgent = new CreateAgentRequest();
	createAgent.setActivity("doing nothing");
	createAgent.setLocation("location");
	createAgent.setMemories(List.of("memory1"));
	createAgent.setName("name");

	service.createAgent(createAgent);

	assertEquals(1, service.getAgents().size());
    }

    @Test
    public void test_service_memory_creation_does_not_throw() {
	CreateLocationRequest createLocation = new CreateLocationRequest();
	createLocation.setName("location");

	service.createLocation(createLocation);

	CreateAgentRequest createAgent = new CreateAgentRequest();
	createAgent.setActivity("doing nothing");
	createAgent.setLocation("location");
	createAgent.setMemories(List.of("memory1"));
	createAgent.setName("name");

	service.createAgent(createAgent);

	CreateMemoryRequest request = new CreateMemoryRequest();
	request.setName("name");
	request.setDescription("description");
	request.setReactable(false);

	assertDoesNotThrow(() -> {
	    service.createMemory(request);
	});
    }
}
