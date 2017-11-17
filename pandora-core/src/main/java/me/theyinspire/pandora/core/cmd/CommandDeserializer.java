package me.theyinspire.pandora.core.cmd;

import me.theyinspire.pandora.core.server.ServerConfiguration;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 10:14 AM)
 */
public interface CommandDeserializer {

    Object UNKNOWN = new Object(){
        @Override
        public String toString() {
            return "(unknown)";
        }
    };

    Command<?> deserializeCommand(String command, ServerConfiguration serverConfiguration);

    Object deserializeResponse(Command<?> command, String response);

}
