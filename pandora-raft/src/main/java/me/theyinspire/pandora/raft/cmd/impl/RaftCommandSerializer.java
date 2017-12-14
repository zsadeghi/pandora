package me.theyinspire.pandora.raft.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandSerializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandSerializer;
import me.theyinspire.pandora.raft.cmd.AppendRaftCommand;
import me.theyinspire.pandora.raft.cmd.LogEntry;
import me.theyinspire.pandora.raft.cmd.RaftCommand;
import me.theyinspire.pandora.raft.cmd.RaftResponse;

import java.util.Collections;
import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:43 PM)
 */
public class RaftCommandSerializer implements CommandSerializer {

    @Override
    public List<Class<? extends Command>> accepts() {
        return Collections.singletonList(RaftCommand.class);
    }

    @Override
    public String serializeCommand(final Command<?> command) {
        if (!(command instanceof RaftCommand)) {
            throw new IllegalStateException();
        }
        final RaftCommand raftCommand = (RaftCommand) command;
        final StringBuilder builder = new StringBuilder();
        builder.append(raftCommand.keyword());
        builder.append(" ").append(raftCommand.term());
        builder.append(" ").append(raftCommand.signature());
        builder.append(" ").append(raftCommand.head().index());
        builder.append(" ").append(raftCommand.head().term());
        if (raftCommand instanceof AppendRaftCommand) {
            AppendRaftCommand appendRaftCommand = (AppendRaftCommand) raftCommand;
            builder.append(" ").append(appendRaftCommand.commit());
            for (LogEntry entry : appendRaftCommand.entries()) {
                final String serializedCommand = AggregateCommandSerializer.getInstance().serializeCommand(entry.command());
                builder.append(" <\0\0").append(serializedCommand).append("\0\0>");
            }
        }
        return builder.toString().trim();
    }

    @Override
    public String serializeResponse(final Command<?> command, final Object response) {
        if (response instanceof RaftResponse) {
            final int term = ((RaftResponse) response).term();
            final boolean success = ((RaftResponse) response).success();
            return term + " " + success;
        }
        throw new IllegalStateException();
    }

}
