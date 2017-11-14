package me.theyinspire.pandora.tcp.server;

import me.theyinspire.pandora.core.server.Server;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.ServerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:32 PM)
 */
public class TcpServerFactory implements ServerFactory {
    @Override
    public Server getInstance(ServerConfiguration configuration) {
        try {
            return new TcpServer(InetAddress.getByName(configuration.getHost()), configuration.getPort(), configuration.getDataStore());
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid host name", e);
        }
    }
}
