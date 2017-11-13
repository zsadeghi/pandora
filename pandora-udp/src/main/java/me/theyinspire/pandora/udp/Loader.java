package me.theyinspire.pandora.udp;

import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;
import me.theyinspire.pandora.udp.client.UdpClientFactory;
import me.theyinspire.pandora.udp.protocol.UdpProtocol;
import me.theyinspire.pandora.udp.server.UdpServerFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/31/17, 10:17 PM)
 */
public class Loader {

    static {
        DefaultProtocolRegistry.getInstance().register(UdpProtocol.getInstance(), new UdpClientFactory(), new UdpServerFactory());
    }

}
