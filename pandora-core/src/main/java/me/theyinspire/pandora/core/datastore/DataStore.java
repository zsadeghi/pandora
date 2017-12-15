package me.theyinspire.pandora.core.datastore;

import me.theyinspire.pandora.core.server.ServerConfiguration;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:09 PM)
 */
public interface DataStore {

    String getUri(ServerConfiguration configuration);

    String getSignature();

    long size();

    boolean isEmpty();

    boolean store(String key, Serializable value);

    Serializable get(String key);

    boolean delete(String key);

    Set<String> keys();

    long truncate();

    boolean has(String key);

    Map<String, Serializable> all();

}
