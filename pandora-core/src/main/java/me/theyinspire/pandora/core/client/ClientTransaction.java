package me.theyinspire.pandora.core.client;

import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 5:00 PM)
 */
public interface ClientTransaction<P extends Protocol> {

    void send(String content);

    String receive();

    void close();

}
