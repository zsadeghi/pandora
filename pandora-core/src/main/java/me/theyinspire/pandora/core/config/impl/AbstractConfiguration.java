package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.config.Configuration;

import java.util.Map;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 3:24 PM)
 */
public abstract class AbstractConfiguration implements Configuration {

    private final Map<String, String> data;

    public AbstractConfiguration(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public String get(String key) {
        return get(key, null);
    }

    @Override
    public String require(String key) {
        if (!data.containsKey(key)) {
            throw new IllegalArgumentException("Missing required argument: " + key);
        }
        return data.get(key);
    }

    @Override
    public boolean has(String key) {
        return data.containsKey(key);
    }

    @Override
    public String get(String key, String defaultValue) {
        if (!data.containsKey(key)) {
            return defaultValue;
        }
        return data.get(key);
    }

}
