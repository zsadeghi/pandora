package me.theyinspire.pandora.core.datastore;

import me.theyinspire.pandora.core.server.ServerConfiguration;

import java.io.Serializable;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 4:07 PM)
 */
public interface LockingDataStore extends DataStore {

    String lock(String key);

    void restore(String key, String lock);

    void unlock(String key, String lock);

    boolean store(String key, Serializable value, String lock);

    boolean delete(String key, String lock);

    Serializable get(String key, String lock);

    boolean locked(String key);

}
