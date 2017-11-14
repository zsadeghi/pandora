package me.theyinspire.pandora.rmi.server;

import me.theyinspire.pandora.core.server.Server;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.ServerFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 4:13 PM)
 */
public class RmiServerFactory implements ServerFactory {
    @Override
    public Server getInstance(ServerConfiguration configuration) {
        return new RmiServer(configuration.getDataStore(), configuration.require("instance"), configuration.getPort());
    }
}
