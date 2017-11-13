package me.theyinspire.pandora.core.server.error;

import java.util.concurrent.CompletionException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 3:39 PM)
 */
public class ServerException extends CompletionException {

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
