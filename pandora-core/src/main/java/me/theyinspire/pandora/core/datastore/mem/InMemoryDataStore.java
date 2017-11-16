package me.theyinspire.pandora.core.datastore.mem;

import me.theyinspire.pandora.core.datastore.DataStore;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:11 PM)
 */
public class InMemoryDataStore implements DataStore {

    private final Map<String, Serializable> storage;

    public InMemoryDataStore() {
        storage = new HashMap<>();
    }

    public long size() {
        return storage.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean store(String key, Serializable value) {
        storage.put(key, value);
        return true;
    }

    public Serializable get(String key) {
        return storage.getOrDefault(key, null);
    }

    public boolean delete(String key) {
        if (!has(key)) {
            return false;
        }
        storage.remove(key);
        return true;
    }

    public Set<String> keys() {
        return storage.keySet();
    }

    public long truncate() {
        final long size = size();
        storage.clear();
        return size;
    }

    public boolean has(String key) {
        return storage.containsKey(key);
    }

    @Override
    public Map<String, Serializable> all() {
        return Collections.unmodifiableMap(storage);
    }

}
