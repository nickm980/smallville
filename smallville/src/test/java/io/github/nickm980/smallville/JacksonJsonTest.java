package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.nickm980.smallville.entities.Location;

public class JacksonJsonTest {

    private static World sim;

    @BeforeAll
    public static void setUp() {
	sim = new World();
	sim.create(new Location("red house"));
    }

    @Test
    public void testJson() {
	String response = """
		[\n  {\n    \"time\": \"2021-06-08 14:30:00\",\n    \"duration\": \"60\",\n    \"location\": \"Living Room\",\n    \"description\": \"Practice guitar for an hour\"\n  },\n  {\n    \"time\": \"2021-06-08 15:30:00\",\n    \"duration\": \"30\",\n    \"location\": \"Kitchen\",\n    \"description\": \"Prepare and have lunch\"\n  },\n  {\n    \"time\": \"2021-06-08 16:00:00\",\n    \"duration\": \"60\",\n    \"location\": \"Home Office\",\n    \"description\": \"Work on a report for an hour\"\n  },\n  {\n    \"time\": \"2021-06-08 17:00:00\",\n    \"duration\": \"30\",\n    \"location\": \"Living Room\",\n    \"description\": \"Watch TV for half an hour\"\n  },\n  {\n    \"time\": \"2021-06-08 17:30:00\",\n    \"duration\": \"60\",\n    \"location\": \"Kitchen\",\n    \"description\": \"Prepare and have dinner\"\n  }\n]"},"finish_reason":"stop","index":0}]
		""";

	ObjectMapper objectMapper = new ObjectMapper();
	JsonNode jsonNode = null;

	try {
	    jsonNode = objectMapper.readTree(response);
	} catch (JsonProcessingException e) {
	    e.printStackTrace();
	}

	JsonNode objNode = jsonNode.get(0);

	String description = objNode.get("description").asText();
	int minutes = objNode.get("duration").asInt();
	String locationName = objNode.get("location").asText(); // town: john's house: desk
	String time = objNode.get("time").asText();

	Duration duration = Duration.ofMinutes(minutes);
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	LocalDateTime dateTime = LocalDateTime.parse(time, formatter);

	assertEquals("Practice guitar for an hour", description);
	assertEquals(60, minutes);
	assertEquals("Living Room", locationName);
	assertEquals("2021-06-08 14:30:00", time);
	assertEquals(dateTime.getHour(), 14);
	assertEquals(dateTime.getMinute(), 30);
	assertEquals(minutes, duration.getSeconds() / 60);
    }
}
