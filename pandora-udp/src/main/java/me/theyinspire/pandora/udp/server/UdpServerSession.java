package me.theyinspire.pandora.udp.server;

import me.theyinspire.pandora.core.server.ServerSession;
import me.theyinspire.pandora.udp.protocol.UdpProtocol;

import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 5:52 PM)
 */
public class UdpServerSession implements ServerSession<UdpProtocol, UdpServerTransaction> {

    private final UdpProtocol protocol;
    private final InetAddress address;
    private final int port;
    private final DatagramSocket socket;

    public UdpServerSession(UdpProtocol protocol, InetAddress address, int port, DatagramSocket socket) {
        this.protocol = protocol;
        this.address = address;
        this.port = port;
        this.socket = socket;
    }

    @Override
    public UdpServerTransaction startTransaction() {
        return new UdpServerTransaction(socket);
    }

    @Override
    public UdpProtocol getProtocol() {
        return protocol;
    }

}
