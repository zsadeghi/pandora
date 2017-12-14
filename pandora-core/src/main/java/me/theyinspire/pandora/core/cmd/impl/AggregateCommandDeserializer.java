package me.theyinspire.pandora.core.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.datastore.cmd.impl.DataStoreCommandDeserializer;
import me.theyinspire.pandora.core.server.ServerConfiguration;

import java.util.ArrayList;
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
        this.deserializers = new ArrayList<>();
        add(new CommandWithArgumentDeserializer());
        add(new DataStoreCommandDeserializer());
    }

    public void add(CommandDeserializer deserializer) {
        this.deserializers.add(0, deserializer);
    }

    @Override
    public Command<?> deserializeCommand(String command, ServerConfiguration serverConfiguration) {
        for (CommandDeserializer deserializer : deserializers) {
            final Command<?> deserializedCommand = deserializer.deserializeCommand(command, serverConfiguration);
            if (deserializedCommand != null) {
                return deserializedCommand;
            }
        }
        throw new IllegalArgumentException("Bad command: " + command);
    }

    @Override
    public <R> R deserializeResponse(Command<R> command, String response) {
        for (CommandDeserializer deserializer : deserializers) {
            final R deserializedResponse;
            try {
                deserializedResponse = deserializer.deserializeResponse(command, response);
            } catch (IllegalStateException e) {
                continue;
            }
            return deserializedResponse;
        }
        return null;
    }

}
