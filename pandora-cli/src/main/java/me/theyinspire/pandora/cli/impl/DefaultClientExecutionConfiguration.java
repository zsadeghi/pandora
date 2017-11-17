package me.theyinspire.pandora.cli.impl;

import me.theyinspire.pandora.core.config.ClientExecutionConfiguration;
import me.theyinspire.pandora.core.config.ExecutionMode;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.config.impl.DefaultClientConfiguration;
import me.theyinspire.pandora.core.error.ConfigurationException;
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
        configuration = new DefaultClientConfiguration(this, protocol);
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

}
