package me.theyinspire.pandora.tcp.client;

import me.theyinspire.pandora.core.client.ClientTransaction;
import me.theyinspire.pandora.core.error.IOCommunicationException;
import me.theyinspire.pandora.tcp.protocol.TcpProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 5:03 PM)
 */
public class TcpClientTransaction implements ClientTransaction<TcpProtocol> {

    private final TcpProtocol protocol;
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public TcpClientTransaction(TcpProtocol protocol, Socket socket, InputStream inputStream, OutputStream outputStream) {
        this.protocol = protocol;
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void send(String content) {
        protocol.getWriter().write(outputStream, content);
    }

    @Override
    public String receive() {
        return protocol.getReader().readAndClose(inputStream);
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new IOCommunicationException("Failed to close the socket", e);
        }
    }

}
