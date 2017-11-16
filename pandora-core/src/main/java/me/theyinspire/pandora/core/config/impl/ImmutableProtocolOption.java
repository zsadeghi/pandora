package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.config.ProtocolOption;
import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 2:27 AM)
 */
public class ImmutableProtocolOption extends AbstractOption implements ProtocolOption {

    private final Protocol protocol;

    public ImmutableProtocolOption(String name, String description, String defaultValue, Protocol protcol) {
        super(name, description, defaultValue);
        this.protocol = protcol;
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

}
