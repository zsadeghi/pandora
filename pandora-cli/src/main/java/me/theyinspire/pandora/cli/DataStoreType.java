package me.theyinspire.pandora.cli;

import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.DataStoreConfiguration;
import me.theyinspire.pandora.core.datastore.DataStoreFactory;
import me.theyinspire.pandora.core.datastore.impl.InMemoryDataStoreFactory;
import me.theyinspire.pandora.core.datastore.impl.SynchronizedDataStore;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:36 PM)
 */
public enum DataStoreType {

    MEMORY(new InMemoryDataStoreFactory());

    private final DataStoreFactory factory;

    DataStoreType(DataStoreFactory factory) {
        this.factory = factory;
    }

    public DataStore getDataStore(DataStoreConfiguration configuration) {
        return new SynchronizedDataStore(factory.getDataStore(configuration));
    }

}
