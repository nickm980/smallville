package io.github.nickm980.smallville.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;

public class LocationManager {

    /**
     * Map of parent: children locationKeys
     */
    private Map<String, List<String>> locationKeys = new HashMap<String, List<String>>();
    private Set<String> roots = new HashSet<String>();
    
    public void addLocation(Location location) {
	List<String> keys = location.getAll();
	roots.add(location.getFullPath());
	
	for (int i = 1; i < keys.size(); i++) {
	    String key = String.join(": ", keys.subList(0, i)).trim();
	    String value = keys.get(i);

	    List<String> tree = Optional.fromNullable(locationKeys.get(key)).or(new ArrayList<String>());

	    if (!tree.contains(value)) {
		tree.add(value);

		locationKeys.put(key, tree);
	    }
	}
    }
    
    public Set<String> getRoots(){
	return roots;
    }
    
    public List<String> getChildren(String key) {
	return locationKeys.get(key);
    }
}
