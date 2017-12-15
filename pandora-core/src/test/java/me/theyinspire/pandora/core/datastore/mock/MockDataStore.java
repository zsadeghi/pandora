package me.theyinspire.pandora.core.datastore.mock;


import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.server.ServerConfiguration;

import java.io.Serializable;
import java.util.*;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:33 PM)
 */
public class MockDataStore implements DataStore {

    private final List<DataStoreOperations> operations;
    private final List<List<Object>> arguments;

    public MockDataStore() {
        operations = new ArrayList<>();
        arguments = new ArrayList<>();
    }

    @Override
    public String getUri(ServerConfiguration configuration) {
        return null;
    }

    @Override
    public String getSignature() {
        return null;
    }

    @Override
    public long size() {
        call(DataStoreOperations.SIZE);
        return 0;
    }

    @Override
    public boolean isEmpty() {
        call(DataStoreOperations.IS_EMPTY);
        return true;
    }

    @Override
    public boolean store(String key, Serializable value) {
        call(DataStoreOperations.STORE, key, value);
        return false;
    }

    @Override
    public Serializable get(String key) {
        call(DataStoreOperations.GET, key);
        return null;
    }

    @Override
    public boolean delete(String key) {
        call(DataStoreOperations.DELETE, key);
        return false;
    }

    @Override
    public Set<String> keys() {
        call(DataStoreOperations.KEYS);
        return Collections.emptySet();
    }

    @Override
    public long truncate() {
        call(DataStoreOperations.TRUNCATE);
        return 0;
    }

    @Override
    public boolean has(String key) {
        call(DataStoreOperations.HAS, key);
        return false;
    }

    @Override
    public Map<String, Serializable> all() {
        call(DataStoreOperations.ALL);
        return Collections.emptyMap();
    }

    private void call(DataStoreOperations operation, Object... arguments) {
        operations.add(operation);
        this.arguments.add(Arrays.asList(arguments));
    }

    public List<DataStoreOperations> getOperations() {
        return operations;
    }

    public List<List<Object>> getArguments() {
        return arguments;
    }

}
