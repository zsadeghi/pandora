package me.theyinspire.pandora.core.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.datastore.cmd.impl.DataStoreCommandDeserializer;

import java.util.Arrays;
import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 10:23 AM)
 */
public class AggregateCommandDeserializer implements CommandDeserializer {

    private static final AggregateCommandDeserializer INSTANCE = new AggregateCommandDeserializer();

    public static AggregateCommandDeserializer getInstance() {
        return INSTANCE;
    }

    private final List<CommandDeserializer> deserializers;

    private AggregateCommandDeserializer() {
        this.deserializers = Arrays.asList(new DataStoreCommandDeserializer());
    }

    @Override
    public Command<?> deserializeCommand(String command) {
        for (CommandDeserializer deserializer : deserializers) {
            final Command<?> deserializedCommand = deserializer.deserializeCommand(command);
            if (deserializedCommand != null) {
                return deserializedCommand;
            }
        }
        return null;
    }

    @Override
    public Object deserializeResponse(Command<?> command, String response) {
        for (CommandDeserializer deserializer : deserializers) {
            final Object deserializedResponse = deserializer.deserializeResponse(command, response);
            if (deserializedResponse != UNKNOWN) {
                return deserializedResponse;
            }
        }
        return null;
    }

}
