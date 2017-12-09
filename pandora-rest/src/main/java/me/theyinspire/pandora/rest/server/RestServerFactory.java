package me.theyinspire.pandora.rest.server;

import me.theyinspire.pandora.core.server.Server;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.ServerFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/11/17, 8:15 PM)
 */
public class RestServerFactory implements ServerFactory {

    @Override
    public Server getInstance(ServerConfiguration configuration) {
        return new RestServer(configuration, configuration.getHost(), configuration.getPort(), configuration.require("base"), configuration.getDataStore());
    }

}
