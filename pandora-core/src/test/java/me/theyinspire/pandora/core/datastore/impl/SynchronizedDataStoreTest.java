package me.theyinspire.pandora.core.datastore.impl;

import me.theyinspire.pandora.core.datastore.BaseDataStoreTest;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.mem.InMemoryDataStore;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:27 PM)
 */
public class SynchronizedDataStoreTest extends BaseDataStoreTest {

    @Override
    protected DataStore getDataStore() {
        return new SynchronizedDataStore(new InMemoryDataStore());
    }

}