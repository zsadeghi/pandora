package me.theyinspire.pandora.core.client;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:21 PM)
 */
public interface ClientFactory {

    Client getInstance(ClientConfiguration configuration);

}
