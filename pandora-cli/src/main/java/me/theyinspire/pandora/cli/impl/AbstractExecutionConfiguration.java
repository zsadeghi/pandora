package me.theyinspire.pandora.cli.impl;

import me.theyinspire.pandora.core.config.Configuration;
import me.theyinspire.pandora.core.config.ExecutionConfiguration;
import me.theyinspire.pandora.core.config.ExecutionMode;
import me.theyinspire.pandora.core.config.impl.AbstractConfiguration;

import java.util.Map;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 12:05 PM)
 */
public abstract class AbstractExecutionConfiguration extends AbstractConfiguration implements ExecutionConfiguration {

    private final ExecutionMode mode;

    public AbstractExecutionConfiguration(ExecutionMode mode, Map<String, String> data, Configuration parent) {
        super(parent, data);
        this.mode = mode;
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return mode;
    }

}
