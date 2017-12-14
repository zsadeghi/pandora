package me.theyinspire.pandora.core.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.str.DocumentReader;
import me.theyinspire.pandora.core.str.impl.DefaultDocumentReader;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/12/17, 1:25 PM)
 */
public class CommandWithArgumentDeserializer implements CommandDeserializer {

    @Override
    public Command<?> deserializeCommand(final String command, final ServerConfiguration serverConfiguration) {
        final DocumentReader reader = new DefaultDocumentReader(command);
        final String commandName = reader.read("\\S+", true);
        final List<String> arguments = new LinkedList<>();
        while (reader.hasMore()) {
            final String word = reader.read("\\S+", true);
            arguments.add(word);
        }
        return new ImmutableCommandWithArguments(commandName, arguments);
    }

    @Override
    public <R> R deserializeResponse(final Command<R> command, final String response) {
        //noinspection unchecked
        return (R) response;
    }

}
