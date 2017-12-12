package me.theyinspire.pandora.core.cmd.impl;

import me.theyinspire.pandora.core.cmd.CommandWithArguments;

import java.util.List;

/**
 * @author Mohammad Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (12/12/17, 1:26 PM)
 */
public class ImmutableCommandWithArguments implements CommandWithArguments {

    private final String command;
    private final List<String> arguments;

    public ImmutableCommandWithArguments(final String command, final List<String> arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public List<String> getArguments() {
        return arguments;
    }

}
