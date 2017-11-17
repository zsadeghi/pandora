package me.theyinspire.pandora.core.datastore.mem;

import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.UriServerConfigurationWriter;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.core.server.impl.DefaultUriServerConfigurationWriter;

import java.io.Serializable;
import java.util.*;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:11 PM)
 */
public class InMemoryDataStore implements LockingDataStore {

    private static final String VOID = UUID.randomUUID().toString();
    private final Map<String, Serializable> storage;
    private final Map<String, Serializable> locked;
    private final UriServerConfigurationWriter configurationWriter;

    public InMemoryDataStore(int initialCapacity) {
        storage = new HashMap<>(initialCapacity);
        locked = new HashMap<>();
        configurationWriter = new DefaultUriServerConfigurationWriter();
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

    @Override
    public String getUri(ServerConfiguration configuration) {
        return configurationWriter.write(configuration);
    }

    @Override
    public void lock(String key) {
        if (locked(key)) {
            throw new ServerException("Key already locked: " + key);
        }
        locked.put(key, has(key) ? get(key) : VOID);
    }

    @Override
    public void restore(String key) {
        if (!locked(key)) {
            throw new ServerException("Key not locked: " + key);
        }
        final Serializable value = locked.get(key);
        if (value == VOID && has(key)) {
            delete(key);
        } else if (value != VOID) {
            store(key, value);
        }
    }

    @Override
    public void unlock(String key) {
        if (!locked(key)) {
            throw new ServerException("Key not locked: " + key);
        }
        locked.remove(key);
    }

    @Override
    public boolean locked(String key) {
        return locked.containsKey(key);
    }

}
