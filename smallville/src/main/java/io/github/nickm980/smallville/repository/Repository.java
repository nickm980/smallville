package io.github.nickm980.smallville.repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Generic repository class for storing and managing items of type T.
 *
 * @param <T> the type of items stored in the repository
 */
public class Repository<T> {
    public Map<String, RepositoryItem<T>> data;

    public Repository() {
	data = new HashMap<String, RepositoryItem<T>>();
    }

    /**
     * Saves an item to the repository with the given ID.
     *
     * @param id   the ID of the item to be saved
     * @param item the item to be saved
     * @return true if the item was successfully saved, false otherwise
     */
    public boolean save(String id, T item) {
	boolean result = false;

	if (!data.containsKey(id)) {
	    data.put(id, new RepositoryItem<T>(item));
	    result = true;
	}

	return result;
    }

    /**
     * Updates an item in the repository with the given ID.
     *
     * @param id   the ID of the item to be updated
     * @param item the updated item
     */
    public void update(String id, T item) {
	RepositoryItem<T> searchedFor = data.get(id);

	if (searchedFor != null) {
	    searchedFor.update(item);
	    return;
	}
    }

    /**
     * Retrieves the item with the given ID from the repository.
     *
     * @param id the ID of the item to retrieve
     * @return the item associated with the given ID, or null if not found
     */
    public T getById(String id) {
	RepositoryItem<T> item = data.get(id);

	if (item == null) {
	    return null;
	}

	return item.getData();
    }

    /**
     * Returns the number of items in the repository.
     *
     * @return the number of items in the repository
     */
    public int size() {
	return data.keySet().size();
    }

    /**
     * Returns a list of items created after the specified time.
     *
     * @param time the reference time to filter the items
     * @return a list of items created after the specified time
     */
    public List<T> after(LocalDateTime time) {
	return data
	    .values()
	    .stream()
	    .filter(t -> t.createdAt().compareTo(time) < 0)
	    .map(item -> item.getData())
	    .collect(Collectors.toList());
    }

    /**
     * Returns a list of all items in the repository.
     *
     * @return a list of all items in the repository
     */
    public List<T> all() {
	return data.values().stream().map(t -> t.getData()).collect(Collectors.toList());
    }
}
