package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nickm980.smallville.api.v1.SimulationService;
import io.github.nickm980.smallville.api.v1.dto.CreateAgentRequest;
import io.github.nickm980.smallville.api.v1.dto.CreateLocationRequest;
import io.github.nickm980.smallville.api.v1.dto.CreateMemoryRequest;
import io.github.nickm980.smallville.api.v1.dto.ImportAgentsRequest;
import io.github.nickm980.smallville.api.v1.dto.ImportAgentsResponse;
import io.github.nickm980.smallville.api.v1.dto.SetAgentModelRequest;
import io.github.nickm980.smallville.api.v1.dto.WorldSnapshotResponse;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.Dialog;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.entities.WorldProposal;
import io.github.nickm980.smallville.llm.ChatGPT;
import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.memory.Plan;
import io.github.nickm980.smallville.memory.PlanType;
import io.github.nickm980.smallville.api.v1.AskShadowBridgeClient;
import io.github.nickm980.smallville.prompts.PromptRequest;
import io.github.nickm980.smallville.prompts.Prompts;
import io.github.nickm980.smallville.prompts.dto.CurrentActivity;
import io.github.nickm980.smallville.update.UpdateConversation;
import io.github.nickm980.smallville.update.UpdateCurrentActivity;
import io.github.nickm980.smallville.update.UpdateInfo;
import io.github.nickm980.smallville.update.UpdatePlans;

public class SimulationServiceTest {

    private SimulationService service;
    private World world;
	private ChatGPT llm;

    @BeforeEach
    public void setUp() {
	llm = Mockito.mock(ChatGPT.class);
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn("result");
	world = new World();
	service = new SimulationService(llm, world);
    }

    @Test
    public void test_location_creation_is_added_to_list() {
	int initialLocationCount = service.getAllLocations().size();

	CreateLocationRequest request = new CreateLocationRequest();
	request.setName("location");

	service.createLocation(request);

	assertEquals(initialLocationCount + 1, service.getAllLocations().size());
    }

    @Test
    public void test_agent_creation_is_added_to_list() {
	int initialAgentCount = service.getAgents().size();

	CreateLocationRequest request = new CreateLocationRequest();
	request.setName("location");

	service.createLocation(request);

	CreateAgentRequest createAgent = new CreateAgentRequest();
	createAgent.setActivity("doing nothing");
	createAgent.setLocation("location");
	createAgent.setMemories(List.of("memory1"));
	createAgent.setName("name");
	createAgent.setTraits("steady, helpful, quiet");

	service.createAgent(createAgent);

	assertEquals(initialAgentCount + 1, service.getAgents().size());
    }

    @Test
    public void test_default_bootstrap_prefers_two_house_scenario_seed() {
	assertEquals(12, service.getAllLocations().size());
	assertEquals(2, service.getAgents().size());
	assertTrue(world.getAgent("Jamie").isPresent());
	assertTrue(world.getAgent("Alex").isPresent());
	assertEquals(
	    "Tea things laid out beside a garden notebook",
	    world.getLocation("Blue House: Kitchen").orElseThrow().getState());
	assertEquals(
	    "Plan sheets and tray maps spread beside a mug ring",
	    world.getLocation("Green House: Glass Table").orElseThrow().getState());
	assertEquals(
	    "Freshly prepared for transplanting and bed tidy work",
	    world.getLocation("Garden: South Bed").orElseThrow().getState());
	assertEquals(
	    "A calm two-house horticultural microcosm built around watering rounds, propagation work, quiet review, and shared tea pauses.",
	    service.getWorldSnapshot().getWorldBuilding().getSummary());
	assertEquals("06:00-09:30", service.getWorldSnapshot().getDailyRhythm().getBreakfast());
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
	createAgent.setTraits("steady, helpful, quiet");

	service.createAgent(createAgent);

	CreateMemoryRequest request = new CreateMemoryRequest();
	request.setName("name");
	request.setDescription("description");
	request.setReactable(false);

	assertDoesNotThrow(() -> {
	    service.createMemory(request);
	});
    }

    @Test
    public void test_agent_creation_keeps_persona_and_working_memories_separate() {
	createLocation("Green House: Glass Table");

	CreateAgentRequest createAgent = new CreateAgentRequest();
	createAgent.setActivity("sketching plants");
	createAgent.setLocation("Green House: Glass Table");
	createAgent.setMemories(List.of("Alex is observant", "Alex likes quiet studios"));
	createAgent.setWorkingMemories(List.of("Jamie promised tea", "The Green House needs watering"));
	createAgent.setTraits("curious, artistic, patient");
	createAgent.setName("Morgan");

	service.createAgent(createAgent);

	Agent alex = world.getAgent("Morgan").orElseThrow();

	assertEquals(2, alex.getMemoryStream().getCharacteristics().size());
	assertEquals(2, alex.getMemoryStream().getWorkingMemories().size());
	assertEquals(0, alex.getMemoryStream().getObservations().size());
	assertEquals("curious, artistic, patient", alex.getTraits());
    }

    @Test
    public void test_world_snapshot_serializes_with_agents_and_locations() {
	createLocation("Green House");
	createLocation("Green House: Glass Table");

	CreateAgentRequest createAgent = new CreateAgentRequest();
	createAgent.setActivity("sketching plans");
	createAgent.setLocation("Green House: Glass Table");
	createAgent.setMemories(List.of("Alex likes quiet studios"));
	createAgent.setWorkingMemories(List.of("Jamie promised tea"));
	createAgent.setTraits("curious, artistic, patient");
	createAgent.setName("Taylor");
	service.createAgent(createAgent);

	assertDoesNotThrow(() -> {
	    new ObjectMapper().writeValueAsString(service.getWorldSnapshot());
	});
	assertEquals("06:00-09:30", service.getWorldSnapshot().getDailyRhythm().getBreakfast());
    }

