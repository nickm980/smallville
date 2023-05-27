package io.github.nickm980.smallville.llm;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nickm980.smallville.Settings;
import io.github.nickm980.smallville.config.SmallvilleConfig;
import io.github.nickm980.smallville.exceptions.SmallvilleException;
import io.github.nickm980.smallville.prompts.Prompt;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPT implements LLM {
    private final static Logger LOG = LoggerFactory.getLogger(ChatGPT.class);
    private final static ObjectMapper MAPPER = new ObjectMapper();
    
    @Override
    public String sendChat(Prompt prompt, double temperature) {
	long start = System.currentTimeMillis();

	OkHttpClient client = new OkHttpClient.Builder()
	    .connectTimeout(10, TimeUnit.SECONDS)
	    .writeTimeout(3, TimeUnit.MINUTES)
	    .readTimeout(3, TimeUnit.MINUTES)
	    .build();

	String json = """
		{
			"model": "%model",
			"messages": [
				%messages
			], "temperature": %temperature, "max_tokens": 1000
		}
		""";

	try {
	    json = json.replaceAll("\n", "");
	    json = json.replaceAll("\t", "");
	    json = json.strip();
	    json = json.replace("%messages", MAPPER.writeValueAsString(prompt.build()));
	    json = json.replace("%temperature", String.valueOf(temperature));
	    json = json.replace("%model", SmallvilleConfig.getConfig().getModel());
	} catch (JsonProcessingException e1) {
	    e1.printStackTrace();
	}

	LOG.debug("[Chat Request]" + prompt.getContent().replaceAll("\n", " "));

	RequestBody body = RequestBody.create(json.getBytes());
	Request request = new Request.Builder()
	    .url(SmallvilleConfig.getConfig().getApiPath())
	    .addHeader("Content-Type", "application/json")
	    .addHeader("Authorization", "Bearer " + Settings.getApiKey())
	    .post(body)
	    .build();

	String result = "";

	try {
	    Response response = client.newCall(request).execute();
	    String responseBody = response.body().string();

	    ObjectMapper objectMapper = new ObjectMapper();
	    JsonNode node = objectMapper.readTree(responseBody);

	    if (node.get("choices") == null) {
		LOG.error("Invalid api token or rate limit reached");
		throw new SmallvilleException("Invalid api token or rate limit reached.");
	    }

	    result = node.get("choices").get(0).get("message").get("content").asText();

	    LOG.debug("[Chat Response]" + result);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	long end = System.currentTimeMillis();
	LOG.info("[Chat] Response took " + String.valueOf(start - end) + "ms");
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
}