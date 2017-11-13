package me.theyinspire.pandora.rest;

import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;
import me.theyinspire.pandora.rest.client.RestClientFactory;
import me.theyinspire.pandora.rest.protocol.RestProtocol;
import me.theyinspire.pandora.rest.server.RestServerFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/11/17, 8:15 PM)
 */
public class Loader {

    static {
        DefaultProtocolRegistry.getInstance().register(RestProtocol.getInstance(), new RestClientFactory(), new RestServerFactory());
    }


}
