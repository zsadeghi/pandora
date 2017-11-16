package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.config.Configuration;
import me.theyinspire.pandora.core.config.ScopedOptionRegistry;
import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 2:38 PM)
 */
public class DefaultClientConfiguration extends AbstractScopedConfiguration implements ClientConfiguration {

    private final Protocol protocol;

    public DefaultClientConfiguration(Configuration delegate, Protocol protocol) {
        super(delegate);
        this.protocol = protocol;
    }

    protected String prefix(String key) {
        return protocol.getName() + "-" + key;
    }

    @Override
    protected ScopedOptionRegistry getOptionRegistry() {
        return DefaultOptionRegistry.getInstance().getProtocolOptionRegistry(getProtocol());
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public String getHost() {
        return require("host");
    }

    @Override
    public int getPort() {
        return Integer.parseInt(require("port"));
    }

}
