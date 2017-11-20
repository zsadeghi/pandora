package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.config.Configuration;
import me.theyinspire.pandora.core.config.ScopedOptionRegistry;
import me.theyinspire.pandora.core.error.ConfigurationException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 2:35 PM)
 */
public abstract class AbstractScopedConfiguration implements Configuration {

    private Configuration delegate;

    public AbstractScopedConfiguration(Configuration delegate) {
        this.delegate = delegate;
    }

    protected abstract String prefix(String key);

    protected abstract ScopedOptionRegistry getOptionRegistry();

    private String getDefault(String key) {
        final ScopedOptionRegistry registry = getOptionRegistry();
        final String defaultValue = registry.getDefaultValue(key, null);
        if (defaultValue == null) {
            throw new ConfigurationException("Missing required argument: " + prefix(key));
        }
        return defaultValue;
    }

    @Override
    public String get(String key) {
        return delegate.get(prefix(key));
    }

    @Override
    public String require(String key) {
        final String value = delegate.get(prefix(key), null);
        if (value == null) {
            return getDefault(key);
        }
        return value;
    }

    @Override
    public String get(String key, String defaultValue) {
        return delegate.get(prefix(key));
    }

    @Override
    public boolean has(String key) {
        return delegate.has(prefix(key));
    }

    @Override
    public Set<String> keys() {
        return delegate.keys().stream().filter(key -> key.startsWith(prefix(""))).collect(Collectors.toSet());
    }

}
