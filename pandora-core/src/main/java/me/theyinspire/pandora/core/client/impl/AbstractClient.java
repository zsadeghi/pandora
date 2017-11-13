package me.theyinspire.pandora.core.client.impl;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientSession;
import me.theyinspire.pandora.core.client.ClientTransaction;
import me.theyinspire.pandora.core.client.error.ClientException;
import me.theyinspire.pandora.core.protocol.Protocol;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 4:57 PM)
 */
public abstract class AbstractClient<P extends Protocol, T extends ClientTransaction<P>, S extends ClientSession<P, T>> implements Client {

    @Override
    public String send(String content) throws ClientException {
        final S session = setUp();
        T transaction = session.startTransaction();
        transaction.send(content);
        String response = transaction.receive();
        transaction.close();
        return response;
    }

    protected abstract S setUp();

}
