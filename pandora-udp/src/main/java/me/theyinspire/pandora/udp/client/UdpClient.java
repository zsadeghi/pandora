package me.theyinspire.pandora.udp.client;

import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.impl.AbstractClient;
import me.theyinspire.pandora.udp.protocol.UdpProtocol;

import java.net.InetAddress;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 6:00 PM)
 */
public class UdpClient extends AbstractClient<UdpProtocol, UdpClientTransaction, UdpClientSession> {

    private final InetAddress destination;
    private final int port;

    public UdpClient(ClientConfiguration configuration, InetAddress destination, int port) {
        super(configuration);
        this.destination = destination;
        this.port = port;
    }

    @Override
    protected UdpClientSession setUp() {
        return new UdpClientSession(destination, port);
    }

}
