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
        final StringBuilder response = new StringBuilder();
        DatagramPacket packet;
        while (true) {
            packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                throw new IOCommunicationException("Failed to receive data from the server", e);
            }
            final byte[] data = packet.getData();
            final String current = new String(data, 0, packet.getLength());
            response.append(current);
            if (response.length() < 5 || response.substring(response.length() - 5).equals("\0\0\0\0\0")) {
                break;
            }
        }
        response.delete(response.length() - 5, response.length());
        return new UdpIncoming(response.toString(), packet.getAddress(), packet.getPort());
    }

    @Override
    public UdpIncoming empty() {
        return new UdpIncoming(null, null, 0);
    }

    @Override
    public void send(UdpOutgoing reply) throws CommunicationException {
        final byte[] content = reply.getContent().concat("\0\0\0\0\0").getBytes();
        int sent = 0;
        int toSend = 0;
        final byte[] buffer = new byte[BUFFER_SIZE];
        while (sent < content.length) {
            toSend = Math.min(content.length - sent, BUFFER_SIZE);
            System.arraycopy(content, sent, buffer, 0, toSend);
            sent += toSend;
            final DatagramPacket packet = new DatagramPacket(buffer, toSend, reply.getAddress(), reply.getPort());
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new IOCommunicationException("Failed to send datagram packet", e);
            }
        }
    }

    @Override
    public void close() throws CommunicationException {

    }

}
