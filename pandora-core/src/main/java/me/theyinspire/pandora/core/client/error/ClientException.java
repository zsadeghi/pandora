package me.theyinspire.pandora.core.client.error;

import me.theyinspire.pandora.core.error.CommunicationException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 4:13 PM)
 */
public class ClientException extends CommunicationException {
    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
