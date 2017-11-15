package me.theyinspire.pandora.rmi.protocl;

import me.theyinspire.pandora.core.config.ProtocolOptionRegistry;
import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:15 PM)
 */
public class RmiProtocol implements Protocol {

    private static final RmiProtocol INSTANCE = new RmiProtocol();

    public static Protocol getInstance() {
        return INSTANCE;
    }

    private RmiProtocol() {
    }

    @Override
    public String getName() {
        return "rmi";
    }

    @Override
    public void defineOptions(ProtocolOptionRegistry registry) {
        registry.register("host", "The host name", "0.0.0.0");
        registry.register("port", "The communication port", "8083");
        registry.register("instance", "The name of the RMI object instance", "dataStore");
    }

}
