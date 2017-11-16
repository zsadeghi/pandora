package me.theyinspire.pandora.core.datastore;

import me.theyinspire.pandora.core.config.ScopedOptionRegistry;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:36 PM)
 */
public interface DataStoreFactory {

    String getName();

    DataStore getDataStore(DataStoreConfiguration configuration);

    void defineOptions(ScopedOptionRegistry optionRegistry);

}
