package io.github.nickm980.smallville;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.entities.LocationManager;

public class LocationTest {

    @Test
    public void test_single_child_location_tree_is_parsed_correctly() {
	Location location = new Location("Red House: Bedroom");

	assertEquals("Red House", location.getAll().get(0));
	assertEquals("Bedroom", location.getAll().get(1));
	assertEquals("Red House: Bedroom", location.getFullPath());
    }

    @Test
    void test_adding_location_with_nested_children() {
	LocationManager locationManager = new LocationManager();

	Location location = new Location("root:a:b:c");
	locationManager.addLocation(location);

	Set<String> roots = locationManager.getRoots();
	assertTrue(roots.contains("root:a:b:c"));
    }

    @Test
    void test_nonexistant_location_returns_null() {
	LocationManager locationManager = new LocationManager();

	List<String> children = locationManager.getChildren("nonexistent");
	assertNull(children);
    }
}
