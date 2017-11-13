package me.theyinspire.pandora.udp.server;

import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.core.server.impl.AbstractServer;
import me.theyinspire.pandora.udp.protocol.UdpProtocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 5:53 PM)
 */
public class UdpServer extends AbstractServer<UdpProtocol, UdpIncoming, UdpOutgoing, UdpServerTransaction, UdpServerSession> {

    private static final Log LOG = LogFactory.getLog("pandora.server.udp");
    private final UdpProtocol protocol;
    private final InetAddress address;
    private final int port;

    public UdpServer(DataStore dataStore, InetAddress address, int port) {
        super(dataStore);
        this.address = address;
        this.port = port;
        protocol = UdpProtocol.getInstance();
    }

    @Override
    protected UdpOutgoing compose(UdpIncoming received, String serialized) {
        return new UdpOutgoing(serialized, received.getAddress(), received.getPort());
    }

    @Override
    protected Log getLog() {
        return LOG;
    }

    @Override
    protected UdpServerSession setUp() {
        final DatagramSocket socket;
        try {
            socket = new DatagramSocket(port, address);
        } catch (SocketException e) {
            throw new ServerException("Failed to set up the socket", e);
        }
        return new UdpServerSession(protocol, address, port, socket);
    }

}
