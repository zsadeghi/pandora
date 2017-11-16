package me.theyinspire.pandora.cli.impl;

import me.theyinspire.pandora.cli.ExecutionConfiguration;
import me.theyinspire.pandora.cli.ExecutionMode;
import me.theyinspire.pandora.core.config.impl.AbstractConfiguration;

import java.util.Map;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 12:05 PM)
 */
public abstract class AbstractExecutionConfiguration extends AbstractConfiguration implements ExecutionConfiguration {

    private final ExecutionMode mode;

    public AbstractExecutionConfiguration(ExecutionMode mode, Map<String, String> data) {
        super(data);
        this.mode = mode;
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return mode;
    }


}
