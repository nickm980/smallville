package io.github.nickm980.smallville;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.nickm980.smallville.entities.Location;
import io.github.nickm980.smallville.entities.LocationManager;

public class LocationTest {
    
    @Test
    public void splitString() {
	String location = "Red House: Barn";
	
	String[] split = location.split(":");
	
	for (String s : split) {
	    System.out.println(s.trim());
	}
    }
    
    @Test
    public void testLocationParsing() {
	Location location = new Location("Red House: Bedroom");
	
	assertEquals("Red House", location.getAll().get(0));
	assertEquals("Bedroom", location.getAll().get(1));
	assertEquals("Red House: Bedroom", location.getFullPath());
    }
    
    @Test
    public void testSerializationOfChildren() {
	LocationManager manager = new LocationManager();
	manager.addLocation(new Location("Red House: Bedroom"));
	manager.addLocation(new Location("Red House: Kitchen"));
	manager.addLocation(new Location("Blue House: Farm: Hay"));

	List<String> redHouse = manager.getChildren("Red House");
	List<String> blueHouse = manager.getChildren("Blue House");
	List<String> farm = manager.getChildren("Blue House");

	assertTrue(redHouse.contains("Bedroom"));
	assertTrue(redHouse.contains("Kitchen"));
	assertTrue(redHouse.size() == 2);
	assertTrue(blueHouse.contains("Farm"));
	assertTrue(blueHouse.size() == 1);
	assertEquals("Hay", farm);
    }
}
