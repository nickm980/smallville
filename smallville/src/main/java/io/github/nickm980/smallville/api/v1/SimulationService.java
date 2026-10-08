package io.github.nickm980.smallville.api.v1;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.nickm980.smallville.World;
import io.github.nickm980.smallville.api.v1.dto.AgentStateResponse;
import io.github.nickm980.smallville.api.v1.dto.ConversationResponse;
import io.github.nickm980.smallville.api.v1.dto.CreateAgentRequest;
import io.github.nickm980.smallville.api.v1.dto.CreateLocationRequest;
import io.github.nickm980.smallville.api.v1.dto.CreateMemoryRequest;
import io.github.nickm980.smallville.api.v1.dto.ImportAgentsRequest;
import io.github.nickm980.smallville.api.v1.dto.AgentMemoryLedgerResponse;
import io.github.nickm980.smallville.api.v1.dto.ImportAgentsResponse;
import io.github.nickm980.smallville.api.v1.dto.LedgerExportResponse;
import io.github.nickm980.smallville.api.v1.dto.LocationStateResponse;
import io.github.nickm980.smallville.api.v1.dto.MemoryResponse;
import io.github.nickm980.smallville.api.v1.dto.ModelMapper;
import io.github.nickm980.smallville.api.v1.dto.ModelsResponse;
import io.github.nickm980.smallville.api.v1.dto.SetAgentModelRequest;
import io.github.nickm980.smallville.api.v1.dto.SetDefaultModelRequest;
import io.github.nickm980.smallville.api.v1.dto.SetTimestepRequest;
import io.github.nickm980.smallville.api.v1.dto.WorldSnapshotResponse;
import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.config.simulation.SimulationFile;
import io.github.nickm980.smallville.config.simulation.SimulationFile.AgentSeed;
import io.github.nickm980.smallville.config.simulation.SimulationFile.DailyRhythmSeed;
import io.github.nickm980.smallville.config.simulation.SimulationFile.LocationSeed;
import io.github.nickm980.smallville.config.simulation.SimulationFile.WorldBuildingSeed;
import io.github.nickm980.smallville.entities.Agent;
import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.entities.SimulationTime;
import io.github.nickm980.smallville.entities.WorldProposal;
import io.github.nickm980.smallville.exceptions.AgentNotFoundException;
import io.github.nickm980.smallville.exceptions.LocationNotFoundException;
import io.github.nickm980.smallville.exceptions.SmallvilleException;
import io.github.nickm980.smallville.llm.LLM;
import io.github.nickm980.smallville.memory.Characteristic;
import io.github.nickm980.smallville.memory.Memory;
import io.github.nickm980.smallville.memory.MemoryStream;
import io.github.nickm980.smallville.memory.Observation;
import io.github.nickm980.smallville.memory.Plan;
import io.github.nickm980.smallville.memory.PlanType;
import io.github.nickm980.smallville.memory.dreaming.DreamingService;
import io.github.nickm980.smallville.memory.dreaming.PonderService;
import io.github.nickm980.smallville.memory.dreaming.RecallService;
import io.github.nickm980.smallville.memory.ledger.DreamPack;
import io.github.nickm980.smallville.memory.ledger.LedgerEntryType;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerEntry;
import io.github.nickm980.smallville.memory.ledger.MemoryLedgerIndex;
import io.github.nickm980.smallville.memory.ledger.MemoryJournalWriter;
import io.github.nickm980.smallville.prompts.dto.WorldProposalCandidate;
import io.github.nickm980.smallville.runtime.RuntimeSettingsService;
import io.github.nickm980.smallville.update.UpdateService;

public class SimulationService {

    private static final Set<String> ALLOWED_PROPOSAL_TYPES = Set.of("add_location", "add_object", "change_state");
    private static final Set<String> OBJECT_LIKE_ADD_LOCATION_PARENT_TOKENS = Set.of(
	"bench",
	"barrel",
	"table",
	"tray",
	"gate",
	"tool",
	"tools",
	"shelf",
	"shelves",
	"pot",
	"pots",
	"planter",
	"planters",
	"rack",
	"racks"
    );
    private static final Set<String> PENDING_PROPOSAL_STATUSES = Set.of("pending", "approved");
    private static final String PROPOSAL_REVIEW_PREFIX = "[ProposalReview]";

    private final Logger LOG = LoggerFactory.getLogger(SimulationService.class);
    private final ModelMapper mapper;
    private final UpdateService prompts;
    private final World world;
    private final RuntimeSettingsService runtimeSettings;
    private final AskShadowBridgeClient askShadowBridgeClient;
    private final List<WorldSnapshotResponse.ActionLogResponse> actionLog;
    private final List<WorldProposal> proposals;
    private final Map<UUID, MemoryStream> memories;
    private int progress;
    private SimulationFile simulationSeed;
    private final MemoryJournalWriter journalWriter;
    private final DreamingService dreamingService;
    private final PonderService ponderService;
    private final RecallService recallService;

    public SimulationService(LLM llm, World world) {
	this(llm, world, null, new RuntimeSettingsService(SmallvilleConfig.getConfig()));
    }

    public SimulationService(LLM llm, World world, AskShadowBridgeClient askShadowBridgeClient) {
	this(llm, world, askShadowBridgeClient, new RuntimeSettingsService(SmallvilleConfig.getConfig()));
    }

    private SimulationService(LLM llm, World world, AskShadowBridgeClient askShadowBridgeClient, RuntimeSettingsService runtimeSettings) {
	this.world = world;
	this.mapper = new ModelMapper();
	this.actionLog = new ArrayList<WorldSnapshotResponse.ActionLogResponse>();
	this.proposals = new ArrayList<WorldProposal>();
	this.memories = new HashMap<UUID, MemoryStream>();
	this.progress = 0;
	this.runtimeSettings = runtimeSettings;
	this.askShadowBridgeClient = askShadowBridgeClient == null
	    ? AskShadowBridgeClient.fromRuntimeSettings(this.runtimeSettings)
	    : askShadowBridgeClient;
	this.journalWriter = new MemoryJournalWriter(".smallville-memory");
	this.dreamingService = new DreamingService(journalWriter);
	this.ponderService = new PonderService(journalWriter);
	this.recallService = new RecallService(journalWriter);
	this.simulationSeed = loadSimulationSeedDefinition();
	this.prompts = new UpdateService(llm, world, simulationSeed, runtimeSettings);

	if (simulationSeed != null) {
	    loadSimulationSeed();
	}

	restoreMemoryLedgers();
    }

    private SimulationFile loadSimulationSeedDefinition() {
	if (!SmallvilleConfig.getConfig().isSimulationFile()) {
	    return null;
	}

	SimulationFile simulation = SmallvilleConfig.loadYamlFile("simulation.yaml", SimulationFile.class);
	if (simulation == null) {
	    LOG.warn("simulationFile is enabled, but simulation.yaml could not be loaded.");
	}

	return simulation;
    }

    private void loadSimulationSeed() {
	if (simulationSeed.getWorld() != null && simulationSeed.getWorld().getLocations() != null) {
	    for (LocationSeed location : simulationSeed.getWorld().getLocations()) {
		createSeedLocation(null, location);
	    }
	}

	if (simulationSeed.getAgents() != null) {
	    for (AgentSeed agent : simulationSeed.getAgents()) {
		createSeedAgent(agent);
	    }
	}

	LOG.info("Loaded simulation seed with {} locations and {} agents.", world.getLocations().size(), world.getAgents().size());
    }

