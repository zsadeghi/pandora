package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.config.Configuration;

import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 3:24 PM)
 */
public abstract class AbstractConfiguration implements Configuration {

    private final Configuration parent;
    private final Map<String, String> data;

    public AbstractConfiguration(Map<String, String> data) {
        this(null, data);
    }

    public AbstractConfiguration(Configuration parent, Map<String, String> data) {
        this.parent = parent;
        this.data = data;
    }

    @Override
    public String get(String key) {
        if (!data.containsKey(key) && parent != null) {
            return parent.get(key);
        }
        return get(key, null);
    }

    @Override
    public String require(String key) {
        if (!data.containsKey(key) && parent != null) {
            return parent.require(key);
        }
        if (!data.containsKey(key)) {
            throw new IllegalArgumentException("Missing required argument: " + key);
        }
        return data.get(key);
    }

    @Override
    public boolean has(String key) {
        return data.containsKey(key) || parent != null && parent.has(key);
    }

    @Override
    public Set<String> keys() {
        return data.keySet();
    }

    @Override
    public String get(String key, String defaultValue) {
        if (!data.containsKey(key) && parent != null) {
            return parent.get(key, defaultValue);
        }
        if (!data.containsKey(key)) {
            return defaultValue;
        }
        return data.get(key);
    }

}
