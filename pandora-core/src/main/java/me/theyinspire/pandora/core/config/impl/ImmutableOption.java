package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.config.Option;
import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 2:27 AM)
 */
public class ImmutableOption implements Option {

    private final String name;
    private final String description;
    private final String defaultValue;
    private final Protocol protocol;

    public ImmutableOption(String name, String description, String defaultValue, Protocol protcol) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.protocol = protcol;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isOptional() {
        return getDefaultValue() != null;
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

}
