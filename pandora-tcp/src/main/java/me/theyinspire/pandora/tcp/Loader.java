package me.theyinspire.pandora.tcp;

import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;
import me.theyinspire.pandora.tcp.client.TcpClientFactory;
import me.theyinspire.pandora.tcp.protocol.TcpProtocol;
import me.theyinspire.pandora.tcp.server.TcpServerFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/31/17, 10:17 PM)
 */
public class Loader {

    static {
        DefaultProtocolRegistry.getInstance().register(TcpProtocol.getInstance(), new TcpClientFactory(), new TcpServerFactory());
    }

}
