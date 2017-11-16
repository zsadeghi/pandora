package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.config.Option;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 10:58 AM)
 */
abstract class AbstractOption implements Option {

    private final String name;
    private final String description;
    private final String defaultValue;

    AbstractOption(String name, String description, String defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
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

}
