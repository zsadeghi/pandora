package me.theyinspire.pandora.core.config;

import me.theyinspire.pandora.core.error.ConfigurationException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:42 PM)
 */
public interface ConfigurationReader {

    ExecutionConfiguration read(Configuration parent, String... args) throws ConfigurationException;

}
