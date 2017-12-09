package me.theyinspire.pandora.core.server;

import me.theyinspire.pandora.core.error.CommunicationException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 3:48 PM)
 */
public interface ServerTransaction<I extends Incoming, O extends Outgoing> {

    I receive() throws CommunicationException;

    I empty();

    void send(O reply) throws CommunicationException;

    void close() throws CommunicationException;
}
