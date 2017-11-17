package me.theyinspire.pandora.core.server;

import me.theyinspire.pandora.core.server.error.ServerException;

import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 7:01 PM)
 */
public interface Server {

    ServerConfiguration getConfiguration();

    void start() throws ServerException;

    default void onAfterStop() {
        final List<Runnable> hooks = getConfiguration().getShutdownHooks();
        for (Runnable hook : hooks) {
            new Thread(hook).start();
        }
    }

    void stop() throws ServerException;

}
