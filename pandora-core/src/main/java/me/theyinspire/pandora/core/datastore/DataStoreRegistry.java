package me.theyinspire.pandora.core.datastore;

import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/31/17, 10:24 PM)
 */
public interface DataStoreRegistry {

    void register(String name, DataStoreFactory factory);

    DataStore get(String name, DataStoreConfiguration configuration);

    List<String> getKnownDataStores();

}
