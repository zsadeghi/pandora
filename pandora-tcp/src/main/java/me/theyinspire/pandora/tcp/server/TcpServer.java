package me.theyinspire.pandora.tcp.server;

import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.impl.InMemoryDataStore;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.core.server.impl.AbstractServer;
import me.theyinspire.pandora.core.server.impl.SimpleIncoming;
import me.theyinspire.pandora.core.server.impl.SimpleOutgoing;
import me.theyinspire.pandora.tcp.protocol.TcpProtocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 3:10 PM)
 */
public class TcpServer extends AbstractServer<TcpProtocol, SimpleIncoming, SimpleOutgoing, TcpServerTransaction, TcpServerSession> {

    private static final Log LOG = LogFactory.getLog("pandora.server.tcp");
    private final InetAddress inetAddress;
    private final int port;
    private final TcpProtocol protocol;

    public TcpServer(InetAddress inetAddress, int port) {
        this(inetAddress, port, new InMemoryDataStore());
    }

    public TcpServer(InetAddress inetAddress, int port, DataStore dataStore) {
        super(dataStore);
        this.inetAddress = inetAddress;
        this.port = port;
        protocol = TcpProtocol.getInstance();
    }

    @Override
    protected SimpleOutgoing compose(SimpleIncoming received, String serialized) {
        return new SimpleOutgoing(serialized);
    }

    @Override
    protected Log getLog() {
        return LOG;
    }

    @Override
    protected TcpServerSession setUp() {
        final TcpServerSession context;
        final ServerSocket serversocket;
        try {
            serversocket = new ServerSocket(port, BACKLOG, inetAddress);
            context = new TcpServerSession(protocol, serversocket);
        } catch (IOException e) {
            throw new ServerException("Failed to listen to socket", e);
        }
        return context;
    }

}
