package me.theyinspire.pandora.udp.server;

import me.theyinspire.pandora.core.server.impl.SimpleIncoming;

import java.net.InetAddress;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 1:25 PM)
 */
public class UdpIncoming extends SimpleIncoming {

    private final InetAddress address;
    private final int port;

    public UdpIncoming(String content, InetAddress address, int port) {
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
