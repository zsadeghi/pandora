package me.theyinspire.pandora.cli.impl;

import me.theyinspire.pandora.cli.ExecutionConfiguration;
import me.theyinspire.pandora.cli.ExecutionMode;

import java.util.Map;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 12:05 PM)
 */
public abstract class AbstractExecutionConfiguration implements ExecutionConfiguration {

    private final Map<String, String> data;
    private final ExecutionMode mode;

    public AbstractExecutionConfiguration(ExecutionMode mode, Map<String, String> data) {
        this.data = data;
        this.mode = mode;
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

    @Override
    public ExecutionMode getExecutionMode() {
        return mode;
    }


}
