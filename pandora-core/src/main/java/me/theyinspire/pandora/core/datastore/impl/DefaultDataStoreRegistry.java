package me.theyinspire.pandora.core.datastore.impl;

import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.DataStoreConfiguration;
import me.theyinspire.pandora.core.datastore.DataStoreFactory;
import me.theyinspire.pandora.core.datastore.DataStoreRegistry;

import java.util.*;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/31/17, 10:25 PM)
 */
public class DefaultDataStoreRegistry implements DataStoreRegistry {

    private static final DataStoreRegistry INSTANCE = new DefaultDataStoreRegistry();

    public static DataStoreRegistry getInstance() {
        return INSTANCE;
    }

    private final List<String> names;
    private final Map<String, DataStoreFactory> factories = new HashMap<>();

    private DefaultDataStoreRegistry() {
        names = new ArrayList<>();
    }

    @Override
    public void register(String name, DataStoreFactory factory) {
        if (names.contains(name)) {
            throw new IllegalArgumentException("Data store already registered: " + name);
        }
        names.add(name);
        factories.put(name, factory);
    }

    @Override
    public DataStore get(String name, DataStoreConfiguration configuration) {
        if (!factories.containsKey(name)) {
            throw new IllegalArgumentException("Unknown data store: " + name);
        }
        return new SynchronizedDataStore(factories.get(name).getDataStore(configuration));
    }

    @Override
    public List<String> getKnownDataStores() {
        return Collections.unmodifiableList(names);
    }

}
