package me.theyinspire.pandora.udp.protocol;

import me.theyinspire.pandora.core.config.ScopedOptionRegistry;
import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 5:44 PM)
 */
public class UdpProtocol implements Protocol {

    private static final UdpProtocol INSTANCE = new UdpProtocol();

    public static UdpProtocol getInstance() {
        return INSTANCE;
    }

    private UdpProtocol() {
    }

    @Override
    public String getName() {
        return "udp";
    }

    @Override
    public void defineOptions(ScopedOptionRegistry registry) {
        registry.register("host", "The host name", "127.0.0.1");
        registry.register("port", "The communication port", "8082");
    }

}
