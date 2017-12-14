package me.theyinspire.pandora.raft.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.raft.cmd.LogEntry;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:32 PM)
 */
public class ImmutableLogEntry implements LogEntry {

    private final Command<?> command;
    private final int term;

    public ImmutableLogEntry(final Command<?> command, final int term) {
        this.command = command;
        this.term = term;
    }

    @Override
    public Command<?> command() {
        return command;
    }

    @Override
    public int term() {
        return term;
    }

}
