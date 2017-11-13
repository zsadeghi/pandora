package me.theyinspire.pandora.cli;

import me.theyinspire.pandora.core.config.Configuration;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 12:02 PM)
 */
public interface ExecutionConfiguration extends Configuration {

    ExecutionMode getExecutionMode();

}
