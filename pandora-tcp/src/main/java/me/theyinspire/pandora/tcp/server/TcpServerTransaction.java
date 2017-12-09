package me.theyinspire.pandora.tcp.server;

import me.theyinspire.pandora.core.error.CommunicationException;
import me.theyinspire.pandora.core.error.IOCommunicationException;
import me.theyinspire.pandora.core.server.ServerTransaction;
import me.theyinspire.pandora.core.server.impl.SimpleIncoming;
import me.theyinspire.pandora.core.server.impl.SimpleOutgoing;
import me.theyinspire.pandora.tcp.protocol.TcpProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 3:52 PM)
 */
public class TcpServerTransaction implements ServerTransaction<SimpleIncoming, SimpleOutgoing> {

    private final TcpProtocol protocol;
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public TcpServerTransaction(TcpProtocol protocol, Socket socket, InputStream inputStream, OutputStream outputStream) {
        this.protocol = protocol;
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public SimpleIncoming receive() throws CommunicationException {
        return new SimpleIncoming(protocol.getReader().read(getInputStream()));
    }

    @Override
    public SimpleIncoming empty() {
        return new SimpleIncoming(null);
    }

    @Override
    public void send(SimpleOutgoing outgoing) throws CommunicationException {
        protocol.getWriter().writeAndClose(getOutputStream(), outgoing.getContent());
    }

    @Override
    public void close() throws CommunicationException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new IOCommunicationException("Failed to close the socket", e);
        }
    }

}
