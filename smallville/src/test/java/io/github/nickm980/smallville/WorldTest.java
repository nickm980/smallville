package io.github.nickm980.smallville;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.entities.Conversation;
import io.github.nickm980.smallville.entities.Dialog;
import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.exceptions.SmallvilleException;

public class WorldTest {

    private World world;

    @BeforeEach
    public void setUp() {
	world = new World();
    }

    @Test
    public void test_world_locations() {
	assertTrue(world.getLocation("missing location").isEmpty());

	world.create(new Location("location name"));

	assertTrue(world.getLocation("location name").isPresent());

	world.setState("location name", "empty");

	assertTrue(world.getLocation("location name").get().getState().equals("empty"));
    }

    @Test
    public void test_saving_null_location_does_not_throw_error() {
	assertThrows(Exception.class, () -> {
	    world.setState(null, null);
	});
    }

    @Test
    public void test_world_conversation_creation() {
	assertEquals(0, world.getConversationsAfter(LocalDateTime.now()).size());

	Conversation conversation = new Conversation("none", "", List.of(new Dialog("john", "hi")));
	world.create(conversation);

	assertEquals(1, world.getConversationsAfter(LocalDateTime.now()).size());

	assertThrows(SmallvilleException.class, () -> {
	    world.create(new Conversation("name", "name", List.of(new Dialog("name", "message"))));
	});

	assertThrows(SmallvilleException.class, () -> {
	    world.create(new Conversation("name", "name", List.of()));
	});
    }
}
