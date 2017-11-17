package me.theyinspire.pandora.core.datastore;

import me.theyinspire.pandora.core.server.ServerConfiguration;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/17/17, 1:51 AM)
 */
public interface DestroyableDataStore {

    void destroy(ServerConfiguration serverConfiguration);

}
