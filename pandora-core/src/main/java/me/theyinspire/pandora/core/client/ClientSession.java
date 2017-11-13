package me.theyinspire.pandora.core.client;

import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 4:57 PM)
 */
public interface ClientSession<P extends Protocol, T extends ClientTransaction<P>> {

    T startTransaction();

}
