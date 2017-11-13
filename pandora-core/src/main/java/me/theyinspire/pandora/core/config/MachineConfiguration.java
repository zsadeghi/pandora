package me.theyinspire.pandora.core.config;

import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/31/17, 10:04 PM)
 */
public interface MachineConfiguration extends Configuration {

    Protocol getProtocol();

    String getHost();

    int getPort();

}
