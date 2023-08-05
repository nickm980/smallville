package io.github.nickm980.smallville.llm;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	ObjectMapper mapper = new ObjectMapper();
	float[] result = new float[0];

	try {
	    // Create the request body
	    JsonNode requestBody = mapper.createObjectNode().put("model", "text-embedding-ada-002").put("input", text);

	    Request request = new Request.Builder()
		.url("https://api.openai.com/v1/embeddings")
		.post(RequestBody
		    .create(mapper.writeValueAsString(requestBody), okhttp3.MediaType.parse("application/json")))
		.addHeader("Authorization", "Bearer " + Settings.getApiKey())
		.build();

	    Response response = client.newCall(request).execute();
	    String responseBody = response.body().string();
	    JsonNode responseJson = mapper.readTree(responseBody);

	    result = mapper.convertValue(responseJson.get("data").get(0).get("embedding"), float[].class);
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

	String json = """
		{
			"model": "%model",
			"messages": [%messages],
			"temperature": %temperature, "max_tokens": 2000

		""";

	if (prompt.isFunctional()) {
	    json += """
	    	,
	    	"functions": %functions,
	    	"function_call": {"name": "%function_name"}
	    	""";
	}

	json += "}";
	json = json.replaceAll("\t", "");
	json = json.strip();
	if (prompt.isFunctional()) {
//	    json = json
//		.replace("%functions", MAPPER.writeValueAsString(SmallvilleConfig.getFunctions().get("functions")));

	    json = json.replace("%function_name", prompt.getFunction());
	}

	json = json.replace("%messages", MAPPER.writeValueAsString(prompt.build()));
	json = json.replace("%temperature", String.valueOf(temperature));
	json = json.replace("%model", SmallvilleConfig.getConfig().getModel());

	LOG.debug("[Chat Request Original]" + json);
	LOG.debug("[Chat Request]" + prompt.getContent());

	RequestBody body = RequestBody.create(json.getBytes());
	Request request = new Request.Builder()
	    .url(SmallvilleConfig.getConfig().getApiPath())
	    .addHeader("Content-Type", "application/json")
	    .addHeader("Authorization", "Bearer " + Settings.getApiKey())
	    .post(body)
	    .build();

	String result = "";

	Response response = client.newCall(request).execute();
	String responseBody = response.body().string();

	ObjectMapper objectMapper = new ObjectMapper();
	JsonNode node = objectMapper.readTree(responseBody);

	if (node.get("choices") == null) {
	    LOG.debug(node.toPrettyString());
	    throw new SmallvilleException(
		    "Invalid api token, rate limit reached, or the LLM is overloaded with requests.");
	}

	
	result = node.get("choices").get(0).get("message").get("content").asText();

	try {
	    Object res = node.get("choices").get(0).get("message").get("function_call").get("arguments");
	    LOG.info(res.toString());
	} catch (Exception e) {

	}

	LOG.debug("[Chat Response]" + node.get("choices").toPrettyString());

	long end = System.currentTimeMillis();
	LOG.debug("[Chat] Response took " + String.valueOf(start - end) + "ms");
//	Analytics.addPrompt(prompt.getContent());
//	Analytics.addPrompt(result);
	PromptReceievedEvent promptReceievedEvent = new PromptReceievedEvent(prompt.getContent(), result, start-end);
	events.postEvent(promptReceievedEvent);
	
	return promptReceievedEvent.getResult();
    }
}