    private void restoreMemoryLedgers() {
	for (Agent agent : world.getAgents()) {
	    String name = agent.getFullName();
	    if (!journalWriter.hasPersistedData(name)) {
		continue;
	    }

	    MemoryLedgerIndex index = journalWriter.loadOrCreateIndex(name);

	    // Restore affect state from last dream cycle
	    if (index.getLastAffect() != null) {
		agent.setAffect(index.getLastAffect());
	    }

	    // Restore hot memories as observations into memory stream
	    List<MemoryLedgerEntry> recentEntries = journalWriter.loadRecentEntriesByType(name, LedgerEntryType.OBSERVATION, index.getHotCount() > 0 ? index.getHotCount() : 20);
	    for (MemoryLedgerEntry entry : recentEntries) {
		if (entry.getDescription() != null && !entry.getDescription().isBlank()) {
		    agent.getMemoryStream().add(new Observation(entry.getDescription()));
		}
	    }

	    // Restore last dream tick so the dreaming service knows when the last cycle ran
	    if (index.getLastDreamTick() > 0) {
		dreamingService.restoreLastDreamTick(name, index.getLastDreamTick());
	    }

	    // Restore last ponder tick so the ponder service knows when the last ponder ran
	    if (index.getLastPonderTick() > 0) {
		ponderService.restoreLastPonderTick(name, index.getLastPonderTick());
	    }

	    LOG.info("Restored memory ledger for {}: {} hot memories, {} dream packs, affect={}",
		name, index.getHotCount(), index.getDreamPackCount(),
		index.getLastAffect() != null ? index.getLastAffect().getMoodLabel() : "neutral");
	}
    }

    private void createSeedLocation(String parentPath, LocationSeed seed) {
	if (seed == null || seed.getName() == null || seed.getName().isBlank()) {
	    return;
	}

	String path = buildLocationPath(parentPath, seed.getName());

	if (world.getLocation(path).isEmpty()) {
	    CreateLocationRequest request = new CreateLocationRequest();
	    request.setName(path);
	    createLocation(request);
	}

	if (seed.getState() != null && !seed.getState().isBlank()) {
	    setState(path, seed.getState());
	}

	if (seed.getObjects() != null) {
	    for (LocationSeed child : seed.getObjects()) {
		createSeedLocation(path, child);
	    }
	}
    }

    private void createSeedAgent(AgentSeed seed) {
	if (seed == null) {
	    return;
	}

	CreateAgentRequest request = new CreateAgentRequest();
	request.setName(seed.getName());
	request.setActivity(seed.getActivity());
	request.setLocation(seed.getLocation());
	request.setMemories(seed.getPersona() != null ? seed.getPersona() : seed.getMemories());
	request.setWorkingMemories(seed.getWorkingMemories());
	request.setTraits(seed.getTraits());
	request.setGoals(seed.getGoals());
	request.setRituals(seed.getRituals());
	request.setModel(seed.getModel());
	request.setSocialPreference(seed.getSocialPreference());
	request.setCanProposeWorldChanges(seed.isCanProposeWorldChanges());
	createAgent(request);
    }

    public void createMemory(CreateMemoryRequest request) {
	Agent agent = world.getAgent(request.getName()).orElseThrow(() -> new AgentNotFoundException(request.getName()));
	Observation observation = new Observation(request.getDescription());
	observation.setReactable(request.isReactable());
	agent.getMemoryStream().add(observation);

	if (observation.isReactable()) {
	    SimulationTime.update();
	    prompts.react(agent, observation.getDescription());
	}
    }

    public AgentStateResponse getAgentState(String name) {
	Agent agent = world.getAgent(name).orElseThrow(() -> new AgentNotFoundException(name));
	return mapper.fromAgent(agent);
    }

    public List<AgentStateResponse> getAgents() {
	return world.getAgents().stream().map(mapper::fromAgent).collect(Collectors.toList());
    }

    public List<LocationStateResponse> getAllLocations() {
	List<LocationStateResponse> result = new ArrayList<LocationStateResponse>();

	for (Location location : world.getLocations()) {
	    result.add(mapper.fromLocation(location));
	}

	return result;
    }

    public ModelsResponse getModels() {
	ModelsResponse response = new ModelsResponse();
	response.setProviderMode(runtimeSettings.getProviderMode());
	response.setDefaultModel(runtimeSettings.getDefaultModel());
	response.setAvailableModels(runtimeSettings.discoverModels());
	response.setAgentModelOverrides(runtimeSettings.getAgentModelOverrides());
	response.setAskShadowBridge(buildAskShadowBridgeResponse());
	return response;
    }

    private ModelsResponse.AskShadowBridgeResponse buildAskShadowBridgeResponse() {
	ModelsResponse.AskShadowBridgeResponse response = new ModelsResponse.AskShadowBridgeResponse();
	response.setEnabled(runtimeSettings.isAskShadowBridgeEnabled());
	response.setEndpoint(runtimeSettings.getAskShadowBridgeEndpoint());
	response.setConnectTimeoutMs(runtimeSettings.getAskShadowBridgeConnectTimeoutMs());
	response.setCallTimeoutMs(runtimeSettings.getAskShadowBridgeCallTimeoutMs());
	return response;
    }

    public void setDefaultModel(SetDefaultModelRequest request) {
	runtimeSettings.setDefaultModel(normalizeModelName(request.getModel()));
	if (request.getProviderMode() != null && !request.getProviderMode().isBlank()) {
	    runtimeSettings.setProviderMode(request.getProviderMode().trim());
	}
	recordAction("World", "model-default", "Default model is now " + runtimeSettings.getDefaultModel(), null, null);
    }

    public void setAgentModel(String agentName, SetAgentModelRequest request) {
	world.getAgent(agentName).orElseThrow(() -> new AgentNotFoundException(agentName));
	String model = normalizeModelName(request.getModel());
	runtimeSettings.setAgentModelOverride(agentName, model);
	recordAction(agentName, "model-override", buildAgentModelSummary(agentName, model), null, null);
    }

    public void createAgent(CreateAgentRequest request) {
	List<String> personaMemories = request.getMemories() == null ? List.of() : request.getMemories();
	List<Characteristic> characteristics = personaMemories
	    .stream()
	    .filter(Objects::nonNull)
	    .map(String::trim)
	    .filter(memory -> !memory.isBlank())
	    .map(Characteristic::new)
	    .collect(Collectors.toList());
	Location location = world.resolveLocation(request.getLocation()).orElse(null);

	if (location == null) {
	    LOG.error("Could not find location {}", request.getLocation());
	    throw new LocationNotFoundException(request.getLocation());
	}

	Agent agent = new Agent(request.getName(), characteristics, request.getActivity(), location);
	seedAgentProfile(agent, request);

	if (world.create(agent)) {
	    seedWorkingMemories(agent, request.getWorkingMemories());
	    try {
		String traits = request.getTraits();
		if (traits == null || traits.isBlank()) {
		    traits = prompts.createTraitsWithCharacteristics(agent);
		}
		agent.setTraits(traits);
	    } catch (Exception e) {
		LOG.warn("Falling back to default traits for {} because the LLM traits request failed.", request.getName());
		agent.setTraits(buildFallbackTraits(request));
	    }
	}
    }

    private void seedAgentProfile(Agent agent, CreateAgentRequest request) {
	agent.setGoals(normalizeStrings(request.getGoals()));
	agent.setRituals(normalizeStrings(request.getRituals()));
	agent.setModel(normalizeModelName(request.getModel()));
	agent.setSocialPreference(normalizeSocialPreference(request.getSocialPreference()));
	agent.setCanProposeWorldChanges(request.isCanProposeWorldChanges());
    }

