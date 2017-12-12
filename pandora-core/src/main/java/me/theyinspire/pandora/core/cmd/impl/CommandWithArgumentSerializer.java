package me.theyinspire.pandora.core.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandSerializer;
import me.theyinspire.pandora.core.cmd.CommandWithArguments;

import java.util.Collections;
import java.util.List;

/**
 * @author Mohammad Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (12/12/17, 1:30 PM)
 */
public class CommandWithArgumentSerializer implements CommandSerializer {

    @Override
    public List<Class<? extends Command>> accepts() {
        return Collections.singletonList(CommandWithArguments.class);
    }

    @Override
    public String serializeCommand(final Command<?> command) {
        final CommandWithArguments commandWithArguments = (CommandWithArguments) command;
        final StringBuilder builder = new StringBuilder();
        builder.append(commandWithArguments.getCommand());
        for (String arg : commandWithArguments.getArguments()) {
            builder.append(" ").append(arg);
        }
        return builder.toString();
    }

    @Override
    public String serializeResponse(final Command<?> command, final Object response) {
        return String.valueOf(response);
    }

}