    @Test
    public void test_world_snapshot_exposes_distinct_working_and_recent_memories() {
	createLocation("Green House: Glass Table");

	CreateAgentRequest createAgent = new CreateAgentRequest();
	createAgent.setActivity("sketching plans");
	createAgent.setLocation("Green House: Glass Table");
	createAgent.setMemories(List.of("Alex likes quiet studios"));
	createAgent.setWorkingMemories(List.of("Jamie promised tea"));
	createAgent.setTraits("curious, artistic, patient");
	createAgent.setName("Taylor");
	service.createAgent(createAgent);

	CreateMemoryRequest request = new CreateMemoryRequest();
	request.setName("Taylor");
	request.setDescription("watered the propagation tray");
	request.setReactable(false);
	service.createMemory(request);

	WorldSnapshotResponse.WorldAgentResponse snapshotAgent = service.getWorldSnapshot().getAgents().stream()
	    .filter(agent -> agent.getName().equals("Taylor"))
	    .findFirst()
	    .orElseThrow();

	assertEquals(List.of("Jamie promised tea"), snapshotAgent.getWorkingMemories());
	assertEquals(List.of("watered the propagation tray"), snapshotAgent.getRecentMemories());
	assertFalse(snapshotAgent.getWorkingMemories().equals(snapshotAgent.getRecentMemories()));
	assertTrue(snapshotAgent.getRecentMemories().contains("watered the propagation tray"));
	assertFalse(snapshotAgent.getRecentMemories().contains("Jamie promised tea"));
    }

    @Test
    public void test_ask_question_preserves_native_answer_when_shadow_bridge_is_unavailable() {
	ChatGPT localLlm = Mockito.mock(ChatGPT.class);
	Mockito.when(localLlm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn("result");

	AskShadowBridgeClient unavailableBridge = new AskShadowBridgeClient() {
	    @Override
	    public ShadowAskResult shadowAsk(WorldSnapshotResponse snapshot, String agentName, String question) {
		return ShadowAskResult.skipped("test-shadow-request", "bridge_unavailable");
	    }
	};

	SimulationService localService = new SimulationService(localLlm, new World(), unavailableBridge);
	WorldSnapshotResponse before = localService.getWorldSnapshot();

	String answer = localService.askQuestion("Jamie", "How is the morning going?");

	WorldSnapshotResponse after = localService.getWorldSnapshot();
	assertEquals("result", answer);
	assertEquals(before.getLocations().size(), after.getLocations().size());
	assertEquals(before.getPendingProposals().size(), after.getPendingProposals().size());
	assertEquals(before.getTick(), after.getTick());
	Mockito.verify(localLlm, Mockito.times(1)).sendChat(Mockito.any(), Mockito.anyDouble());
    }

    @Test
    public void test_ask_question_skips_disabled_shadow_bridge_without_attempting_call() {
	ChatGPT localLlm = Mockito.mock(ChatGPT.class);
	Mockito.when(localLlm.sendChat(Mockito.any(), Mockito.anyDouble())).thenReturn("result");

	AtomicInteger shadowCalls = new AtomicInteger();
	AskShadowBridgeClient disabledBridge = new AskShadowBridgeClient(false, "http://127.0.0.1:8010/neural/turn", Duration.ofMillis(200), Duration.ofMillis(1200)) {
	    @Override
	    public ShadowAskResult shadowAsk(WorldSnapshotResponse snapshot, String agentName, String question) {
		shadowCalls.incrementAndGet();
		return super.shadowAsk(snapshot, agentName, question);
	    }
	};

	SimulationService localService = new SimulationService(localLlm, new World(), disabledBridge);

	String answer = localService.askQuestion("Jamie", "How is the morning going?");

	assertEquals("result", answer);
	assertEquals(0, shadowCalls.get());
	Mockito.verify(localLlm, Mockito.times(1)).sendChat(Mockito.any(), Mockito.anyDouble());
    }

    @Test
    public void test_current_activity_update_refreshes_working_memory_without_collapsing_into_observations() {
	World localWorld = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	Location glassTable = new Location("Green House: Glass Table");
	localWorld.create(kitchen);
	localWorld.create(glassTable);

	Agent agent = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "brewing tea",
	    kitchen);

	Prompts prompts = Mockito.mock(Prompts.class);
	UpdateCurrentActivity update = new UpdateCurrentActivity();

	CurrentActivity first = new CurrentActivity();
	first.setActivity("bringing tea to Alex");
	first.setLocation("Green House: Glass Table");
	first.setEmoji("tea");
	first.setLastActivity("brewed tea");

	CurrentActivity duplicate = new CurrentActivity();
	duplicate.setActivity("bringing tea to Alex");
	duplicate.setLocation("Green House: Glass Table");
	duplicate.setEmoji("tea");
	duplicate.setLastActivity("brewed tea");

	CurrentActivity second = new CurrentActivity();
	second.setActivity("checking seed notes");
	second.setLocation("Green House: Glass Table");
	second.setEmoji("notes");
	second.setLastActivity("checked the propagation notes");

	Mockito.when(prompts.getCurrentActivity(agent)).thenReturn(first, duplicate, second);

	update.update(prompts, localWorld, agent, new UpdateInfo());
	update.update(prompts, localWorld, agent, new UpdateInfo());
	update.update(prompts, localWorld, agent, new UpdateInfo());

	assertEquals(3, agent.getMemoryStream().getObservations().size());
	assertEquals(2, agent.getMemoryStream().getWorkingMemories().size());
	assertEquals(1, agent.getMemoryStream().getWorkingMemories().stream().filter(memory -> memory.getDescription().equals("brewed tea")).count());
	assertTrue(agent.getMemoryStream().getWorkingMemories().stream().anyMatch(memory -> memory.getDescription().equals("checked the propagation notes")));
	assertTrue(agent.getMemoryStream().getObservations().stream().anyMatch(memory -> memory.getDescription().equals("brewed tea")));
	assertTrue(agent.getMemoryStream().getObservations().stream().anyMatch(memory -> memory.getDescription().equals("checked the propagation notes")));
    }

    @Test
    public void test_update_plans_refreshes_stale_short_term_plans_without_touching_long_term_plans() {
	World localWorld = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	localWorld.create(kitchen);

	Agent agent = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "bringing tea",
	    kitchen);
	localWorld.create(agent);

