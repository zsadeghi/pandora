package me.theyinspire.pandora.rest.protocol;

import me.theyinspire.pandora.core.config.ScopedOptionRegistry;
import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/11/17, 7:43 PM)
 */
public class RestProtocol implements Protocol {

    private static final RestProtocol INSTANCE = new RestProtocol();

    public static RestProtocol getInstance() {
        return INSTANCE;
    }

    private RestProtocol() {
    }

    @Override
    public String getName() {
        return "rest";
    }

    @Override
    public void defineOptions(ScopedOptionRegistry registry) {
        registry.register("host", "The host name", "0.0.0.0");
        registry.register("port", "The communication port", "8080");
        registry.register("base", "The context path for the REST endpoints", "/");
    }

}
