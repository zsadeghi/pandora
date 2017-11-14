package me.theyinspire.pandora.core.datastore.impl;

import me.theyinspire.pandora.core.datastore.BaseDataStoreTest;
import me.theyinspire.pandora.core.datastore.DataStore;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:07 PM)
 */
public class InMemoryDataStoreTest extends BaseDataStoreTest {

    @Override
    protected DataStore getDataStore() {
        return new InMemoryDataStore();
    }

}
