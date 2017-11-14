package me.theyinspire.pandora.udp.client;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.ClientFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 4:10 PM)
 */
public class UdpClientFactory implements ClientFactory {

    @Override
    public Client getInstance(ClientConfiguration configuration) {
        try {
            return new UdpClient(InetAddress.getByName(configuration.getHost()), configuration.getPort());
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid host name", e);
        }
    }

}
