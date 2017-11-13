package me.theyinspire.pandora.udp.client;

import me.theyinspire.pandora.core.client.ClientSession;
import me.theyinspire.pandora.core.client.error.ClientException;
import me.theyinspire.pandora.udp.protocol.UdpProtocol;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 6:00 PM)
 */
public class UdpClientSession implements ClientSession<UdpProtocol, UdpClientTransaction> {

    private final InetAddress destination;
    private final int port;

    public UdpClientSession(InetAddress destination, int port) {
        this.destination = destination;
        this.port = port;
    }

    @Override
    public UdpClientTransaction startTransaction() {
        final DatagramSocket socket;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new ClientException("Failed to bind socket", e);
        }
        return new UdpClientTransaction(socket, destination, port);
    }

}