    private void seedWorkingMemories(Agent agent, List<String> workingMemories) {
	if (workingMemories == null) {
	    return;
	}

	for (String memory : workingMemories) {
	    if (memory != null && !memory.isBlank()) {
		agent.getMemoryStream().addWorkingMemory(memory);
	    }
	}
    }

    private String buildFallbackTraits(CreateAgentRequest request) {
	if (request.getMemories() == null || request.getMemories().isEmpty()) {
	    return "curious, grounded, adaptable";
	}

	return request
	    .getMemories()
	    .stream()
	    .limit(3)
	    .map(memory -> memory.replace(",", "").trim())
	    .collect(Collectors.joining(", "));
    }

    public ImportAgentsResponse importAgents(ImportAgentsRequest request) {
	ImportAgentsResponse response = new ImportAgentsResponse();
	response.setPreview(request != null && request.isPreview());

	if (request == null || request.getYaml() == null || request.getYaml().isBlank()) {
	    response.setSuccess(false);
	    response.setErrors(List.of("YAML is required."));
	    return response;
	}

	SimulationFile importFile;
	try {
	    importFile = SmallvilleConfig.parseYaml(request.getYaml(), SimulationFile.class);
	} catch (Exception e) {
	    LOG.warn("Failed to parse agent import YAML.", e);
	    response.setSuccess(false);
	    response.setErrors(List.of("Could not parse YAML. Quote values that contain colons, such as location paths."));
	    return response;
	}

	if (importFile == null || importFile.getAgents() == null || importFile.getAgents().isEmpty()) {
	    response.setSuccess(false);
	    response.setErrors(List.of("No agents were found in the YAML."));
	    return response;
	}

	List<String> allErrors = new ArrayList<String>();
	List<ImportAgentsResponse.AgentImportPreview> previews = new ArrayList<ImportAgentsResponse.AgentImportPreview>();
	Set<String> batchNames = new HashSet<String>();

	for (AgentSeed seed : importFile.getAgents()) {
	    ImportAgentsResponse.AgentImportPreview preview = toImportPreview(seed, batchNames);
	    previews.add(preview);
	    allErrors.addAll(preview.getIssues());
	}

	response.setAgents(previews);
	response.setErrors(allErrors);

	if (request.isPreview() || !allErrors.isEmpty()) {
	    response.setSuccess(allErrors.isEmpty());
	    return response;
	}

	List<String> createdAgents = new ArrayList<String>();
	for (AgentSeed seed : importFile.getAgents()) {
	    createSeedAgent(seed);
	    createdAgents.add(seed.getName());
	}

	response.setCreatedAgents(createdAgents);
	response.setSuccess(true);
	recordAction("World", "persona-import", "Imported " + createdAgents.size() + " persona(s) from YAML.", null, null);
	return response;
    }

    private ImportAgentsResponse.AgentImportPreview toImportPreview(AgentSeed seed, Set<String> batchNames) {
	ImportAgentsResponse.AgentImportPreview preview = new ImportAgentsResponse.AgentImportPreview();
	List<String> issues = new ArrayList<String>();

	if (seed == null) {
	    issues.add("Encountered an empty agent entry.");
	    preview.setIssues(issues);
	    return preview;
	}

	preview.setName(seed.getName());
	preview.setLocation(seed.getLocation());
	preview.setActivity(seed.getActivity());
	preview.setModel(runtimeSettings.resolveModel(seed.getName(), normalizeModelName(seed.getModel())));

	if (seed.getName() == null || seed.getName().isBlank()) {
	    issues.add("Agent name is missing.");
	} else {
	    String normalized = seed.getName().trim().toLowerCase(Locale.ENGLISH);
	    if (!batchNames.add(normalized)) {
		issues.add("Duplicate name in import batch: " + seed.getName());
	    }
	    if (world.getAgent(seed.getName()).isPresent()) {
		issues.add("Agent already exists: " + seed.getName());
	    }
	}

	if (seed.getLocation() == null || seed.getLocation().isBlank()) {
	    issues.add("Location is missing for " + safeName(seed.getName()) + ".");
	} else if (world.resolveLocation(seed.getLocation()).isEmpty()) {
	    issues.add("Location does not exist for " + safeName(seed.getName()) + ": " + seed.getLocation());
	}

	if (seed.getActivity() == null || seed.getActivity().isBlank()) {
	    issues.add("Activity is missing for " + safeName(seed.getName()) + ".");
	}

	List<String> persona = seed.getPersona() != null ? seed.getPersona() : seed.getMemories();
	if (persona == null || persona.stream().filter(Objects::nonNull).map(String::trim).noneMatch(text -> !text.isBlank())) {
	    issues.add("Persona memories are missing for " + safeName(seed.getName()) + ".");
	}

	preview.setIssues(issues);
	return preview;
    }

    private String safeName(String name) {
	return name == null || name.isBlank() ? "unnamed agent" : name;
    }

    public void createLocation(CreateLocationRequest request) {
	if (world.getLocation(request.getName()).isPresent()) {
	    throw new SmallvilleException("Location already exists");
	}

	world.create(new Location(request.getName()));
    }

    public List<MemoryResponse> getMemoriesOfAgent(String agentName) {
	return world
	    .getAgent(agentName)
	    .orElseThrow(() -> new AgentNotFoundException(agentName))
	    .getMemoryStream()
	    .getMemories()
	    .stream()
	    .map(mapper::fromMemory)
	    .sorted(Comparator.comparing(MemoryResponse::getTime, Comparator.nullsLast(Comparator.naturalOrder())))
	    .collect(Collectors.toList());
    }

    public String askQuestion(String name, String question) {
	Agent agent = world.getAgent(name).orElseThrow(() -> new AgentNotFoundException(name));
	String answer = prompts.ask(agent, question);
	shadowAskBridge(name, question);
	return answer;
    }

    private void shadowAskBridge(String agentName, String question) {
	if (!askShadowBridgeClient.isEnabled()) {
	    LOG.info("Shadow ask bridge skipped agent={} reason=disabled", agentName);
	    return;
	}

	try {
	    AskShadowBridgeClient.ShadowAskResult result = askShadowBridgeClient.shadowAsk(getWorldSnapshot(), agentName, question);
	    if (result.isAccepted()) {
		LOG.info(
		    "Shadow ask bridge accepted requestId={} agent={} adapter={} model={} auditRef={} latencyMs={}",
		    result.getRequestId(),
		    agentName,
		    result.getSelectedAdapter(),
		    result.getSelectedModel(),
		    result.getAuditRef(),
		    result.getLatencyMs());
		return;
	    }

	    LOG.info("Shadow ask bridge skipped requestId={} agent={} reason={}", result.getRequestId(), agentName, result.getReason());
	} catch (RuntimeException e) {
	    LOG.warn("Shadow ask bridge failed unexpectedly for {}", agentName, e);
	}
    }

