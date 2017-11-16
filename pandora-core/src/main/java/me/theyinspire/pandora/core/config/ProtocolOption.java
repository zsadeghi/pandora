package me.theyinspire.pandora.core.config;

import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 10:37 AM)
 */
public interface ProtocolOption extends Option {

    Protocol getProtocol();


}
