package io.github.nickm980.smallville.api.v1;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.nickm980.smallville.api.v1.dto.WorldSnapshotResponse;
import io.github.nickm980.smallville.entities.SimulationTime;
import io.github.nickm980.smallville.runtime.RuntimeSettingsService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AskShadowBridgeClient {
    private static final String BRIDGE_SCHEMA_VERSION = "2026-bridge-v1";
    private static final String DEFAULT_ENDPOINT = "http://127.0.0.1:8010/neural/turn";
    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofMillis(200);
    private static final Duration DEFAULT_CALL_TIMEOUT = Duration.ofMillis(1200);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final boolean enabled;
    private final OkHttpClient client;
    private final String endpoint;
    private final int connectTimeoutMs;
    private final int callTimeoutMs;

    public AskShadowBridgeClient() {
        this(false, DEFAULT_ENDPOINT, DEFAULT_CONNECT_TIMEOUT, DEFAULT_CALL_TIMEOUT);
    }

    public static AskShadowBridgeClient fromRuntimeSettings(RuntimeSettingsService runtimeSettings) {
        return new AskShadowBridgeClient(
            runtimeSettings.isAskShadowBridgeEnabled(),
            runtimeSettings.getAskShadowBridgeEndpoint(),
            runtimeSettings.getAskShadowBridgeConnectTimeout(),
            runtimeSettings.getAskShadowBridgeCallTimeout());
    }

    public AskShadowBridgeClient(String endpoint, Duration connectTimeout, Duration callTimeout) {
        this(true, endpoint, connectTimeout, callTimeout);
    }

    public AskShadowBridgeClient(boolean enabled, String endpoint, Duration connectTimeout, Duration callTimeout) {
        this(
            enabled,
            new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectTimeout(connectTimeout)
                .writeTimeout(callTimeout)
                .readTimeout(callTimeout)
                .callTimeout(callTimeout)
                .build(),
            endpoint,
            (int) connectTimeout.toMillis(),
            (int) callTimeout.toMillis());
    }

    public AskShadowBridgeClient(OkHttpClient client, String endpoint) {
        this(true, client, endpoint, 0, 0);
    }

    public AskShadowBridgeClient(boolean enabled, OkHttpClient client, String endpoint) {
        this(enabled, client, endpoint, 0, 0);
    }

    private AskShadowBridgeClient(boolean enabled, OkHttpClient client, String endpoint, int connectTimeoutMs, int callTimeoutMs) {
        this.enabled = enabled;
        this.client = client;
        this.endpoint = endpoint;
        this.connectTimeoutMs = connectTimeoutMs;
        this.callTimeoutMs = callTimeoutMs;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public int getCallTimeoutMs() {
        return callTimeoutMs;
    }

    public ShadowAskResult shadowAsk(WorldSnapshotResponse snapshot, String agentName, String question) {
        String requestId = "ask-" + UUID.randomUUID();

        if (!enabled) {
            return ShadowAskResult.skipped(requestId, "disabled");
        }

        if (snapshot == null || agentName == null || agentName.isBlank() || question == null || question.isBlank()) {
            return ShadowAskResult.skipped(requestId, "missing_context");
        }

        WorldSnapshotResponse.WorldAgentResponse primaryAgent = snapshot
            .getAgents()
            .stream()
            .filter(agent -> agentName.equals(agent.getName()))
            .findFirst()
            .orElse(null);

        if (primaryAgent == null) {
            return ShadowAskResult.skipped(requestId, "agent_not_in_snapshot");
        }

        try {
            ObjectNode payload = buildPayload(requestId, snapshot, primaryAgent, question);
            RequestBody body = RequestBody.create(
                MAPPER.writeValueAsString(payload),
                okhttp3.MediaType.parse("application/json"));

            Request request = new Request.Builder()
                .url(endpoint)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    return ShadowAskResult.skipped(requestId, "http_" + response.code());
                }

                return parseResponse(requestId, response.body().string());
            }
        } catch (SocketTimeoutException e) {
            return ShadowAskResult.skipped(requestId, "timeout");
        } catch (JsonProcessingException e) {
            return ShadowAskResult.skipped(requestId, "invalid_response");
        } catch (InterruptedIOException e) {
            return ShadowAskResult.skipped(requestId, "timeout");
        } catch (IOException e) {
            return ShadowAskResult.skipped(requestId, "io_failure");
        } catch (RuntimeException e) {
            return ShadowAskResult.skipped(requestId, "invalid_payload");
        }
    }

    private ObjectNode buildPayload(
        String requestId,
        WorldSnapshotResponse snapshot,
        WorldSnapshotResponse.WorldAgentResponse primaryAgent,
        String question) {
        ObjectNode root = MAPPER.createObjectNode();
        root.put("request_id", requestId);
        root.put("schema_version", BRIDGE_SCHEMA_VERSION);
        root.put("mode", "shadow");
        root.put("turn_kind", "ask");
        root.set("world_slice", buildWorldSlice(snapshot, primaryAgent));
        root.set("primary_agent", buildAgentContext(primaryAgent));
        root.put("operator_question", question.trim());
        root.put("observation", buildObservation(primaryAgent));

        ArrayNode allowedDecisionTypes = root.putArray("allowed_decision_types");
        allowedDecisionTypes.add("answer");

        root.set("route_budget", buildRouteBudget());

        ArrayNode hostConstraints = root.putArray("host_constraints");
        hostConstraints.add("Ask-path only.");
        hostConstraints.add("Bridge responses remain advisory only.");
        hostConstraints.add("Return structured JSON only.");
        hostConstraints.add("No authoritative mutation.");

        ObjectNode trace = root.putObject("trace");
        trace.put("trace_id", "trace-" + requestId);
        trace.put("source", "java-host");
        trace.put("conversation_id", "ask:" + normalizeId(primaryAgent.getName()));

        root.put("requested_mutation_intent", "none");
        return root;
    }

    private ObjectNode buildWorldSlice(
        WorldSnapshotResponse snapshot,
        WorldSnapshotResponse.WorldAgentResponse primaryAgent) {
        ObjectNode worldSlice = MAPPER.createObjectNode();
        WorldSnapshotResponse.WorldBuildRulesResponse worldBuilding = snapshot.getWorldBuilding() == null
            ? new WorldSnapshotResponse.WorldBuildRulesResponse()
            : snapshot.getWorldBuilding();
        worldSlice.put("schema_version", BRIDGE_SCHEMA_VERSION);
        worldSlice.put("slice_id", "slice-" + snapshot.getTick() + "-" + normalizeId(primaryAgent.getName()));
        worldSlice.put("tick", Math.max(snapshot.getTick(), 0));
        worldSlice.put("sim_time", SimulationTime.now().toString());
        worldSlice.put("step_minutes", Math.max(snapshot.getStep(), 1));
        worldSlice.put("seed_scenario", "smallville-ask-shadow");
        worldSlice.put("world_summary", firstNonBlank(
            worldBuilding.getSummary(),
            "SmallVille snapshot with " + snapshot.getAgents().size() + " agents across " + snapshot.getLocations().size() + " locations."));

        ArrayNode worldRules = worldSlice.putArray("world_rules");
        List<String> rules = new ArrayList<String>();
        rules.addAll(nonBlankStrings(worldBuilding.getRules()));
        rules.add("Java host remains authoritative.");
        rules.add("Bridge responses are advisory only.");
        rules.add("No mutation-capable bridge turn is permitted.");
        dedupeOrdered(rules).forEach(worldRules::add);

        ArrayNode allowedLocationStates = worldSlice.putArray("allowed_location_states");
        List<String> states = nonBlankStrings(worldBuilding.getAllowedLocationStates());
        if (states.isEmpty()) {
            states = snapshot.getLocations().stream()
                .map(WorldSnapshotResponse.WorldLocationResponse::getState)
                .filter(AskShadowBridgeClient::isNotBlank)
                .collect(Collectors.toList());
        }
        if (states.isEmpty()) {
            states = List.of("unspecified");
        }
        dedupeOrdered(states).forEach(allowedLocationStates::add);

        worldSlice.put("daily_rhythm", inferDailyRhythm());

        ArrayNode focusLocations = worldSlice.putArray("focus_locations");
        selectFocusLocations(snapshot, primaryAgent)
            .forEach(location -> {
                ObjectNode focus = focusLocations.addObject();
                focus.put("name", location.getName());
                focus.put("state", firstNonBlank(location.getState(), "unspecified"));
                focus.put("summary", buildLocationSummary(location));
            });

        ArrayNode recentActions = worldSlice.putArray("recent_actions");
        List<String> actionSummaries = snapshot.getActionLog().stream()
            .sorted(Comparator.comparingInt(WorldSnapshotResponse.ActionLogResponse::getTick).reversed())
            .map(WorldSnapshotResponse.ActionLogResponse::getSummary)
            .filter(AskShadowBridgeClient::isNotBlank)
            .limit(6)
            .collect(Collectors.toList());
        if (actionSummaries.isEmpty()) {
            actionSummaries = List.of("No recent actions recorded in snapshot.");
        }
        actionSummaries.forEach(recentActions::add);

        ArrayNode pendingProposals = worldSlice.putArray("pending_proposals");
        List<String> proposalSummaries = snapshot.getPendingProposals().stream()
            .map(this::describeProposal)
            .filter(AskShadowBridgeClient::isNotBlank)
            .limit(6)
            .collect(Collectors.toList());
        if (proposalSummaries.isEmpty()) {
            proposalSummaries = List.of("No pending proposals are exposed for this shadow turn.");
        }
        proposalSummaries.forEach(pendingProposals::add);

        return worldSlice;
    }

    private ObjectNode buildAgentContext(WorldSnapshotResponse.WorldAgentResponse agent) {
        ObjectNode context = MAPPER.createObjectNode();
        context.put("agent_id", normalizeId(agent.getName()));
        context.put("location", firstNonBlank(agent.getLocation(), "Unknown"));
        context.put("current_activity", firstNonBlank(agent.getAction(), "unspecified"));

        ArrayNode traits = context.putArray("traits");
        splitTraits(agent.getTraits()).forEach(traits::add);

        context.put("social_preference", firstNonBlank(agent.getSocialPreference(), "balanced"));
        context.put("can_propose_world_changes", agent.isCanProposeWorldChanges());

        addStrings(context.putArray("persona"), agent.getPersona());
        addStrings(context.putArray("goals"), agent.getGoals());
        addStrings(context.putArray("rituals"), agent.getRituals());
        addStrings(context.putArray("working_memories"), agent.getWorkingMemories());
        addStrings(context.putArray("recent_memories"), agent.getRecentMemories());
        addStrings(context.putArray("short_plans"), agent.getShortPlans());
        addStrings(context.putArray("long_plans"), agent.getLongPlans());

        context.put("resolved_model_hint", firstNonBlank(agent.getModel(), "local_root"));
        return context;
    }

    private ObjectNode buildRouteBudget() {
        int totalTimeoutMs = resolveBudgetTimeout(callTimeoutMs, (int) DEFAULT_CALL_TIMEOUT.toMillis());
        int localRootSoftTargetMs = totalTimeoutMs;
        int softFallbackMs = Math.max(1, Math.min(totalTimeoutMs, 300));
        ObjectNode routeBudget = MAPPER.createObjectNode();
        routeBudget.put("total_timeout_ms", totalTimeoutMs);
        routeBudget.put("local_root_soft_target_ms", localRootSoftTargetMs);
        routeBudget.put("nano_soft_target_ms", softFallbackMs);
        routeBudget.put("mini_soft_target_ms", softFallbackMs);
        routeBudget.put("allow_remote_escalation", false);
        return routeBudget;
    }

    private ShadowAskResult parseResponse(String expectedRequestId, String responseBody) throws IOException {
        JsonNode root = MAPPER.readTree(responseBody);
        if (root == null || !root.isObject()) {
            return ShadowAskResult.skipped(expectedRequestId, "non_json_response");
        }

        String requestId = requiredText(root, "request_id");
        if (!Objects.equals(expectedRequestId, requestId)) {
            return ShadowAskResult.skipped(expectedRequestId, "request_id_mismatch");
        }

        String status = requiredText(root, "status");
        if (!"ok".equals(status)) {
            return ShadowAskResult.skipped(requestId, "status_" + status);
        }

        JsonNode decision = requiredObject(root, "decision");
        JsonNode routeTaken = requiredObject(root, "route_taken");
        Set<String> policyFlags = readStringSet(decision, "policy_flags");

        if (!"answer".equals(requiredText(decision, "decision_type"))) {
            return ShadowAskResult.skipped(requestId, "decision_type_blocked");
        }

        if (!"none".equals(requiredText(decision, "mutation_intent"))) {
            return ShadowAskResult.skipped(requestId, "mutation_intent_blocked");
        }

        if (!requiredBoolean(decision, "host_validation_required")) {
            return ShadowAskResult.skipped(requestId, "host_validation_missing");
        }

        if (!requiredBoolean(decision, "requires_human_review")) {
            return ShadowAskResult.skipped(requestId, "human_review_missing");
        }

        if (!policyFlags.contains("shadow_only") || !policyFlags.contains("ask_only") || !policyFlags.contains("host_validation_required")) {
            return ShadowAskResult.skipped(requestId, "shadow_policy_missing");
        }

        if (!"local_root".equals(requiredText(routeTaken, "selected_adapter"))) {
            return ShadowAskResult.skipped(requestId, "adapter_blocked");
        }

        if (!requiredBoolean(routeTaken, "default_route")) {
            return ShadowAskResult.skipped(requestId, "default_route_required");
        }

        if (requiredBoolean(routeTaken, "escalation_occurred")) {
            return ShadowAskResult.skipped(requestId, "escalation_blocked");
        }

        if (!requiredBoolean(routeTaken, "enabled")) {
            return ShadowAskResult.skipped(requestId, "route_disabled");
        }

        if (requiredBoolean(root, "fallback_used")) {
            return ShadowAskResult.skipped(requestId, "bridge_fallback_used");
        }

        String summary = requiredText(decision, "summary");
        String content = requiredText(decision, "content");
        String selectedAdapter = requiredText(routeTaken, "selected_adapter");
        String selectedModel = requiredText(routeTaken, "selected_model");
        String auditRef = requiredText(root, "audit_ref");
        int latencyMs = requiredInt(root, "latency_ms");

        return ShadowAskResult.accepted(requestId, summary, content, selectedAdapter, selectedModel, auditRef, latencyMs);
    }

    private List<WorldSnapshotResponse.WorldLocationResponse> selectFocusLocations(
        WorldSnapshotResponse snapshot,
        WorldSnapshotResponse.WorldAgentResponse primaryAgent) {
        List<WorldSnapshotResponse.WorldLocationResponse> prioritized = new ArrayList<WorldSnapshotResponse.WorldLocationResponse>();
        snapshot.getLocations().stream()
            .filter(location -> Objects.equals(location.getName(), primaryAgent.getLocation()))
            .findFirst()
            .ifPresent(prioritized::add);

        snapshot.getLocations().stream()
            .filter(location -> !prioritized.contains(location))
            .filter(location -> location.getAgents() != null && !location.getAgents().isEmpty())
            .sorted(Comparator.comparing(WorldSnapshotResponse.WorldLocationResponse::getName))
            .forEach(prioritized::add);

        if (prioritized.isEmpty() && !snapshot.getLocations().isEmpty()) {
            prioritized.add(snapshot.getLocations().get(0));
        }

        return prioritized.stream().limit(4).collect(Collectors.toList());
    }

    private String buildLocationSummary(WorldSnapshotResponse.WorldLocationResponse location) {
        List<String> parts = new ArrayList<String>();
        if (location.getAgents() != null && !location.getAgents().isEmpty()) {
            parts.add("Agents here: " + String.join(", ", location.getAgents()) + ".");
        }
        if (isNotBlank(location.getState())) {
            parts.add("State: " + location.getState() + ".");
        }
        if (parts.isEmpty()) {
            parts.add("No active state reported.");
        }
        return String.join(" ", parts);
    }

    private String describeProposal(WorldSnapshotResponse.WorldProposalResponse proposal) {
        String target = firstNonBlank(
            proposal.getName(),
            proposal.getParentLocation(),
            "unspecified target");
        String reason = firstNonBlank(proposal.getReason(), "No reason provided.");
        return proposal.getStatus() + ": " + proposal.getType() + " " + target + " because " + reason;
    }

    private String buildObservation(WorldSnapshotResponse.WorldAgentResponse primaryAgent) {
        List<String> parts = new ArrayList<String>();
        parts.add(primaryAgent.getName() + " is " + firstNonBlank(primaryAgent.getAction(), "currently active"));
        if (isNotBlank(primaryAgent.getLocation())) {
            parts.add("at " + primaryAgent.getLocation());
        }

        String base = String.join(" ", parts).trim();
        String recent = firstNonBlank(
            firstFrom(primaryAgent.getWorkingMemories()),
            firstFrom(primaryAgent.getRecentMemories()),
            firstFrom(primaryAgent.getShortPlans()));

        if (recent == null) {
            return base + ".";
        }

        return base + ". Recent context: " + recent;
    }

    private String inferDailyRhythm() {
        int hour = LocalTime.from(SimulationTime.now()).getHour();
        if (hour < 6) {
            return "early_morning";
        }
        if (hour < 12) {
            return "morning";
        }
        if (hour < 17) {
            return "afternoon";
        }
        if (hour < 22) {
            return "evening";
        }
        return "night";
    }

    private void addStrings(ArrayNode array, List<String> values) {
        nonBlankStrings(values).forEach(array::add);
    }

    private List<String> splitTraits(String traits) {
        if (!isNotBlank(traits)) {
            return List.of();
        }

        return List.of(traits.split(","))
            .stream()
            .map(String::trim)
            .filter(AskShadowBridgeClient::isNotBlank)
            .collect(Collectors.toList());
    }

    private static String firstFrom(List<String> values) {
        return values == null ? null : values.stream().filter(AskShadowBridgeClient::isNotBlank).findFirst().orElse(null);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (isNotBlank(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static List<String> nonBlankStrings(List<String> values) {
        if (values == null) {
            return List.of();
        }

        return values.stream()
            .filter(AskShadowBridgeClient::isNotBlank)
            .map(String::trim)
            .collect(Collectors.toList());
    }

    private static List<String> dedupeOrdered(List<String> values) {
        return new ArrayList<String>(new LinkedHashSet<String>(values));
    }

    private static String normalizeId(String value) {
        String normalized = value == null ? "unknown" : value.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        normalized = normalized.replaceAll("^-+", "").replaceAll("-+$", "");
        return normalized.isBlank() ? "unknown" : normalized;
    }

    private int resolveBudgetTimeout(int configuredTimeoutMs, int defaultTimeoutMs) {
        if (configuredTimeoutMs <= 0) {
            return defaultTimeoutMs;
        }

        return configuredTimeoutMs;
    }

    private static JsonNode requiredObject(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || !node.isObject()) {
            throw new IllegalArgumentException("Missing object field: " + field);
        }
        return node;
    }

    private static String requiredText(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || !node.isTextual() || node.asText().isBlank()) {
            throw new IllegalArgumentException("Missing text field: " + field);
        }
        return node.asText();
    }

    private static boolean requiredBoolean(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || !node.isBoolean()) {
            throw new IllegalArgumentException("Missing boolean field: " + field);
        }
        return node.asBoolean();
    }

    private static int requiredInt(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || !node.canConvertToInt()) {
            throw new IllegalArgumentException("Missing int field: " + field);
        }
        return node.asInt();
    }

    private static Set<String> readStringSet(JsonNode root, String field) {
        JsonNode node = root.get(field);
        if (node == null || !node.isArray()) {
            throw new IllegalArgumentException("Missing array field: " + field);
        }

        LinkedHashSet<String> result = new LinkedHashSet<String>();
        for (JsonNode entry : node) {
            if (!entry.isTextual() || entry.asText().isBlank()) {
                throw new IllegalArgumentException("Invalid array entry for field: " + field);
            }
            result.add(entry.asText().trim());
        }
        return result;
    }

    public static final class ShadowAskResult {
        private final boolean accepted;
        private final String requestId;
        private final String reason;
        private final String summary;
        private final String bridgeContent;
        private final String selectedAdapter;
        private final String selectedModel;
        private final String auditRef;
        private final int latencyMs;

        private ShadowAskResult(
            boolean accepted,
            String requestId,
            String reason,
            String summary,
            String bridgeContent,
            String selectedAdapter,
            String selectedModel,
            String auditRef,
            int latencyMs) {
            this.accepted = accepted;
            this.requestId = requestId;
            this.reason = reason;
            this.summary = summary;
            this.bridgeContent = bridgeContent;
            this.selectedAdapter = selectedAdapter;
            this.selectedModel = selectedModel;
            this.auditRef = auditRef;
            this.latencyMs = latencyMs;
        }

        public static ShadowAskResult accepted(
            String requestId,
            String summary,
            String bridgeContent,
            String selectedAdapter,
            String selectedModel,
            String auditRef,
            int latencyMs) {
            return new ShadowAskResult(true, requestId, "accepted", summary, bridgeContent, selectedAdapter, selectedModel, auditRef, latencyMs);
        }

        public static ShadowAskResult skipped(String requestId, String reason) {
            return new ShadowAskResult(false, requestId, reason, null, null, null, null, null, 0);
        }

        public boolean isAccepted() {
            return accepted;
        }

        public String getRequestId() {
            return requestId;
        }

        public String getReason() {
            return reason;
        }

        public String getSummary() {
            return summary;
        }

        public String getBridgeContent() {
            return bridgeContent;
        }

        public String getSelectedAdapter() {
            return selectedAdapter;
        }

        public String getSelectedModel() {
            return selectedModel;
        }

        public String getAuditRef() {
            return auditRef;
        }

        public int getLatencyMs() {
            return latencyMs;
        }
    }
}
