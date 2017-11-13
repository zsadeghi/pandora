package me.theyinspire.pandora.tcp.server;

import me.theyinspire.pandora.core.server.ServerSession;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.tcp.protocol.TcpProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 3:49 PM)
 */
public class TcpServerSession implements ServerSession<TcpProtocol, TcpServerTransaction> {

    private final TcpProtocol protocol;
    private final ServerSocket socket;

    public TcpServerSession(TcpProtocol protocol, ServerSocket socket) {
        this.socket = socket;
        this.protocol = protocol;
    }

    public ServerSocket getSocket() {
        return socket;
    }

    @Override
    public TcpServerTransaction startTransaction() {
        final Socket socket;
        try {
            socket = getSocket().accept();
        } catch (IOException e) {
            throw new ServerException("Could not accept connection", e);
        }
        final InputStream inputStream;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            throw new ServerException("Failed to open connection data input", e);
        }
        final OutputStream outputStream;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new ServerException("Failed to open connection data output");
        }
        return new TcpServerTransaction(getProtocol(), socket, inputStream, outputStream);
    }

    @Override
    public TcpProtocol getProtocol() {
        return protocol;
    }

}
