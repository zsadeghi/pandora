package me.theyinspire.pandora.core.datastore;

import me.theyinspire.pandora.core.server.ServerConfiguration;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 4:07 PM)
 */
public interface LockingDataStore extends DataStore {

    String getUri(ServerConfiguration configuration);

    void lock(String key);

    void restore(String key);

    void unlock(String key);

    boolean locked(String key);

}
