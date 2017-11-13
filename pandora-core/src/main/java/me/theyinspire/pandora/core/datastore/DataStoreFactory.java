package me.theyinspire.pandora.core.datastore;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:36 PM)
 */
public interface DataStoreFactory {

    DataStore getDataStore(DataStoreConfiguration configuration);

}
