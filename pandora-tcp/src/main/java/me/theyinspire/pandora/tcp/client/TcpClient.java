package me.theyinspire.pandora.tcp.client;

import me.theyinspire.pandora.core.client.impl.AbstractClient;
import me.theyinspire.pandora.tcp.protocol.TcpProtocol;

import java.net.InetAddress;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 4:10 PM)
 */
public class TcpClient extends AbstractClient<TcpProtocol, TcpClientTransaction, TcpClientSession> {

    private final InetAddress inetAddress;
    private final int port;
    private final TcpProtocol protocol;

    public TcpClient(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
        this.protocol = TcpProtocol.getInstance();
    }

    @Override
    protected TcpClientSession setUp() {
        return new TcpClientSession(inetAddress, port, protocol);
    }

}
