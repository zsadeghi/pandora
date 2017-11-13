package me.theyinspire.pandora.core.client;

import me.theyinspire.pandora.core.client.error.ClientException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 10:35 PM)
 */
public interface Client {

    String send(String content) throws ClientException;

}
