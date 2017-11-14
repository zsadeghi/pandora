package me.theyinspire.pandora.tcp.client;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.ClientFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:30 PM)
 */
public class TcpClientFactory implements ClientFactory {

    @Override
    public Client getInstance(ClientConfiguration configuration) {
        try {
            return new TcpClient(InetAddress.getByName(configuration.getHost()), configuration.getPort());
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Unknown host name", e);
        }
    }

}
