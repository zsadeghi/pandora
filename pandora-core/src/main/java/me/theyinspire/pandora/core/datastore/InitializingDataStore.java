package me.theyinspire.pandora.core.datastore;

import me.theyinspire.pandora.core.server.ServerConfiguration;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 11:19 PM)
 */
public interface InitializingDataStore {

    void init(ServerConfiguration serverConfiguration, DataStoreConfiguration dataStoreConfiguration);

}
