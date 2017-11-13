package me.theyinspire.pandora.core.error;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 3:40 PM)
 */
public abstract class CommunicationException extends RuntimeException {

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(String message, Throwable cause) {
        super(message, cause);
    }

}
