package me.theyinspire.pandora.core.datastore.mem;

import me.theyinspire.pandora.core.config.ScopedOptionRegistry;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.DataStoreConfiguration;
import me.theyinspire.pandora.core.datastore.DataStoreFactory;

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
        return new InMemoryDataStore(Integer.parseInt(configuration.require("initial-capacity")));
    }

    @Override
    public void defineOptions(ScopedOptionRegistry optionRegistry) {
        optionRegistry.register("initial-capacity", "The initial capacity of the in-memory data store", "100");
    }

}
