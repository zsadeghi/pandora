package me.theyinspire.pandora.cli.impl;

import me.theyinspire.pandora.cli.ClientExecutionConfiguration;
import me.theyinspire.pandora.cli.ExecutionMode;
import me.theyinspire.pandora.cli.error.ConfigurationException;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.config.ProtocolOptionRegistry;
import me.theyinspire.pandora.core.config.impl.DefaultOptionRegistry;
import me.theyinspire.pandora.core.protocol.Protocol;
import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;

import java.util.List;
import java.util.Map;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 12:07 PM)
 */
public class DefaultClientExecutionConfiguration extends AbstractExecutionConfiguration implements ClientExecutionConfiguration {

    private final String command;
    private final Protocol protocol;
    private final ClientConfiguration configuration;

    public DefaultClientExecutionConfiguration(Map<String, String> data, String command) {
        super(ExecutionMode.CLIENT, data);
        this.command = command;
        final List<Protocol> knownProtocols = DefaultProtocolRegistry.getInstance().getKnownProtocols();
        if (knownProtocols.isEmpty()) {
            throw new ConfigurationException("No protocol is known");
        }
        final Protocol defaultProtocol = knownProtocols.get(0);
        protocol = DefaultProtocolRegistry.getInstance().getProtocolByName(get("protocol", defaultProtocol.getName()));
        configuration = new DefaultClientConfiguration();
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public ClientConfiguration getClientConfiguration() {
        return configuration;
    }

    @Override
    public String getCommand() {
        return command;
    }

    private class DefaultClientConfiguration implements ClientConfiguration {

        private String getDefault(String key) {
            final ProtocolOptionRegistry registry = DefaultOptionRegistry.getInstance().getProtocolOptionRegistry(getProtocol());
            final String defaultValue = registry.getDefaultValue(key, null);
            if (defaultValue == null) {
                throw new ConfigurationException("Missing required argument: " + prefix(key));
            }
            return defaultValue;
        }

        private String prefix(String key) {
            return DefaultClientExecutionConfiguration.this.getProtocol().getName() + "-" + key;
        }

        @Override
        public String get(String key) {
            return DefaultClientExecutionConfiguration.this.get(prefix(key));
        }

        @Override
        public String require(String key) {
            return DefaultClientExecutionConfiguration.this.get(prefix(key), getDefault(key));
        }

        @Override
        public String get(String key, String defaultValue) {
            return DefaultClientExecutionConfiguration.this.get(prefix(key), defaultValue);
        }

        @Override
        public boolean has(String key) {
            return DefaultClientExecutionConfiguration.this.has(prefix(key));
        }

        @Override
        public Protocol getProtocol() {
            return DefaultClientExecutionConfiguration.this.getProtocol();
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

}
