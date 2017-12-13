package me.theyinspire.pandora.udp.client;

import me.theyinspire.pandora.core.client.ClientTransaction;
import me.theyinspire.pandora.core.error.IOCommunicationException;
import me.theyinspire.pandora.udp.protocol.UdpProtocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 5:54 PM)
 */
public class UdpClientTransaction implements ClientTransaction<UdpProtocol> {

    public static final int BUFFER_SIZE = 1024;
    private final DatagramSocket socket;
    private final InetAddress destination;
    private final int port;

    public UdpClientTransaction(DatagramSocket socket, InetAddress destination, int port) {
        this.socket = socket;
        this.destination = destination;
        this.port = port;
    }

    @Override
    public void send(String content) {
        final byte[] contentBuffer = content.concat("\0\0\0\0\0").getBytes();
        int sent = 0;
        int toSend = 0;
        final byte[] buffer = new byte[BUFFER_SIZE];
        while (sent < contentBuffer.length) {
            toSend = Math.min(contentBuffer.length - sent, BUFFER_SIZE);
            System.arraycopy(contentBuffer, sent, buffer, 0, toSend);
            sent += toSend;
            final DatagramPacket packet = new DatagramPacket(buffer, toSend, destination, port);
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new IOCommunicationException("Failed to send datagram packet", e);
            }
        }
    }

    @Override
    public String receive() {
        final StringBuilder response = new StringBuilder();
        while (true) {
            final DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
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
        return response.toString();
    }

    @Override
    public void close() {
        socket.close();
    }

}
