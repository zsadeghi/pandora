package me.theyinspire.pandora.rmi;

import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;
import me.theyinspire.pandora.rmi.client.RmiClientFactory;
import me.theyinspire.pandora.rmi.protocl.RmiProtocol;
import me.theyinspire.pandora.rmi.server.RmiServerFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/31/17, 10:16 PM)
 */
public class Loader {

    static {
        DefaultProtocolRegistry.getInstance().register(RmiProtocol.getInstance(), new RmiClientFactory(), new RmiServerFactory());
    }

}
