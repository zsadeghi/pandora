package me.theyinspire.pandora.udp.server;

import me.theyinspire.pandora.core.error.CommunicationException;
import me.theyinspire.pandora.core.error.IOCommunicationException;
import me.theyinspire.pandora.core.server.ServerTransaction;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 5:45 PM)
 */
public class UdpServerTransaction implements ServerTransaction<UdpIncoming, UdpOutgoing> {

    public static final int BUFFER_SIZE = 1024;
    private final DatagramSocket socket;

    public UdpServerTransaction(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public UdpIncoming receive() throws CommunicationException {
        final byte[] buffer = new byte[BUFFER_SIZE];
        final DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            throw new IOCommunicationException("Failed to receive datagram packet", e);
        }
        final byte[] data = packet.getData();
        return new UdpIncoming(new String(data, 0, packet.getLength()), packet.getAddress(), packet.getPort());
    }

    @Override
    public void send(UdpOutgoing reply) throws CommunicationException {
        final byte[] buffer = reply.getContent().getBytes();
        final DatagramPacket packet = new DatagramPacket(buffer, reply.getContent().length(), reply.getAddress(), reply.getPort());
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new IOCommunicationException("Failed to send datagram packet", e);
        }
    }

    @Override
    public void close() throws CommunicationException {

    }

}