    public void updateState() throws SmallvilleException {
	if (world.getAgents().isEmpty()) {
	    throw new SmallvilleException("Must create an agent before changing the state");
	}

	SimulationTime.update();
	progress++;
	Set<String> conversationsStarted = new HashSet<String>();

	for (Agent agent : world.getAgents()) {
	    String oldLocation = agent.getLocation() == null ? null : agent.getLocation().getFullPath();
	    String oldActivity = agent.getCurrentActivity();
	    prompts.updateAgent(agent);
	    String newLocation = agent.getLocation() == null ? null : agent.getLocation().getFullPath();
	    recordAction(agent.getFullName(), "agent-update", buildAgentUpdateSummary(agent, oldActivity, oldLocation), oldLocation, newLocation);
	    maybeStartAmbientConversation(agent, oldActivity, oldLocation, conversationsStarted);
	    maybeQueueWorldProposal(agent);
	}

	// Run dream cycle for eligible agents after all updates
	Set<String> dreamedAgents = runDreamCycles();

	// Run ponder for eligible agents that did not dream this tick
	runPonderCycles(dreamedAgents);
    }

    private Set<String> runDreamCycles() {
	Set<String> dreamed = new HashSet<>();
	SimulationFile.MemorySeed memoryConfig = simulationSeed != null ? simulationSeed.getMemory() : null;
	for (Agent agent : world.getAgents()) {
	    try {
		boolean fired = dreamingService.runDreamCycle(agent, memoryConfig, progress);
		if (fired) {
		    dreamed.add(agent.getFullName());
		}
	    } catch (Exception e) {
		LOG.warn("Dream cycle failed for {}", agent.getFullName(), e);
	    }
	}
	return dreamed;
    }

    private void runPonderCycles(Set<String> dreamedAgents) {
	SimulationFile.MemorySeed memoryConfig = simulationSeed != null ? simulationSeed.getMemory() : null;
	SimulationFile.DailyRhythmSeed rhythm = simulationSeed != null ? simulationSeed.getDailyRhythm() : null;
	for (Agent agent : world.getAgents()) {
	    boolean dreamFired = dreamedAgents.contains(agent.getFullName());
	    try {
		ponderService.runPonder(agent, memoryConfig, rhythm, progress, dreamFired);
	    } catch (Exception e) {
		LOG.warn("Ponder cycle failed for {}", agent.getFullName(), e);
	    }
	}
    }

    public List<ConversationResponse> getConversations() {
	List<ConversationResponse> result = new ArrayList<ConversationResponse>();
	List<Conversation> conversations = world.getConversationsAfter(SimulationTime.now().minus(SimulationTime.getStepDuration()));

	for (Conversation conversation : conversations) {
	    result.addAll(mapper.fromConversation(conversation));
	}

	return result;
    }

    public void setTimestep(SetTimestepRequest request) {
	long durationValue = Long.parseLong(request.getNumOfMinutes());
	Duration duration = Duration.ofMinutes(durationValue);
	SimulationTime.setStep(duration);
    }

    public int getProgress() {
	return progress;
    }

    public void setState(String location, String state) {
	Location target = world.resolveLocation(location).orElseThrow(() -> new LocationNotFoundException(location));
	world.setState(target.getFullPath(), state);
	recordAction("World", "location-state", target.getFullPath() + " is now " + state, target.getFullPath(), target.getFullPath());
    }

    public UUID createMemoryStream() {
	UUID uuid = UUID.randomUUID();
	memories.put(uuid, new MemoryStream());
	return uuid;
    }

    public boolean addMemory(UUID uuid, String memory) {
	MemoryStream stream = memories.get(uuid);
	if (stream == null || memory == null || memory.isBlank()) {
	    return false;
	}

	stream.add(new Observation(memory));
	return true;
    }

    public List<String> getMemories(UUID uuid, String query) {
	MemoryStream stream = memories.get(uuid);
	if (stream == null) {
	    return List.of();
	}

	return stream
	    .getRelevantMemories(query == null ? "" : query)
	    .stream()
	    .map(Memory::getDescription)
	    .collect(Collectors.toList());
    }

    public WorldSnapshotResponse getWorldSnapshot() {
	WorldSnapshotResponse snapshot = new WorldSnapshotResponse();
	snapshot.setStep((int) SimulationTime.getStepDurationInMinutes());
	snapshot.setTick(progress);
	snapshot.setTime(SimulationTime.now().format(DateTimeFormatter.ofPattern(SmallvilleConfig.getConfig().getTimeFormat())));
	snapshot.setLocations(world.getLocations().stream().sorted(Comparator.comparing(Location::getFullPath)).map(this::toWorldLocation).collect(Collectors.toList()));
	snapshot.setAgents(world.getAgents().stream().sorted(Comparator.comparing(Agent::getFullName)).map(this::toWorldAgent).collect(Collectors.toList()));
	snapshot.setConversations(getConversations());
	snapshot.setActionLog(new ArrayList<WorldSnapshotResponse.ActionLogResponse>(actionLog));
	snapshot.setWorldBuilding(toWorldBuildingRules());
	snapshot.setDailyRhythm(toDailyRhythm());
	snapshot.setPendingProposals(getWorldProposals());
	return snapshot;
    }

    public List<WorldSnapshotResponse.WorldProposalResponse> getWorldProposals() {
	return proposals
	    .stream()
	    .filter(proposal -> PENDING_PROPOSAL_STATUSES.contains(proposal.getStatus()))
	    .sorted(Comparator.comparing(WorldProposal::getCreatedAtTick).reversed())
	    .map(this::toWorldProposal)
	    .collect(Collectors.toList());
    }

    public LedgerExportResponse getLedgerExport() {
	LedgerExportResponse response = new LedgerExportResponse();
	response.setGeneratedAtUtc(java.time.Instant.now().toString());
	response.setScenario(simulationSeed != null ? SmallvilleConfig.getConfig().getModel() : "unknown");
	response.setWorld(getWorldSnapshot());

	// Full unwindowed proposal history
	List<WorldSnapshotResponse.WorldProposalResponse> allProposals = proposals
	    .stream()
	    .sorted(Comparator.comparing(WorldProposal::getCreatedAtTick).reversed())
	    .map(this::toWorldProposal)
	    .collect(Collectors.toList());
	response.setGovernanceLedger(allProposals);
	response.setProposalHistoryFull(allProposals);

	// Memory index for each agent
	Map<String, MemoryLedgerIndex> memIndex = new HashMap<>();
	for (Agent agent : world.getAgents()) {
	    memIndex.put(agent.getFullName(), journalWriter.loadOrCreateIndex(agent.getFullName()));
	}
	response.setMemoryIndex(memIndex);

	// Offline policy and governance policy summaries
	response.setOfflinePolicy(Map.of(
	    "offlineMode", false,
	    "loopbackOnly", false
	));
	response.setGovernancePolicy(Map.of(
	    "proposalTypes", new ArrayList<>(ALLOWED_PROPOSAL_TYPES),
	    "pendingStatuses", new ArrayList<>(PENDING_PROPOSAL_STATUSES)
	));

	return response;
    }

