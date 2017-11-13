package me.theyinspire.pandora.tcp.client;

import me.theyinspire.pandora.core.client.ClientSession;
import me.theyinspire.pandora.core.error.IOCommunicationException;
import me.theyinspire.pandora.tcp.protocol.TcpProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 5:16 PM)
 */
public class TcpClientSession implements ClientSession<TcpProtocol, TcpClientTransaction> {

    private final InetAddress address;
    private final int port;
    private final TcpProtocol protocol;

    public TcpClientSession(InetAddress address, int port, TcpProtocol protocol) {
        this.address = address;
        this.port = port;
        this.protocol = protocol;
    }

    @Override
    public TcpClientTransaction startTransaction() {
        final Socket socket;
        try {
            socket = new Socket(address, port);
        } catch (IOException e) {
            throw new IOCommunicationException("Failed to open the socket", e);
        }

        final InputStream inputStream;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            throw new IOCommunicationException("Failed to open the input channel", e);
        }
        final OutputStream outputStream;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new IOCommunicationException("Failed to open the output channel", e);
        }
        return new TcpClientTransaction(protocol, socket, inputStream, outputStream);
    }

}
