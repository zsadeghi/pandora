package me.theyinspire.pandora.core.datastore.mem;

import me.theyinspire.pandora.core.config.ScopedOptionRegistry;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.DataStoreConfiguration;
import me.theyinspire.pandora.core.datastore.DataStoreFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:38 PM)
 */
public class InMemoryDataStoreFactory implements DataStoreFactory {

    @Override
    public String getName() {
        return "memory";
    }

    @Override
    public DataStore getDataStore(DataStoreConfiguration configuration) {
        return new InMemoryDataStore(Integer.parseInt(configuration.require("initial-capacity")), LockingMethod.valueOf(configuration.require("locking").toUpperCase()));
    }

    @Override
    public void defineOptions(ScopedOptionRegistry optionRegistry) {
        final List<String> lockingMethods = Arrays.stream(LockingMethod.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.toList());
        optionRegistry.register("initial-capacity", "The initial capacity of the in-memory data store", "100");
        optionRegistry.register("locking", "Locking mechanism used by the in-memory data-store. One of " + lockingMethods, LockingMethod.PESSIMISTIC.name().toLowerCase());
    }

}