    public AgentMemoryLedgerResponse getAgentMemoryLedger(String name) {
	Agent agent = world.getAgent(name).orElseThrow(() -> new AgentNotFoundException(name));
	AgentMemoryLedgerResponse response = new AgentMemoryLedgerResponse();
	response.setAgent(agent.getFullName());

	// Persona anchors
	AgentMemoryLedgerResponse.PersonaAnchors anchors = new AgentMemoryLedgerResponse.PersonaAnchors();
	anchors.setTraits(agent.getTraits());
	anchors.setGoals(new ArrayList<>(agent.getGoals()));
	anchors.setRituals(new ArrayList<>(agent.getRituals()));
	anchors.setPersona(agent.getMemoryStream().getCharacteristics().stream().map(Memory::getDescription).collect(Collectors.toList()));
	anchors.setSocialPreference(agent.getSocialPreference());
	response.setPersonaAnchors(anchors);

	// Affect
	response.setAffect(agent.getAffect());

	// Working memories
	response.setWorking(agent.getMemoryStream().getWorkingMemories().stream().map(Memory::getDescription).collect(Collectors.toList()));

	// Recent memories
	response.setRecent(agent.getMemoryStream().getRecentMemories().stream().map(Memory::getDescription).collect(Collectors.toList()));

	// Archived (all non-working, non-recent)
	response.setArchived(agent.getMemoryStream().getMemories().stream().map(Memory::getDescription).collect(Collectors.toList()));

	// Dream packs
	response.setDreamPacks(journalWriter.loadDreamPacks(agent.getFullName()));

	// Recall policy
	SimulationFile.MemorySeed memCfg = simulationSeed != null ? simulationSeed.getMemory() : new SimulationFile.MemorySeed();
	if (memCfg == null) memCfg = new SimulationFile.MemorySeed();
	response.setRecallPolicy(Map.of(
	    "recallTopK", memCfg.getRecallTopK(),
	    "hotMemoryLimit", memCfg.getHotMemoryLimit(),
	    "dreamIntervalTicks", memCfg.getDreamIntervalTicks()
	));

	return response;
    }

    public WorldSnapshotResponse.WorldProposalResponse approveWorldProposal(String id) {
	WorldProposal proposal = findProposal(id);
	proposal.setStatus("approved");
	recordAction("World", "proposal-approved", "Approved " + describeProposal(proposal), proposal.getParentLocation(), buildProposalLocation(proposal));
	applyProposal(proposal);
	proposal.setStatus("applied");
	recordAction("World", "proposal-applied", "Applied " + describeProposal(proposal), proposal.getParentLocation(), buildProposalLocation(proposal));
	return toWorldProposal(proposal);
    }

    public WorldSnapshotResponse.WorldProposalResponse rejectWorldProposal(String id) {
	WorldProposal proposal = findProposal(id);
	proposal.setStatus("rejected");
	recordAction("World", "proposal-rejected", "Rejected " + describeProposal(proposal), proposal.getParentLocation(), buildProposalLocation(proposal));
	return toWorldProposal(proposal);
    }

    private WorldProposal findProposal(String id) {
	return proposals.stream().filter(proposal -> proposal.getId().equals(id)).findFirst().orElseThrow(() -> new SmallvilleException("Proposal not found"));
    }

    private void applyProposal(WorldProposal proposal) {
	String targetLocation = buildProposalLocation(proposal);
	switch (proposal.getType()) {
	case "add_location":
	case "add_object":
	    if (targetLocation == null || targetLocation.isBlank()) {
		throw new SmallvilleException("Proposal is missing a target location.");
	    }
	    if (world.getLocation(targetLocation).isPresent()) {
		throw new SmallvilleException("Location already exists: " + targetLocation);
	    }
	    world.create(new Location(targetLocation));
	    if (proposal.getProposedState() != null && !proposal.getProposedState().isBlank()) {
		world.setState(targetLocation, proposal.getProposedState());
	    }
	    break;
	case "change_state":
	    Location location = world.resolveLocation(targetLocation).orElseThrow(() -> new LocationNotFoundException(targetLocation));
	    world.setState(location.getFullPath(), proposal.getProposedState());
	    break;
	default:
	    throw new SmallvilleException("Unsupported proposal type: " + proposal.getType());
	}
    }

    private WorldSnapshotResponse.WorldBuildRulesResponse toWorldBuildingRules() {
	WorldSnapshotResponse.WorldBuildRulesResponse result = new WorldSnapshotResponse.WorldBuildRulesResponse();
	if (simulationSeed == null) {
	    return result;
	}

	WorldBuildingSeed rules = simulationSeed.getWorldBuilding();
	if (rules == null) {
	    return result;
	}

	result.setSummary(rules.getSummary() == null ? "" : rules.getSummary());
	result.setRules(rules.getRules() == null ? List.of() : rules.getRules());
	result.setAllowedLocationStates(rules.getAllowedLocationStates() == null ? List.of() : rules.getAllowedLocationStates());
	return result;
    }

    private WorldSnapshotResponse.DailyRhythmResponse toDailyRhythm() {
	WorldSnapshotResponse.DailyRhythmResponse response = new WorldSnapshotResponse.DailyRhythmResponse();
	DailyRhythmSeed rhythm = getDailyRhythm();
	response.setBreakfast(rhythm.getBreakfast());
	response.setLunch(rhythm.getLunch());
	response.setDinner(rhythm.getDinner());
	response.setMorningTea(rhythm.getMorningTea());
	response.setAfternoonTea(rhythm.getAfternoonTea());
	response.setSnack(rhythm.getSnack());
	return response;
    }

    private DailyRhythmSeed getDailyRhythm() {
	if (simulationSeed != null && simulationSeed.getDailyRhythm() != null) {
	    return simulationSeed.getDailyRhythm();
	}

	return new DailyRhythmSeed();
    }

    private WorldSnapshotResponse.WorldLocationResponse toWorldLocation(Location location) {
	WorldSnapshotResponse.WorldLocationResponse result = new WorldSnapshotResponse.WorldLocationResponse();
	result.setName(location.getFullPath());
	result.setState(location.getState());
	result.setDepth(Math.max(location.getAll().size() - 1, 0));
	result.setParent(getParentLocation(location));
	result.setAgents(world.getAgents().stream().filter(agent -> {
	    return agent.getLocation() != null && agent.getLocation().getFullPath().equals(location.getFullPath());
	}).map(Agent::getFullName).sorted().collect(Collectors.toList()));
	return result;
    }

    private String getParentLocation(Location location) {
	List<String> parts = location.getAll();
	if (parts.size() <= 1) {
	    return null;
	}

	return String.join(": ", parts.subList(0, parts.size() - 1));
    }

    private WorldSnapshotResponse.WorldAgentResponse toWorldAgent(Agent agent) {
	WorldSnapshotResponse.WorldAgentResponse result = new WorldSnapshotResponse.WorldAgentResponse();
	result.setName(agent.getFullName());
	result.setAction(agent.getCurrentActivity());
	result.setEmoji(agent.getEmoji());
	result.setLocation(agent.getLocation() == null ? null : agent.getLocation().getFullPath());
	result.setTraits(agent.getTraits());
	result.setModel(runtimeSettings.resolveModel(agent.getFullName(), agent.getModel()));
	result.setSocialPreference(agent.getSocialPreference());
	result.setCanProposeWorldChanges(agent.canProposeWorldChanges());
	result.setGoals(new ArrayList<String>(agent.getGoals()));
	result.setRituals(new ArrayList<String>(agent.getRituals()));
	result.setPersona(agent.getMemoryStream().getCharacteristics().stream().map(Memory::getDescription).collect(Collectors.toList()));
	result.setWorkingMemories(agent.getMemoryStream().getWorkingMemories().stream().sorted(Comparator.comparing(Observation::getTime).reversed()).limit(6).map(Memory::getDescription).collect(Collectors.toList()));
	result.setRecentMemories(agent.getMemoryStream().getObservations().stream().sorted(Comparator.comparing(Observation::getTime).reversed()).limit(6).map(Memory::getDescription).collect(Collectors.toList()));
	result.setShortPlans(agent.getMemoryStream().getPlans().stream().filter(plan -> plan.getType() == PlanType.SHORT_TERM).map(Plan::getDescription).collect(Collectors.toList()));
	result.setLongPlans(agent.getMemoryStream().getPlans().stream().filter(plan -> plan.getType() == PlanType.LONG_TERM).map(Plan::getDescription).collect(Collectors.toList()));

	// Affect summary
	if (agent.getAffect() != null) {
	    WorldSnapshotResponse.AffectSummary affectSummary = new WorldSnapshotResponse.AffectSummary();
	    affectSummary.setMoodLabel(agent.getAffect().getMoodLabel());
	    affectSummary.setValence(agent.getAffect().getValence());
	    affectSummary.setActivation(agent.getAffect().getActivation());
	    result.setAffect(affectSummary);
	}

	// Memory ledger summary
	MemoryLedgerIndex ledgerIndex = journalWriter.loadOrCreateIndex(agent.getFullName());
	WorldSnapshotResponse.MemoryLedgerSummary ledgerSummary = new WorldSnapshotResponse.MemoryLedgerSummary();
	ledgerSummary.setHotCount(ledgerIndex.getHotCount());
	ledgerSummary.setArchivedCount(ledgerIndex.getArchivedCount());
	ledgerSummary.setDreamPackCount(ledgerIndex.getDreamPackCount());
	result.setMemoryLedgerSummary(ledgerSummary);

	return result;
    }

