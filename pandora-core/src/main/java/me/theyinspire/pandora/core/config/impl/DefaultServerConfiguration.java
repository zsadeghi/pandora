package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.config.Configuration;
import me.theyinspire.pandora.core.config.ScopedOptionRegistry;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.protocol.Protocol;
import me.theyinspire.pandora.core.server.ServerConfiguration;

import java.util.Collections;
import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 2:35 PM)
 */
public class DefaultServerConfiguration extends AbstractScopedConfiguration implements ServerConfiguration {

    private final Protocol protocol;
    private DataStore dataStore;
    private final List<Runnable> shutdownHooks;

    public DefaultServerConfiguration(Configuration delegate, Protocol protocol, DataStore dataStore, List<Runnable> shutdownHooks) {
        super(delegate);
        this.protocol = protocol;
        this.dataStore = dataStore;
        this.shutdownHooks = shutdownHooks;
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

    @Override
    public DataStore getDataStore() {
        return dataStore;
    }

    @Override
    public List<Runnable> getShutdownHooks() {
        return Collections.unmodifiableList(shutdownHooks);
    }

}
