package io.github.nickm980.smallville.repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Repository<T> {

    public Map<String, RepositoryItem<T>> data;

    public Repository() {
	data = new HashMap<String, RepositoryItem<T>>();
    }

    public void save(String id, T item) {
	RepositoryItem<T> searchedFor = data.get(id);

	if (searchedFor != null) {
	    searchedFor.update(item);
	    return;
	}

	data.put(id, new RepositoryItem<T>(item));
    }

    public T getById(String id) {
	RepositoryItem<T> item = data.get(id);
	
	if (item == null) {
	    return null;
	}
	
	return item.getData();
    }

    public int size() {
	return data.keySet().size();
    }

    public List<T> after(LocalDateTime time) {
	return data
	    .values()
	    .stream()
	    .filter(t -> t.createdAt().compareTo(time) < 0)
	    .map(item -> item.getData())
	    .toList();
    }

    public List<T> all() {
	return data.values().stream().map(t -> t.getData()).toList();
    }
}
