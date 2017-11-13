package me.theyinspire.pandora.udp.server;

import me.theyinspire.pandora.core.server.impl.SimpleOutgoing;

import java.net.InetAddress;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 1:26 PM)
 */
public class UdpOutgoing extends SimpleOutgoing {

    private final InetAddress address;
    private final int port;

    public UdpOutgoing(String content, InetAddress address, int port) {
        super(content);
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

}