	Plan staleShortTerm = new Plan("8:00 am at the Blue House: Kitchen, prepare the tea tray", java.time.LocalDateTime.now().minusHours(2));
	staleShortTerm.convert(PlanType.SHORT_TERM);
	Plan longTerm = new Plan("6:00 pm at the Blue House: Kitchen, prepare dinner", java.time.LocalDateTime.now().plusHours(2));
	longTerm.convert(PlanType.LONG_TERM);
	agent.getMemoryStream().addAll(List.of(staleShortTerm, longTerm));
	Plan refreshedShortTerm = new Plan("10:30 am at the Blue House: Kitchen, refresh the tea tray", java.time.LocalDateTime.now().plusMinutes(30));
	refreshedShortTerm.convert(PlanType.SHORT_TERM);

	Prompts prompts = Mockito.mock(Prompts.class);
	Mockito.when(prompts.getShortTermPlans(agent)).thenReturn(List.of(refreshedShortTerm));
	UpdatePlans update = new UpdatePlans();
	UpdateInfo cycle = new UpdateInfo();

	assertDoesNotThrow(() -> update.update(prompts, localWorld, agent, cycle));

	List<Plan> shortPlans = agent.getMemoryStream().getPlans(PlanType.SHORT_TERM);
	assertEquals(1, shortPlans.size());
	assertEquals("10:30 am at the Blue House: Kitchen, refresh the tea tray", shortPlans.get(0).getDescription());
	assertTrue(shortPlans.get(0).getTime().isAfter(java.time.LocalDateTime.now()));
	assertFalse(shortPlans.stream().anyMatch(plan -> plan.getDescription().equals("8:00 am at the Blue House: Kitchen, prepare the tea tray")));
	assertEquals(1, agent.getMemoryStream().getPlans(PlanType.LONG_TERM).size());
	assertEquals("6:00 pm at the Blue House: Kitchen, prepare dinner", agent.getMemoryStream().getPlans(PlanType.LONG_TERM).get(0).getDescription());
	assertTrue(cycle.isPlansUpdated());
	Mockito.verify(prompts, Mockito.never()).shouldUpdatePlans(Mockito.any(), Mockito.anyString());
	Mockito.verify(prompts, Mockito.never()).getPlans(Mockito.any());
	Mockito.verify(prompts, Mockito.times(1)).getShortTermPlans(agent);
    }

    @Test
    public void test_update_plans_prunes_expired_entries_from_mixed_short_term_family() {
	World localWorld = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	localWorld.create(kitchen);

	Agent agent = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "bringing tea",
	    kitchen);
	localWorld.create(agent);

	Plan expiredShortTerm = new Plan("8:00 am at the Blue House: Kitchen, clear the cups", java.time.LocalDateTime.now().minusHours(2));
	expiredShortTerm.convert(PlanType.SHORT_TERM);
	Plan futureShortTerm = new Plan("10:30 am at the Blue House: Kitchen, pour fresh tea", java.time.LocalDateTime.now().plusMinutes(30));
	futureShortTerm.convert(PlanType.SHORT_TERM);
	Plan longTerm = new Plan("6:00 pm at the Blue House: Kitchen, prepare dinner", java.time.LocalDateTime.now().plusHours(2));
	longTerm.convert(PlanType.LONG_TERM);
	agent.getMemoryStream().addAll(List.of(expiredShortTerm, futureShortTerm, longTerm));

	Prompts prompts = Mockito.mock(Prompts.class);
	UpdatePlans update = new UpdatePlans();
	UpdateInfo cycle = new UpdateInfo();

	assertDoesNotThrow(() -> update.update(prompts, localWorld, agent, cycle));

	List<Plan> shortPlans = agent.getMemoryStream().getPlans(PlanType.SHORT_TERM);
	assertEquals(1, shortPlans.size());
	assertFalse(shortPlans.stream().anyMatch(plan -> plan.getDescription().equals("8:00 am at the Blue House: Kitchen, clear the cups")));
	assertTrue(shortPlans.stream().anyMatch(plan -> plan.getDescription().equals("10:30 am at the Blue House: Kitchen, pour fresh tea")));
	assertEquals(1, agent.getMemoryStream().getPlans(PlanType.LONG_TERM).size());
	assertEquals("6:00 pm at the Blue House: Kitchen, prepare dinner", agent.getMemoryStream().getPlans(PlanType.LONG_TERM).get(0).getDescription());
	assertTrue(cycle.isPlansUpdated());
	Mockito.verify(prompts, Mockito.never()).shouldUpdatePlans(Mockito.any(), Mockito.anyString());
	Mockito.verify(prompts, Mockito.never()).getPlans(Mockito.any());
	Mockito.verify(prompts, Mockito.never()).getShortTermPlans(Mockito.any());
    }

    @Test
    public void test_conversation_update_adds_latest_dialog_to_working_memory_without_spam() throws Exception {
	World localWorld = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	localWorld.create(kitchen);

	Agent jamie = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "bringing tea",
	    kitchen);
	Agent alex = new Agent(
	    "Alex",
	    List.of(new Characteristic("curious"), new Characteristic("artistic")),
	    "checking seedlings",
	    kitchen);
	localWorld.create(jamie);
	localWorld.create(alex);

	Prompts prompts = Mockito.mock(Prompts.class);
	Conversation conversation = new Conversation(
	    "Jamie",
	    "Alex",
	    List.of(
		new Dialog("Jamie", "The seedlings look stronger this morning."),
		new Dialog("Alex", "Good, I brought the tray notes with me."),
		new Dialog("Jamie", "Let us compare the watering order before lunch."),
		new Dialog("Alex", "Start with the glass-table notes.")));
	Mockito.when(prompts.getConversationIfExists(jamie, alex, "Alex, can we review the seedlings?"))
	    .thenReturn(conversation);

	Method method = UpdateConversation.class.getDeclaredMethod("updateConversation", Agent.class, Prompts.class, World.class, String.class);
	method.setAccessible(true);
	boolean result = (boolean) method.invoke(new UpdateConversation(), jamie, prompts, localWorld, "Alex, can we review the seedlings?");

	assertFalse(result);
	assertEquals(4, jamie.getMemoryStream().getObservations().size());
	assertEquals(4, alex.getMemoryStream().getObservations().size());
	assertEquals(1, jamie.getMemoryStream().getWorkingMemories().size());
	assertEquals(1, alex.getMemoryStream().getWorkingMemories().size());
	assertEquals("Start with the glasstable notes.", jamie.getMemoryStream().getWorkingMemories().get(0).getDescription());
	assertEquals("Start with the glasstable notes.", alex.getMemoryStream().getWorkingMemories().get(0).getDescription());
	assertFalse(jamie.getMemoryStream().getWorkingMemories().stream().anyMatch(memory -> memory.getDescription().equals("The seedlings look stronger this morning.")));
	assertEquals(1, localWorld.getConversationsAfter(null).size());
    }

    @Test
    public void test_conversation_update_isolated_dialogue_captures_single_line_for_agent_only() throws Exception {
	World localWorld = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	localWorld.create(kitchen);

	Agent jamie = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "bringing tea",
	    kitchen);
	localWorld.create(jamie);

	Prompts prompts = Mockito.mock(Prompts.class);
	Mockito.when(prompts.saySomething(jamie, "The greenhouse feels quiet today."))
	    .thenReturn(new Dialog("Jamie", "I should check the tea tray before moving on."));

	Method method = UpdateConversation.class.getDeclaredMethod("updateConversation", Agent.class, Prompts.class, World.class, String.class);
	method.setAccessible(true);
	boolean result = (boolean) method.invoke(new UpdateConversation(), jamie, prompts, localWorld, "The greenhouse feels quiet today.");

	assertTrue(result);
	assertEquals(1, jamie.getMemoryStream().getObservations().size());
	assertEquals(1, jamie.getMemoryStream().getWorkingMemories().size());
	assertEquals("I should check the tea tray before moving on.", jamie.getMemoryStream().getObservations().get(0).getDescription());
	assertEquals("I should check the tea tray before moving on.", jamie.getMemoryStream().getWorkingMemories().get(0).getDescription());
	assertEquals(0, localWorld.getConversationsAfter(null).size());
    }

    @Test
    public void test_conversation_update_empty_named_conversation_is_a_no_op() throws Exception {
	World localWorld = new World();
	Location kitchen = new Location("Blue House: Kitchen");
	localWorld.create(kitchen);

	Agent jamie = new Agent(
	    "Jamie",
	    List.of(new Characteristic("grounded"), new Characteristic("hospitable")),
	    "bringing tea",
	    kitchen);
	Agent alex = new Agent(
	    "Alex",
	    List.of(new Characteristic("curious"), new Characteristic("artistic")),
	    "checking seedlings",
	    kitchen);
	localWorld.create(jamie);
	localWorld.create(alex);

	Prompts prompts = Mockito.mock(Prompts.class);
	Mockito.when(prompts.getConversationIfExists(jamie, alex, "Alex, can we review the seedlings?"))
	    .thenReturn(new Conversation("Jamie", "Alex", List.of()));

	Method method = UpdateConversation.class.getDeclaredMethod("updateConversation", Agent.class, Prompts.class, World.class, String.class);
	method.setAccessible(true);
	boolean result = (boolean) method.invoke(new UpdateConversation(), jamie, prompts, localWorld, "Alex, can we review the seedlings?");

	assertFalse(result);
	assertEquals(0, jamie.getMemoryStream().getObservations().size());
	assertEquals(0, alex.getMemoryStream().getObservations().size());
	assertEquals(0, jamie.getMemoryStream().getWorkingMemories().size());
	assertEquals(0, alex.getMemoryStream().getWorkingMemories().size());
	assertEquals(0, localWorld.getConversationsAfter(null).size());
    }

    @Test
    public void test_model_override_is_reflected_in_world_snapshot() {
	createLocation("Green House: Glass Table");

	CreateAgentRequest createAgent = new CreateAgentRequest();
	createAgent.setActivity("sketching plans");
	createAgent.setLocation("Green House: Glass Table");
	createAgent.setMemories(List.of("Alex likes quiet studios"));
	createAgent.setName("Taylor");
	createAgent.setModel("gwen2.5:0.5b");
	createAgent.setTraits("curious, artistic, patient");
	service.createAgent(createAgent);

	SetAgentModelRequest override = new SetAgentModelRequest();
	override.setModel("llama3.2:3b");
	service.setAgentModel("Taylor", override);

	assertEquals(
	    "llama3.2:3b",
	    service.getWorldSnapshot().getAgents().stream()
		.filter(agent -> agent.getName().equals("Taylor"))
		.findFirst()
		.orElseThrow()
		.getModel());
    }

    @Test
    public void test_import_agents_preview_reports_missing_location() {
	ImportAgentsRequest request = new ImportAgentsRequest();
	request.setPreview(true);
	request.setYaml("agents:\n  - name: Rowan\n    location: Missing Place\n    activity: checking seeds\n    persona:\n      - Rowan keeps notes");

	ImportAgentsResponse response = service.importAgents(request);

	assertEquals(false, response.isSuccess());
	assertEquals(1, response.getAgents().size());
	assertEquals(true, response.getErrors().get(0).contains("Location does not exist"));
    }

    @Test
    public void test_memory_stream_add_and_query_preserves_duplicate_relevant_memories() {
	UUID uuid = service.createMemoryStream();
	assertTrue(service.addMemory(uuid, "memory 1"));
	assertTrue(service.addMemory(uuid, "memory 1"));
	assertTrue(service.addMemory(uuid, "memory 1"));
	assertTrue(service.addMemory(uuid, "memory 1"));

	List<String> memories = service.getMemories(uuid, "memory 1");

	assertEquals(3, memories.size());
	assertEquals(List.of("memory 1", "memory 1", "memory 1"), memories);
    }

    @Test
    public void test_add_location_accepts_container_like_parents() throws Exception {
	createLocation("Green House");
	createLocation("Green House: Potting Room");
	createLocation("Garden");

	assertTrue(isProposalValid(newProposal("add_location", "Garden", "North Arbor", null, "To make watering rounds easier to follow.")));
	assertTrue(isProposalValid(newProposal("add_location", "Green House", "Tray Room", null, "To separate propagation work from the main table.")));
	assertTrue(isProposalValid(newProposal("add_location", "Green House: Potting Room", "Seed Nook", null, "To give note-making and seed sorting a quiet corner.")));
    }

    @Test
    public void test_add_location_rejects_object_like_leaf_parents() throws Exception {
	createLocation("Green House");
	createLocation("Green House: Water Barrel");
	createLocation("Green House: Glass Table");
	createLocation("Garden");
	createLocation("Garden: Bench");

	assertFalse(isProposalValid(newProposal("add_location", "Green House: Water Barrel", "Rain Barrel Alcove", null, "To keep spare barrels together.")));
	assertFalse(isProposalValid(newProposal("add_location", "Green House: Glass Table", "Tool Caddy Corner", null, "To keep cuttings paperwork nearby.")));
	assertFalse(isProposalValid(newProposal("add_location", "Garden: Bench", "Pause Shelf", null, "To give tea cups a tidy resting place.")));
    }

    @Test
    public void test_valid_pending_add_location_remains_visible_for_review() throws Exception {
	createLocation("Garden");

	WorldProposal proposal = newProposal("add_location", "Garden", "North Arbor", "ready", "To keep the watering route organized.");
	assertTrue(isProposalValid(proposal));

	addPendingProposal(proposal);

	assertEquals(1, service.getWorldProposals().size());
	assertEquals("add_location", service.getWorldProposals().get(0).getType());
	assertEquals("Garden", service.getWorldProposals().get(0).getParentLocation());
	assertEquals("North Arbor", service.getWorldProposals().get(0).getName());
	assertEquals("To keep the watering route organized.", service.getWorldProposals().get(0).getReason());
	assertEquals("pending", service.getWorldProposals().get(0).getStatus());
    }

    @Test
    public void test_non_add_location_proposal_types_keep_previous_behavior() throws Exception {
	createLocation("Green House");
	createLocation("Green House: Glass Table");

	assertTrue(isProposalValid(newProposal("add_object", "Green House: Glass Table", "Tool Basket", null, "To keep twine and labels together.")));
	assertTrue(isProposalValid(newProposal("change_state", null, "Green House: Glass Table", "cleared", "To mark the work surface ready for propagation.")));
    }

    @Test
    public void test_duplicate_pending_add_location_is_still_rejected() throws Exception {
	createLocation("Garden");

	WorldProposal first = newProposal("add_location", "Garden", "North Arbor", null, "To keep the watering route organized.");
	assertTrue(isProposalValid(first));
	addPendingProposal(first);

	WorldProposal duplicate = newProposal("add_location", "Garden", "North Arbor", null, "To keep the watering route organized.");
	assertFalse(isProposalValid(duplicate));
    }

    @Test
    public void test_approve_world_proposal_applies_add_location_and_clears_pending_review() throws Exception {
	createLocation("Garden");

	WorldProposal proposal = newProposal("add_location", "Garden", "North Arbor", "ready", "To keep the watering route organized.");
	assertTrue(isProposalValid(proposal));
	addPendingProposal(proposal);

	WorldSnapshotResponse.WorldProposalResponse approved = service.approveWorldProposal(proposal.getId());

	assertEquals("applied", approved.getStatus());
	assertTrue(world.getLocation("Garden: North Arbor").isPresent());
	assertEquals("ready", world.getLocation("Garden: North Arbor").orElseThrow().getState());
	assertTrue(service.getWorldProposals().stream().noneMatch(candidate -> candidate.getId().equals(proposal.getId())));
	assertTrue(service.getWorldSnapshot().getPendingProposals().stream().noneMatch(candidate -> candidate.getId().equals(proposal.getId())));
	assertTrue(service.getWorldSnapshot().getLocations().stream().anyMatch(location -> location.getName().equals("Garden: North Arbor") && "ready".equals(location.getState())));
    }

    @Test
    public void test_approve_world_proposal_applies_add_object_and_clears_pending_review() throws Exception {
	createLocation("Blue House: Kitchen");

	WorldProposal proposal = newProposal("add_object", "Blue House: Kitchen", "Tea Tray Shelf", "organized", "To keep the tea setup tidy during the morning round.");
	assertTrue(isProposalValid(proposal));
	addPendingProposal(proposal);

	WorldSnapshotResponse.WorldProposalResponse approved = service.approveWorldProposal(proposal.getId());

	assertEquals("applied", approved.getStatus());
	assertTrue(world.getLocation("Blue House: Kitchen: Tea Tray Shelf").isPresent());
	assertEquals("organized", world.getLocation("Blue House: Kitchen: Tea Tray Shelf").orElseThrow().getState());
	assertTrue(service.getWorldProposals().stream().noneMatch(candidate -> candidate.getId().equals(proposal.getId())));
	assertTrue(service.getWorldSnapshot().getPendingProposals().stream().noneMatch(candidate -> candidate.getId().equals(proposal.getId())));
	assertTrue(service.getWorldSnapshot().getLocations().stream().anyMatch(location -> location.getName().equals("Blue House: Kitchen: Tea Tray Shelf") && "organized".equals(location.getState())));
    }

    @Test
    public void test_approve_world_proposal_applies_change_state_and_clears_pending_review() throws Exception {
	createLocation("Green House: Glass Table");
	service.setState("Green House: Glass Table", "crowded");

	WorldProposal proposal = newProposal("change_state", null, "Green House: Glass Table", "cleared", "To mark the work surface ready for propagation.");
	assertTrue(isProposalValid(proposal));
	addPendingProposal(proposal);

	WorldSnapshotResponse.WorldProposalResponse approved = service.approveWorldProposal(proposal.getId());

	assertEquals("applied", approved.getStatus());
	assertEquals("cleared", world.getLocation("Green House: Glass Table").orElseThrow().getState());
	assertTrue(service.getWorldProposals().stream().noneMatch(candidate -> candidate.getId().equals(proposal.getId())));
	assertTrue(service.getWorldSnapshot().getPendingProposals().stream().noneMatch(candidate -> candidate.getId().equals(proposal.getId())));
	assertTrue(service.getWorldSnapshot().getLocations().stream().anyMatch(location -> location.getName().equals("Green House: Glass Table") && "cleared".equals(location.getState())));
    }

    @Test
    public void test_approve_world_proposal_records_approval_and_applied_actions_in_newest_first_order() throws Exception {
	createLocation("Garden");

	WorldProposal proposal = newProposal("add_location", "Garden", "North Arbor", "ready", "To keep the watering route organized.");
	assertTrue(isProposalValid(proposal));
	addPendingProposal(proposal);
	int initialActionCount = service.getWorldSnapshot().getActionLog().size();

	service.approveWorldProposal(proposal.getId());

	List<WorldSnapshotResponse.ActionLogResponse> actionLog = service.getWorldSnapshot().getActionLog();
	assertEquals(initialActionCount + 2, actionLog.size());
	assertEquals("proposal-applied", actionLog.get(0).getType());
	assertEquals("World", actionLog.get(0).getActor());
	assertEquals("Applied add_location Garden: North Arbor", actionLog.get(0).getSummary());
	assertEquals("Garden", actionLog.get(0).getFromLocation());
	assertEquals("Garden: North Arbor", actionLog.get(0).getToLocation());
	assertEquals("proposal-approved", actionLog.get(1).getType());
	assertEquals("World", actionLog.get(1).getActor());
	assertEquals("Approved add_location Garden: North Arbor", actionLog.get(1).getSummary());
	assertEquals("Garden", actionLog.get(1).getFromLocation());
	assertEquals("Garden: North Arbor", actionLog.get(1).getToLocation());
	assertTrue(service.getWorldSnapshot().getPendingProposals().stream().noneMatch(candidate -> candidate.getId().equals(proposal.getId())));
	assertTrue(world.getLocation("Garden: North Arbor").isPresent());
    }

    @Test
    public void test_reject_world_proposal_clears_pending_review_without_applying_world_change() throws Exception {
	createLocation("Garden");

	WorldProposal proposal = newProposal("add_location", "Garden", "North Arbor", "ready", "To keep the watering route organized.");
	assertTrue(isProposalValid(proposal));
	addPendingProposal(proposal);

	WorldSnapshotResponse.WorldProposalResponse rejected = service.rejectWorldProposal(proposal.getId());

	assertEquals("rejected", rejected.getStatus());
	assertTrue(world.getLocation("Garden: North Arbor").isEmpty());
	assertTrue(service.getWorldProposals().stream().noneMatch(candidate -> candidate.getId().equals(proposal.getId())));
	assertTrue(service.getWorldSnapshot().getPendingProposals().stream().noneMatch(candidate -> candidate.getId().equals(proposal.getId())));
	assertTrue(service.getWorldSnapshot().getLocations().stream().noneMatch(location -> location.getName().equals("Garden: North Arbor")));
    }

    @Test
    public void test_reject_world_proposal_records_rejection_action_in_snapshot_history() throws Exception {
	createLocation("Garden");

	WorldProposal proposal = newProposal("add_location", "Garden", "North Arbor", "ready", "To keep the watering route organized.");
	assertTrue(isProposalValid(proposal));
	addPendingProposal(proposal);
	int initialActionCount = service.getWorldSnapshot().getActionLog().size();

	service.rejectWorldProposal(proposal.getId());

	List<WorldSnapshotResponse.ActionLogResponse> actionLog = service.getWorldSnapshot().getActionLog();
	assertEquals(initialActionCount + 1, actionLog.size());
	assertEquals("proposal-rejected", actionLog.get(0).getType());
	assertEquals("World", actionLog.get(0).getActor());
	assertEquals("Rejected add_location Garden: North Arbor", actionLog.get(0).getSummary());
	assertEquals("Garden", actionLog.get(0).getFromLocation());
	assertEquals("Garden: North Arbor", actionLog.get(0).getToLocation());
	assertTrue(service.getWorldSnapshot().getPendingProposals().stream().noneMatch(candidate -> candidate.getId().equals(proposal.getId())));
	assertTrue(world.getLocation("Garden: North Arbor").isEmpty());
    }

    @Test
    public void test_update_state_queues_grounded_pending_proposal_for_eligible_agent() {
	stubRuntimeProposalResponse(
	    "Answer: Yes\n"
		+ "Type: add_object\n"
		+ "ParentLocation: Blue House: Kitchen\n"
		+ "Name: Tea Tray Shelf\n"
		+ "ProposedState: organized\n"
		+ "Reason: To keep the tea setup tidy during the morning round.");

	createProposalAgent("Rowan", true);

	assertDoesNotThrow(() -> service.updateState());

	List<WorldSnapshotResponse.WorldProposalResponse> rowanProposals = service.getWorldProposals().stream()
	    .filter(proposal -> proposal.getAgent().equals("Rowan"))
	    .toList();

	assertEquals(1, rowanProposals.size());
	assertEquals("pending", rowanProposals.get(0).getStatus());
	assertEquals("add_object", rowanProposals.get(0).getType());
	assertEquals("Blue House: Kitchen", rowanProposals.get(0).getParentLocation());
	assertEquals("Tea Tray Shelf", rowanProposals.get(0).getName());
	assertEquals("To keep the tea setup tidy during the morning round.", rowanProposals.get(0).getReason());
    }

    @Test
    public void test_existing_pending_proposal_suppresses_another_runtime_proposal_from_same_agent() throws Exception {
	stubRuntimeProposalResponse(
	    "Answer: Yes\n"
		+ "Type: add_object\n"
		+ "ParentLocation: Blue House: Kitchen\n"
		+ "Name: Backup Tea Shelf\n"
		+ "ProposedState: organized\n"
		+ "Reason: To keep backup cups nearby.");

	createProposalAgent("Rowan", true);
	WorldProposal existing = newProposal("add_object", "Blue House: Kitchen", "Tea Tray Shelf", null, "To keep the tea setup tidy during the morning round.");
	existing.setAgent("Rowan");
	addPendingProposal(existing);

	assertDoesNotThrow(() -> service.updateState());

	List<WorldSnapshotResponse.WorldProposalResponse> rowanProposals = service.getWorldProposals().stream()
	    .filter(proposal -> proposal.getAgent().equals("Rowan"))
	    .toList();

	assertEquals(1, rowanProposals.size());
	assertEquals("Tea Tray Shelf", rowanProposals.get(0).getName());
	assertEquals("pending", rowanProposals.get(0).getStatus());
    }

    @Test
    public void test_existing_approved_proposal_suppresses_another_runtime_proposal_from_same_agent() throws Exception {
	stubRuntimeProposalResponse(
	    "Answer: Yes\n"
		+ "Type: add_object\n"
		+ "ParentLocation: Blue House: Kitchen\n"
		+ "Name: Backup Tea Shelf\n"
		+ "ProposedState: organized\n"
		+ "Reason: To keep backup cups nearby.");

	createProposalAgent("Rowan", true);
	WorldProposal existing = newProposal("add_object", "Blue House: Kitchen", "Tea Tray Shelf", null, "To keep the tea setup tidy during the morning round.");
	existing.setAgent("Rowan");
	existing.setStatus("approved");
	addPendingProposal(existing);

	assertDoesNotThrow(() -> service.updateState());

	List<WorldSnapshotResponse.WorldProposalResponse> rowanProposals = service.getWorldProposals().stream()
	    .filter(proposal -> proposal.getAgent().equals("Rowan"))
	    .toList();

	assertEquals(1, rowanProposals.size());
	assertEquals("Tea Tray Shelf", rowanProposals.get(0).getName());
	assertEquals("approved", rowanProposals.get(0).getStatus());
    }

    @Test
    public void test_update_state_does_not_queue_proposal_for_agent_without_capability() {
	stubRuntimeProposalResponse(
	    "Answer: Yes\n"
		+ "Type: add_object\n"
		+ "ParentLocation: Blue House: Kitchen\n"
		+ "Name: Tea Tray Shelf\n"
		+ "ProposedState: organized\n"
		+ "Reason: To keep the tea setup tidy during the morning round.");

	createProposalAgent("Rowan", false);

	assertDoesNotThrow(() -> service.updateState());

	assertEquals(0, service.getWorldProposals().stream()
	    .filter(proposal -> proposal.getAgent().equals("Rowan"))
	    .count());
    }

    @Test
    public void test_update_state_discards_incomplete_proposal_output() {
	stubRuntimeProposalResponse(
	    "Answer: Yes\n"
		+ "Type: add_object\n"
		+ "ParentLocation: Blue House: Kitchen\n"
		+ "Name: Tea Tray Shelf\n"
		+ "ProposedState: organized");

	createProposalAgent("Rowan", true);

	Logger logger = Logger.getLogger(SimulationService.class);
	CapturingAppender appender = new CapturingAppender();
	logger.addAppender(appender);

	try {
	    assertDoesNotThrow(() -> service.updateState());

	    assertEquals(0, service.getWorldProposals().stream()
		.filter(proposal -> proposal.getAgent().equals("Rowan"))
		.count());
	    assertTrue(appender.messages.stream().anyMatch(message -> message.contains("[ProposalReview] agent=[Rowan] outcome=[dropped_proposal] detail=[missing_reason]")));
	} finally {
	    logger.removeAppender(appender);
	}
    }

    @Test
    public void test_update_state_logs_explicit_no_proposal_attempt() {
	stubRuntimeProposalResponse("Answer: No");
	createProposalAgent("Rowan", true);

	Logger logger = Logger.getLogger(SimulationService.class);
	CapturingAppender appender = new CapturingAppender();
	logger.addAppender(appender);

	try {
	    assertDoesNotThrow(() -> service.updateState());

	    assertEquals(0, service.getWorldProposals().stream()
		.filter(proposal -> proposal.getAgent().equals("Rowan"))
		.count());
	    assertTrue(appender.messages.stream().anyMatch(message -> message.contains("[ProposalReview] agent=[Rowan] outcome=[no_proposal_attempt] detail=[explicit_no]")));
	} finally {
	    logger.removeAppender(appender);
	}
    }

    @Test
    public void test_update_state_logs_parser_rejected_proposal_attempt_when_no_fields_are_parseable() {
	stubRuntimeProposalResponse("I might tidy the kitchen later.");
	createProposalAgent("Rowan", true);

	Logger logger = Logger.getLogger(SimulationService.class);
	CapturingAppender appender = new CapturingAppender();
	logger.addAppender(appender);

	try {
	    assertDoesNotThrow(() -> service.updateState());

	    assertEquals(0, service.getWorldProposals().stream()
		.filter(proposal -> proposal.getAgent().equals("Rowan"))
		.count());
	    assertTrue(appender.messages.stream().anyMatch(message -> message.contains("[ProposalReview] agent=[Rowan] outcome=[parser_rejected] detail=[parser_rejected]")));
	} finally {
	    logger.removeAppender(appender);
	}
    }

    @Test
    public void test_update_state_normalizes_answer_line_type_into_queued_proposal() {
	stubRuntimeProposalResponse(
	    "Answer: add_location\n"
		+ "ParentLocation: Green House\n"
		+ "Name: Compost Bin\n"
		+ "ProposedState: partially filled with finished compost\n"
		+ "Reason: to reduce waste and create a steady supply of nutrient-rich compost for future propagation rounds.");
	createProposalAgent("Rowan", true);

	Logger logger = Logger.getLogger(SimulationService.class);
	CapturingAppender appender = new CapturingAppender();
	logger.addAppender(appender);

	try {
	    assertDoesNotThrow(() -> service.updateState());

	    assertEquals(1, service.getWorldProposals().stream()
		.filter(proposal -> proposal.getAgent().equals("Rowan"))
		.count());
	    assertTrue(appender.messages.stream().anyMatch(message -> message.contains("[ProposalReview] agent=[Rowan] outcome=[queued_proposal] detail=[pending_review; normalized_type_from_answer]")));
	} finally {
	    logger.removeAppender(appender);
	}
    }

    @Test
    public void test_update_state_normalizes_missing_answer_into_yes_for_grounded_type() {
	stubRuntimeProposalResponse(
	    "Type: add_object\n"
		+ "ParentLocation: Blue House: Kitchen\n"
		+ "Name: Tea Tray Shelf\n"
		+ "ProposedState: organized\n"
		+ "Reason: To keep the tea setup tidy during the morning round.");
	createProposalAgent("Rowan", true);

	Logger logger = Logger.getLogger(SimulationService.class);
	CapturingAppender appender = new CapturingAppender();
	logger.addAppender(appender);

	try {
	    assertDoesNotThrow(() -> service.updateState());

	    assertEquals(1, service.getWorldProposals().stream()
		.filter(proposal -> proposal.getAgent().equals("Rowan"))
		.count());
	    assertTrue(appender.messages.stream().anyMatch(message -> message.contains("[ProposalReview] agent=[Rowan] outcome=[queued_proposal] detail=[pending_review; normalized_yes_from_missing_answer]")));
	} finally {
	    logger.removeAppender(appender);
	}
    }

    @Test
    public void test_update_state_keeps_invalid_answer_family_as_malformed_response() {
	stubRuntimeProposalResponse(
	    "Answer: Maybe\n"
		+ "Type: add_location\n"
		+ "ParentLocation: Green House\n"
		+ "Name: Compost Bin\n"
		+ "ProposedState: partially filled with finished compost\n"
		+ "Reason: to reduce waste and create a steady supply of nutrient-rich compost for future propagation rounds.");
	createProposalAgent("Rowan", true);

	Logger logger = Logger.getLogger(SimulationService.class);
	CapturingAppender appender = new CapturingAppender();
	logger.addAppender(appender);

	try {
	    assertDoesNotThrow(() -> service.updateState());

	    assertEquals(0, service.getWorldProposals().stream()
		.filter(proposal -> proposal.getAgent().equals("Rowan"))
		.count());
	    assertTrue(appender.messages.stream().anyMatch(message -> message.contains("[ProposalReview] agent=[Rowan] outcome=[malformed_response] detail=[invalid_answer]")));
	} finally {
	    logger.removeAppender(appender);
	}
    }

    @Test
    public void test_update_state_logs_dropped_proposal_when_validation_fails_after_parse() {
	stubRuntimeProposalResponse(
	    "Answer: Yes\n"
		+ "Type: add_location\n"
		+ "ParentLocation: Green House: Water Barrel\n"
		+ "Name: Rain Barrel Alcove\n"
		+ "ProposedState: ready\n"
		+ "Reason: To keep spare barrels together.");
	createProposalAgent("Rowan", true);

	Logger logger = Logger.getLogger(SimulationService.class);
	CapturingAppender appender = new CapturingAppender();
	logger.addAppender(appender);

	try {
	    assertDoesNotThrow(() -> service.updateState());

	    assertEquals(0, service.getWorldProposals().stream()
		.filter(proposal -> proposal.getAgent().equals("Rowan"))
		.count());
	    assertTrue(appender.messages.stream().anyMatch(message -> message.contains("[ProposalReview] agent=[Rowan] outcome=[dropped_proposal] detail=[invalid_add_location_parent]")));
	} finally {
	    logger.removeAppender(appender);
	}
    }

    private void createLocation(String name) {
	if (world.getLocation(name).isPresent()) {
	    return;
	}

	CreateLocationRequest request = new CreateLocationRequest();
	request.setName(name);
	service.createLocation(request);
    }

    private void createProposalAgent(String name, boolean canProposeWorldChanges) {
	CreateAgentRequest createAgent = new CreateAgentRequest();
	createAgent.setActivity("organizing the tea things");
	createAgent.setLocation("Blue House: Kitchen");
	createAgent.setMemories(List.of(name + " keeps the kitchen orderly"));
	createAgent.setGoals(List.of("keep the kitchen orderly"));
	createAgent.setRituals(List.of("morning tea setup"));
	createAgent.setTraits("steady, practical, observant");
	createAgent.setName(name);
	createAgent.setCanProposeWorldChanges(canProposeWorldChanges);
	service.createAgent(createAgent);
	}

	private void stubRuntimeProposalResponse(String proposalResponse) {
	Mockito.when(llm.sendChat(Mockito.any(), Mockito.anyDouble())).thenAnswer(invocation -> {
	    PromptRequest prompt = invocation.getArgument(0);
	    String content = prompt.getContent();

	    if (content.contains("On the scale of 1 to 10")) {
		return "[1,1,1,1,1,1]";
	    }

	    if (content.contains("Break down") && content.contains("plan for the next hour")) {
		return "8:00 am at the Blue House: Kitchen, organize the tea tray";
	    }

	    if (content.contains("Today is Wednesday February 13")) {
		return "8:00 am at the Blue House: Kitchen, wake up and open the curtains\n"
		    + "9:00 am at the Blue House: Kitchen, prepare tea for the morning round";
	    }

	    if (content.contains("What is your activity at")) {
		return "Activity: organizing the tea tray\n"
		    + "Location: Blue House: Kitchen\n"
		    + "Emoji: tea";
	    }

	    if (content.contains("Given only the information above, what are 3 most salient high-level questions")) {
		return "- What should Rowan tend next?\n- Who needs tea next?\n- Which part of the kitchen needs organizing?";
	    }

	    if (content.contains("What single high-level insight can you infer")) {
		return "Rowan keeps the kitchen orderly (because of 1, 2, 3)";
	    }

	    if (content.contains("wants to propose one grounded world change")) {
		return proposalResponse;
	    }

	    return "result";
	});
	}

    private WorldProposal newProposal(String type, String parentLocation, String name, String proposedState, String reason) {
	WorldProposal proposal = new WorldProposal();
	proposal.setId(UUID.randomUUID().toString());
	proposal.setAgent("Jamie");
	proposal.setType(type);
	proposal.setParentLocation(parentLocation);
	proposal.setName(name);
	proposal.setProposedState(proposedState);
	proposal.setReason(reason);
	proposal.setStatus("pending");
	proposal.setCreatedAtTick(0);
	return proposal;
    }

    private boolean isProposalValid(WorldProposal proposal) throws Exception {
	Method method = SimulationService.class.getDeclaredMethod("isProposalValid", WorldProposal.class);
	method.setAccessible(true);
	return (boolean) method.invoke(service, proposal);
    }

    @SuppressWarnings("unchecked")
    private void addPendingProposal(WorldProposal proposal) throws Exception {
	Field field = SimulationService.class.getDeclaredField("proposals");
	field.setAccessible(true);
	List<WorldProposal> proposals = (List<WorldProposal>) field.get(service);
	assertNotNull(proposals);
	proposals.add(proposal);
    }

    private static class CapturingAppender extends AppenderSkeleton {
	private final List<String> messages = new ArrayList<String>();

	@Override
	protected void append(LoggingEvent event) {
	    messages.add(String.valueOf(event.getRenderedMessage()));
	}

	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
	    return false;
	}
    }
}
