package me.theyinspire.pandora.cli;

import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 12:01 PM)
 */
public interface ClientExecutionConfiguration extends ExecutionConfiguration {

    Protocol getProtocol();

    ClientConfiguration getClientConfiguration();

    String getCommand();

}
