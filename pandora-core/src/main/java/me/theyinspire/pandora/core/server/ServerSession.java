package me.theyinspire.pandora.core.server;

import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 3:47 PM)
 */
public interface ServerSession<P extends Protocol, T extends ServerTransaction> {

    T startTransaction();

    P getProtocol();

}
