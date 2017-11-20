package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.config.Configuration;

import java.util.Map;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 3:42 PM)
 */
public class DefaultConfiguration extends AbstractConfiguration {

    public DefaultConfiguration(Map<String, String> data) {
        this(data, null);
    }

    public DefaultConfiguration(Map<String, String> data, Configuration parent) {
        super(parent, data);
    }

}
