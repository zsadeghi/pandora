package me.theyinspire.pandora.core.server;

import me.theyinspire.pandora.core.server.error.ServerException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 7:01 PM)
 */
public interface Server {

    ServerConfiguration getConfiguration();

    void start() throws ServerException;

    void stop() throws ServerException;

}
