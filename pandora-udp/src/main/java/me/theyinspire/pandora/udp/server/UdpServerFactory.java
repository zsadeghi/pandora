package me.theyinspire.pandora.udp.server;

import me.theyinspire.pandora.core.server.Server;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.ServerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 4:11 PM)
 */
public class UdpServerFactory implements ServerFactory {
    @Override
    public Server getInstance(ServerConfiguration configuration) {
        try {
            return new UdpServer(configuration, configuration.getDataStore(), InetAddress.getByName(configuration.getHost()), configuration.getPort());
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid host name", e);
        }
    }
}