    private WorldSnapshotResponse.WorldProposalResponse toWorldProposal(WorldProposal proposal) {
	WorldSnapshotResponse.WorldProposalResponse response = new WorldSnapshotResponse.WorldProposalResponse();
	response.setId(proposal.getId());
	response.setAgent(proposal.getAgent());
	response.setType(proposal.getType());
	response.setParentLocation(proposal.getParentLocation());
	response.setName(proposal.getName());
	response.setProposedState(proposal.getProposedState());
	response.setReason(proposal.getReason());
	response.setStatus(proposal.getStatus());
	response.setCreatedAtTick(proposal.getCreatedAtTick());
	return response;
    }

    private void recordAction(String actor, String type, String summary, String fromLocation, String toLocation) {
	WorldSnapshotResponse.ActionLogResponse entry = new WorldSnapshotResponse.ActionLogResponse();
	entry.setTick(progress);
	entry.setActor(actor);
	entry.setType(type);
	entry.setSummary(summary);
	entry.setFromLocation(fromLocation);
	entry.setToLocation(toLocation);
	entry.setTime(SimulationTime.now().format(DateTimeFormatter.ofPattern(SmallvilleConfig.getConfig().getTimeFormat())));
	actionLog.add(0, entry);
	if (actionLog.size() > 75) {
	    actionLog.remove(actionLog.size() - 1);
	}
    }

    private void maybeStartAmbientConversation(Agent agent, String oldActivity, String oldLocation, Set<String> conversationsStarted) {
	if (agent.getLocation() == null) {
	    return;
	}

	boolean moved = !Objects.equals(oldLocation, agent.getLocation().getFullPath());
	boolean changedActivity = !Objects.equals(normalizeText(oldActivity), normalizeText(agent.getCurrentActivity()));

	if (!moved && !changedActivity) {
	    return;
	}

	for (Agent other : world.getAgents()) {
	    if (other == agent || other.getLocation() == null) {
		continue;
	    }

	    if (!agent.getLocation().getFullPath().equals(other.getLocation().getFullPath())) {
		continue;
	    }

	    String conversationKey = buildConversationKey(agent, other, agent.getLocation().getFullPath());
	    if (conversationsStarted.contains(conversationKey)) {
		continue;
	    }

	    if (!shouldStartAmbientConversation(agent, other)) {
		continue;
	    }

	    String observation = buildAmbientConversationTopic(agent, other);
	    if (prompts.startAmbientConversation(agent, other, observation)) {
		conversationsStarted.add(conversationKey);
		String location = agent.getLocation().getFullPath();
		recordAction(agent.getFullName(), "conversation", agent.getFullName() + " chose to speak with " + other.getFullName() + " in " + location, location, location);
		return;
	    }
	}
    }

    private boolean shouldStartAmbientConversation(Agent agent, Agent other) {
	double score = 1.0;

	if (hasRecentConversation(agent, other)) {
	    return false;
	}

	if (isQuietOrFocusedLocation(agent.getLocation())) {
	    score -= 1.25;
	}

	if (isUrgentActivity(agent.getCurrentActivity()) || isUrgentActivity(other.getCurrentActivity())) {
	    score -= 1.5;
	}

	score += socialPreferenceScore(agent) + socialPreferenceScore(other);

	if (sharesText(agent.getGoals(), other.getGoals())) {
	    score += 0.75;
	}

	if (sharesText(agent.getRituals(), other.getRituals())) {
	    score += 1.0;
	}

	if (recentlyMentioned(agent, other.getFullName()) || recentlyMentioned(other, agent.getFullName())) {
	    score += 0.75;
	}

	return score >= 1.75;
    }

    private boolean hasRecentConversation(Agent agent, Agent other) {
	LocalDateTime cutoff = SimulationTime.now().minusMinutes(30);
	return world.getConversationsAfter(cutoff).stream().anyMatch(conversation -> {
	    return isConversationPair(conversation, agent.getFullName(), other.getFullName());
	});
    }

    private boolean isConversationPair(Conversation conversation, String first, String second) {
	return (conversation.getTalker().equals(first) && conversation.getTalkee().equals(second))
	    || (conversation.getTalker().equals(second) && conversation.getTalkee().equals(first));
    }

    private boolean isQuietOrFocusedLocation(Location location) {
	if (location == null) {
	    return false;
	}

	String combined = (location.getFullPath() + " " + (location.getState() == null ? "" : location.getState())).toLowerCase(Locale.ENGLISH);
	return combined.contains("quiet")
	    || combined.contains("focused")
	    || combined.contains("focus")
	    || combined.contains("study")
	    || combined.contains("library");
    }

    private boolean isUrgentActivity(String activity) {
	String normalized = normalizeText(activity);
	return normalized.contains("urgent")
	    || normalized.contains("rush")
	    || normalized.contains("hurry")
	    || normalized.contains("late")
	    || normalized.contains("repair")
	    || normalized.contains("fixing");
    }

    private double socialPreferenceScore(Agent agent) {
	String preference = normalizeSocialPreference(agent.getSocialPreference());
	if ("social".equals(preference)) {
	    return 1.0;
	}
	if ("solitary".equals(preference)) {
	    return -0.5;
	}
	return 0.0;
    }

    private boolean sharesText(Collection<String> first, Collection<String> second) {
	Set<String> firstNormalized = normalizeTextSet(first);
	Set<String> secondNormalized = normalizeTextSet(second);
	if (firstNormalized.isEmpty() || secondNormalized.isEmpty()) {
	    return false;
	}

	firstNormalized.retainAll(secondNormalized);
	return !firstNormalized.isEmpty();
    }

