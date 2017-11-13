package me.theyinspire.pandora.core.server;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:22 PM)
 */
public interface ServerFactory {

    Server getInstance(ServerConfiguration configuration);

}
