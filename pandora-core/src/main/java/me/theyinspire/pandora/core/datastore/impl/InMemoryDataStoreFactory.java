package me.theyinspire.pandora.core.datastore.impl;

import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.DataStoreConfiguration;
import me.theyinspire.pandora.core.datastore.DataStoreFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:38 PM)
 */
public class InMemoryDataStoreFactory implements DataStoreFactory {
    @Override
    public DataStore getDataStore(DataStoreConfiguration configuration) {
        return new InMemoryDataStore();
    }
}
