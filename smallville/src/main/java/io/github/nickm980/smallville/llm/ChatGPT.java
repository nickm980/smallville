package io.github.nickm980.smallville.llm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nickm980.smallville.Settings;
import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.events.EventBus;
import io.github.nickm980.smallville.events.llm.PromptReceievedEvent;
import io.github.nickm980.smallville.exceptions.SmallvilleException;
import io.github.nickm980.smallville.prompts.PromptRequest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPT implements LLM {
    private final static Logger LOG = LoggerFactory.getLogger(ChatGPT.class);
    private final static ObjectMapper MAPPER = new ObjectMapper();
    private final EventBus events = EventBus.getEventBus();
    
    @Override
    public String sendChat(PromptRequest prompt, double temperature) {
	int maxRetries = SmallvilleConfig.getConfig().getMaxRetries();
	int retryCount = 0;
	String result = null;

	ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	Semaphore semaphore = new Semaphore(0);

	while (retryCount < maxRetries) {
	    try {
		result = attemptRequest(prompt, temperature);
		break;
	    } catch (IOException | SmallvilleException e) {
		retryCount++;
		LOG.error("Request failed. Retrying... (Attempt " + retryCount + ")");

		executor.schedule(() -> semaphore.release(), 2, TimeUnit.SECONDS);

		try {
		    semaphore.acquire();
		} catch (InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	    }
	}

	executor.shutdownNow();

	if (result == null) {
	    LOG.error("Failed to get a successful response after " + maxRetries + " attempts.");
	    throw new SmallvilleException("Failed to get a successful response.");
	}

	return result;
    }

    
    @Override
    public float[] getTokenEmbeddings(String text) {
	OkHttpClient client = new OkHttpClient();
	float[] result = new float[0];

	try {
	    if (!Settings.hasApiKey()) {
		LOG.debug("Skipping embeddings request because no API key is configured.");
		return result;
	    }

	    JsonNode requestBody = MAPPER.createObjectNode().put("model", "text-embedding-ada-002").put("input", text);

	    Request.Builder requestBuilder = new Request.Builder()
		.url("https://api.openai.com/v1/embeddings")
		.post(RequestBody
		    .create(MAPPER.writeValueAsString(requestBody), okhttp3.MediaType.parse("application/json")));

	    addAuthorizationHeader(requestBuilder, "https://api.openai.com/v1/embeddings");
	    Request request = requestBuilder.build();

	    Response response = client.newCall(request).execute();
	    String responseBody = response.body().string();
	    JsonNode responseJson = MAPPER.readTree(responseBody);

	    result = MAPPER.convertValue(responseJson.get("data").get(0).get("embedding"), float[].class);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return result;
    }

    private String attemptRequest(PromptRequest prompt, double temperature) throws IOException, SmallvilleException {
	long start = System.currentTimeMillis();

	OkHttpClient client = new OkHttpClient.Builder()
	    .connectTimeout(10, TimeUnit.SECONDS)
	    .writeTimeout(3, TimeUnit.MINUTES)
	    .readTimeout(3, TimeUnit.MINUTES)
	    .build();

	if (prompt.isFunctional()) {
	    LOG.warn("Function calling was requested for {}, but Smallville is sending a normal chat completion request.", prompt.getFunction());
	}

	ObjectNode requestJson = MAPPER.createObjectNode();
	requestJson.put("model", resolveModel(prompt));
	requestJson.put("temperature", temperature);
	requestJson.put("max_tokens", 2000);
	requestJson.put("stream", false);

	ArrayNode messages = requestJson.putArray("messages");
	messages.addPOJO(prompt.build());

	String json = MAPPER.writeValueAsString(requestJson);

	LOG.debug("[Chat Request Original]" + json);
	LOG.debug("[Chat Request]" + prompt.getContent());

	RequestBody body = RequestBody.create(json, okhttp3.MediaType.parse("application/json"));
	Request.Builder requestBuilder = new Request.Builder()
	    .url(SmallvilleConfig.getConfig().getApiPath())
	    .addHeader("Content-Type", "application/json")
	    .post(body);
	addAuthorizationHeader(requestBuilder, SmallvilleConfig.getConfig().getApiPath());
	Request request = requestBuilder.build();

	String result = "";

	Response response = client.newCall(request).execute();
	String responseBody = response.body().string();

	JsonNode node = MAPPER.readTree(responseBody);

	if (node.get("choices") == null) {
	    LOG.debug(node.toPrettyString());
	    if (node.get("error") != null) {
		throw new SmallvilleException(node.get("error").toString());
	    }

	    throw new SmallvilleException("The LLM response did not include any choices.");
	}

	
	result = node.get("choices").get(0).get("message").get("content").asText();

	try {
	    Object res = node.get("choices").get(0).get("message").get("function_call").get("arguments");
	    LOG.info(res.toString());
	} catch (Exception e) {

	}

	LOG.debug("[Chat Response]" + node.get("choices").toPrettyString());

	long end = System.currentTimeMillis();
	LOG.debug("[Chat] Response took " + String.valueOf(end - start) + "ms");
//	Analytics.addPrompt(prompt.getContent());
//	Analytics.addPrompt(result);
	PromptReceievedEvent promptReceievedEvent = new PromptReceievedEvent(prompt.getContent(), result, end - start);
	events.postEvent(promptReceievedEvent);
	
	return promptReceievedEvent.getResult();
    }

    private String resolveModel(PromptRequest prompt) {
	if (prompt.getModel() != null && !prompt.getModel().isBlank()) {
	    return prompt.getModel();
	}

	return SmallvilleConfig.getConfig().getModel();
    }

    private void addAuthorizationHeader(Request.Builder requestBuilder, String apiPath) {
	if (!isLocalLoopbackProvider(apiPath) && Settings.hasApiKey()) {
	    requestBuilder.addHeader("Authorization", "Bearer " + Settings.getApiKey());
	}
    }

    private boolean isLocalLoopbackProvider(String apiPath) {
	try {
	    URI uri = new URI(apiPath);
	    String host = uri.getHost();
	    if (host == null || host.isBlank()) {
		return false;
	    }

	    if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "::1".equals(host)) {
		return true;
	    }

	    InetAddress address = InetAddress.getByName(host);
	    return address.isLoopbackAddress() || address.isAnyLocalAddress();
	} catch (URISyntaxException | UnknownHostException e) {
	    return false;
	}
    }
}