    private Set<String> normalizeTextSet(Collection<String> input) {
	if (input == null) {
	    return Collections.emptySet();
	}

	return input
	    .stream()
	    .filter(Objects::nonNull)
	    .map(this::normalizeText)
	    .filter(text -> !text.isBlank())
	    .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean recentlyMentioned(Agent agent, String otherName) {
	String needle = otherName.toLowerCase(Locale.ENGLISH);
	return agent
	    .getMemoryStream()
	    .getObservations()
	    .stream()
	    .sorted(Comparator.comparing(Observation::getTime).reversed())
	    .limit(6)
	    .map(Observation::getDescription)
	    .filter(Objects::nonNull)
	    .map(text -> text.toLowerCase(Locale.ENGLISH))
	    .anyMatch(text -> text.contains(needle));
    }

    private String buildConversationKey(Agent first, Agent second, String location) {
	List<String> names = new ArrayList<String>();
	names.add(first.getFullName());
	names.add(second.getFullName());
	names.sort(String::compareTo);
	return String.join("|", names) + "|" + location;
    }

    private String buildAmbientConversationTopic(Agent agent, Agent other) {
	String location = agent.getLocation().getFullPath();
	String state = agent.getLocation().getState();
	String locationContext = state == null || state.isBlank() ? location : location + " (" + state + ")";
	return agent.getFullName() + " and " + other.getFullName() + " are both at " + locationContext + ". "
	    + agent.getFullName() + " is " + agent.getCurrentActivity() + ". "
	    + other.getFullName() + " is " + other.getCurrentActivity() + ". "
	    + "If they would naturally choose to speak, let them exchange a short grounded conversation. "
	    + "If not, return no dialogue.";
    }

    private void maybeQueueWorldProposal(Agent agent) {
	if (!agent.canProposeWorldChanges()) {
	    return;
	}

	if (simulationSeed == null || simulationSeed.getWorldBuilding() == null) {
	    return;
	}

	boolean hasPendingProposal = proposals
	    .stream()
	    .anyMatch(proposal -> proposal.getAgent().equals(agent.getFullName()) && PENDING_PROPOSAL_STATUSES.contains(proposal.getStatus()));
	if (hasPendingProposal) {
	    return;
	}

	WorldProposalCandidate candidate = prompts.createWorldProposal(agent);
	String preQueueDetail = getProposalPreQueueDetail(candidate);
	if (preQueueDetail != null) {
	    String reviewDetail = buildProposalReviewDetail(preQueueDetail, candidate);
	    if ("explicit_no".equals(preQueueDetail)) {
		logProposalReview(agent, "no_proposal_attempt", reviewDetail, candidate);
	    } else if ("parser_rejected".equals(preQueueDetail)) {
		logProposalReview(agent, "parser_rejected", reviewDetail, candidate);
	    } else {
		logProposalReview(agent, "malformed_response", reviewDetail, candidate);
	    }
	    return;
	}

	WorldProposal proposal = toProposal(agent, candidate);
	if (proposal == null) {
	    logProposalReview(agent, "dropped_proposal", buildProposalReviewDetail("to_proposal_returned_null", candidate), candidate);
	    return;
	}

	String validationFailure = getProposalValidationFailure(proposal);
	if (validationFailure != null) {
	    logProposalReview(agent, "dropped_proposal", buildProposalReviewDetail(validationFailure, candidate), candidate);
	    return;
	}

	proposals.add(proposal);
	recordAction(agent.getFullName(), "proposal", "Proposed " + describeProposal(proposal), proposal.getParentLocation(), buildProposalLocation(proposal));
	logProposalReview(agent, "queued_proposal", buildProposalReviewDetail("pending_review", candidate), candidate);
    }

    private String getProposalPreQueueDetail(WorldProposalCandidate candidate) {
	if (candidate == null) {
	    return "parser_rejected";
	}

	String normalizedAnswer = normalizeProposalAnswer(candidate.getAnswer());
	if ("no".equals(normalizedAnswer)) {
	    return "explicit_no";
	}

	if (normalizedAnswer == null) {
	    return normalizeBlank(candidate.getAnswer()) == null ? "missing_answer" : "invalid_answer";
	}

	if (candidate.getType() == null || candidate.getType().isBlank()) {
	    return "missing_type";
	}

	return null;
    }

    private String buildProposalReviewDetail(String detail, WorldProposalCandidate candidate) {
	String normalizationDetail = normalizeBlank(candidate == null ? null : candidate.getNormalizationDetail());
	if (normalizationDetail == null) {
	    return detail;
	}

	String baseDetail = normalizeBlank(detail);
	if (baseDetail == null) {
	    return normalizationDetail;
	}

	return baseDetail + "; " + normalizationDetail;
    }

    private WorldProposal toProposal(Agent agent, WorldProposalCandidate candidate) {
	WorldProposal proposal = new WorldProposal();
	proposal.setId(UUID.randomUUID().toString());
	proposal.setAgent(agent.getFullName());
	proposal.setType(normalizeProposalType(candidate.getType()));
	proposal.setParentLocation(normalizeBlank(candidate.getParentLocation()));
	proposal.setName(normalizeBlank(candidate.getName()));
	proposal.setProposedState(normalizeBlank(candidate.getProposedState()));
	proposal.setReason(normalizeBlank(candidate.getReason()));
	proposal.setStatus("pending");
	proposal.setCreatedAtTick(progress);
	return proposal;
    }

    private boolean isProposalValid(WorldProposal proposal) {
	return getProposalValidationFailure(proposal) == null;
    }

    private String getProposalValidationFailure(WorldProposal proposal) {
	if (proposal.getType() == null || !ALLOWED_PROPOSAL_TYPES.contains(proposal.getType())) {
	    return "unsupported_type";
	}

	if (proposal.getReason() == null || proposal.getReason().isBlank()) {
	    return "missing_reason";
	}

	if ("change_state".equals(proposal.getType())) {
	    if (proposal.getProposedState() == null || proposal.getProposedState().isBlank()) {
		return "missing_proposed_state";
	    }
	    String targetLocation = buildProposalLocation(proposal);
	    if (targetLocation == null || world.resolveLocation(targetLocation).isEmpty()) {
		return "unknown_change_state_target";
	    }
	    return null;
	}

	if (proposal.getName() == null || proposal.getName().isBlank()) {
	    return "missing_name";
	}

	if ("add_object".equals(proposal.getType()) || (proposal.getParentLocation() != null && !proposal.getParentLocation().isBlank())) {
	    if (proposal.getParentLocation() == null || proposal.getParentLocation().isBlank()) {
		return "missing_parent_location";
	    }
	    if (world.resolveLocation(proposal.getParentLocation()).isEmpty()) {
		return "unknown_parent_location";
	    }
	    if ("add_location".equals(proposal.getType()) && !isValidAddLocationParent(proposal.getParentLocation())) {
		return "invalid_add_location_parent";
	    }
	}

	String targetLocation = buildProposalLocation(proposal);
	if (targetLocation == null || targetLocation.isBlank()) {
	    return "missing_target_location";
	}

	boolean duplicatePending = proposals
	    .stream()
	    .filter(existing -> PENDING_PROPOSAL_STATUSES.contains(existing.getStatus()))
	    .anyMatch(existing -> normalizeText(existing.getType()).equals(normalizeText(proposal.getType()))
		&& normalizeText(buildProposalLocation(existing)).equals(normalizeText(targetLocation)));
	if (duplicatePending) {
	    return "duplicate_pending_target";
	}

	if (world.getLocation(targetLocation).isPresent()) {
	    return "target_location_already_exists";
	}

	return null;
    }

    private boolean isValidAddLocationParent(String parentLocationName) {
	Location parent = world.resolveLocation(parentLocationName).orElse(null);
	if (parent == null) {
	    return false;
	}

	if (parent.getAll().size() <= 1) {
	    return true;
	}

	if (hasDirectChildLocation(parent)) {
	    return true;
	}

	return !isObjectLikeLeaf(parent);
    }

    private boolean hasDirectChildLocation(Location parent) {
	String parentPath = parent.getFullPath();
	return world
	    .getLocations()
	    .stream()
	    .map(Location::getFullPath)
	    .anyMatch(candidatePath -> isDirectChildLocation(parentPath, candidatePath));
    }

    private boolean isDirectChildLocation(String parentPath, String candidatePath) {
	if (candidatePath == null || parentPath == null || candidatePath.equals(parentPath) || !candidatePath.startsWith(parentPath + ":")) {
	    return false;
	}

	String remainder = candidatePath.substring(parentPath.length() + 1).trim();
	return !remainder.isBlank() && !remainder.contains(":");
    }

    private boolean isObjectLikeLeaf(Location location) {
	if (location == null || location.getAll().isEmpty()) {
	    return false;
	}

	String leaf = location.getAll().get(location.getAll().size() - 1).toLowerCase(Locale.ROOT);
	String normalizedLeaf = leaf.replaceAll("[^a-z0-9]+", " ").trim();
	if (normalizedLeaf.isBlank()) {
	    return false;
	}

	for (String token : normalizedLeaf.split("\\s+")) {
	    if (OBJECT_LIKE_ADD_LOCATION_PARENT_TOKENS.contains(token)) {
		return true;
	    }
	}

	return false;
    }

    private String describeProposal(WorldProposal proposal) {
	String target = buildProposalLocation(proposal);
	if ("change_state".equals(proposal.getType())) {
	    return proposal.getType() + " for " + target + " -> " + proposal.getProposedState();
	}
	return proposal.getType() + " " + target;
    }

    private String buildProposalLocation(WorldProposal proposal) {
	if (proposal == null) {
	    return null;
	}

	if ("change_state".equals(proposal.getType())) {
	    if (proposal.getName() != null && world.resolveLocation(proposal.getName()).isPresent()) {
		return world.resolveLocation(proposal.getName()).orElseThrow().getFullPath();
	    }
	    if (proposal.getParentLocation() != null && proposal.getName() != null) {
		String combined = buildLocationPath(proposal.getParentLocation(), proposal.getName());
		if (world.resolveLocation(combined).isPresent()) {
		    return world.resolveLocation(combined).orElseThrow().getFullPath();
		}
	    }
	    return proposal.getParentLocation();
	}

	if (proposal.getParentLocation() == null || proposal.getParentLocation().isBlank()) {
	    return proposal.getName();
	}

	return buildLocationPath(proposal.getParentLocation(), proposal.getName());
    }

    private String buildLocationPath(String parentPath, String name) {
	if (name == null || name.isBlank()) {
	    return normalizeBlank(parentPath);
	}

	String trimmedName = name.trim();
	if (parentPath == null || parentPath.isBlank()) {
	    return trimmedName;
	}

	if (trimmedName.startsWith(parentPath + ":")) {
	    return trimmedName;
	}

	return parentPath + ": " + trimmedName;
    }

    private String buildAgentUpdateSummary(Agent agent, String oldActivity, String oldLocation) {
	String newLocation = agent.getLocation() == null ? "Unknown" : agent.getLocation().getFullPath();
	boolean moved = !Objects.equals(oldLocation, newLocation);
	boolean changedActivity = !Objects.equals(normalizeText(oldActivity), normalizeText(agent.getCurrentActivity()));

	if (moved && changedActivity) {
	    return agent.getFullName() + " moved from " + oldLocation + " to " + newLocation + " and is now " + agent.getCurrentActivity();
	}
	if (moved) {
	    return agent.getFullName() + " moved from " + oldLocation + " to " + newLocation;
	}
	if (changedActivity) {
	    return agent.getFullName() + " is now " + agent.getCurrentActivity() + " at " + newLocation;
	}
	return agent.getFullName() + " stayed at " + newLocation + " and continued " + agent.getCurrentActivity();
    }

    private String buildAgentModelSummary(String agentName, String model) {
	if (model == null || model.isBlank()) {
	    return "Cleared runtime model override for " + agentName;
	}

	return "Set runtime model override for " + agentName + " to " + model;
    }

    private List<String> normalizeStrings(List<String> values) {
	if (values == null) {
	    return new ArrayList<String>();
	}

	return values
	    .stream()
	    .filter(Objects::nonNull)
	    .map(String::trim)
	    .filter(text -> !text.isBlank())
	    .collect(Collectors.toList());
    }

    private String normalizeProposalType(String type) {
	String normalized = normalizeBlank(type);
	if (normalized == null) {
	    return null;
	}

	return normalized.toLowerCase(Locale.ENGLISH).replace(' ', '_');
    }

    private String normalizeProposalAnswer(String answer) {
	String normalized = normalizeBlank(answer);
	if (normalized == null) {
	    return null;
	}

	normalized = normalized.toLowerCase(Locale.ENGLISH).replace(".", "");
	if ("yes".equals(normalized) || "y".equals(normalized)) {
	    return "yes";
	}
	if ("no".equals(normalized) || "n".equals(normalized)) {
	    return "no";
	}
	return null;
    }

    private String normalizeSocialPreference(String preference) {
	String normalized = normalizeBlank(preference);
	if (normalized == null) {
	    return "balanced";
	}

	normalized = normalized.toLowerCase(Locale.ENGLISH);
	if (!Set.of("solitary", "balanced", "social").contains(normalized)) {
	    return "balanced";
	}
	return normalized;
    }

    private String normalizeModelName(String model) {
	String normalized = normalizeBlank(model);
	if (normalized == null) {
	    return null;
	}

	if (normalized.toLowerCase(Locale.ENGLISH).startsWith("gwen")) {
	    return "q" + normalized.substring(1);
	}

	return normalized;
    }

    private String normalizeBlank(String value) {
	if (value == null) {
	    return null;
	}

	String normalized = value.trim();
	return normalized.isBlank() ? null : normalized;
    }

    private void logProposalReview(Agent agent, String outcome, String detail, WorldProposalCandidate candidate) {
	LOG.info(
	    "{} agent=[{}] outcome=[{}] detail=[{}] answer=[{}] type=[{}] parentLocation=[{}] name=[{}] proposedState=[{}] reason=[{}]",
	    PROPOSAL_REVIEW_PREFIX,
	    sanitizeProposalDiagnosticValue(agent == null ? null : agent.getFullName()),
	    sanitizeProposalDiagnosticValue(outcome),
	    sanitizeProposalDiagnosticValue(detail),
	    sanitizeProposalDiagnosticValue(candidate == null ? null : candidate.getAnswer()),
	    sanitizeProposalDiagnosticValue(candidate == null ? null : candidate.getType()),
	    sanitizeProposalDiagnosticValue(candidate == null ? null : candidate.getParentLocation()),
	    sanitizeProposalDiagnosticValue(candidate == null ? null : candidate.getName()),
	    sanitizeProposalDiagnosticValue(candidate == null ? null : candidate.getProposedState()),
	    sanitizeProposalDiagnosticValue(candidate == null ? null : candidate.getReason()));
    }

    private String sanitizeProposalDiagnosticValue(String value) {
	String normalized = normalizeBlank(value);
	if (normalized == null) {
	    return "";
	}

	return normalized
	    .replace("[", "(")
	    .replace("]", ")")
	    .replace("\r", " ")
	    .replace("\n", " | ")
	    .trim();
    }

    private String normalizeText(String value) {
	if (value == null) {
	    return "";
	}

	return value.trim().toLowerCase(Locale.ENGLISH);
    }
}
