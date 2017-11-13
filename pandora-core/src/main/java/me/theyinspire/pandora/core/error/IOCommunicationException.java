package me.theyinspire.pandora.core.error;

import java.io.IOException;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 3:44 PM)
 */
public class IOCommunicationException extends CommunicationException {

    public IOCommunicationException(String message, IOException cause) {
        super(message, cause);
    }

}
