package me.theyinspire.pandora.raft.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandDeserializer;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.str.impl.DefaultDocumentReader;
import me.theyinspire.pandora.raft.LogEntry;
import me.theyinspire.pandora.raft.LogReference;
import me.theyinspire.pandora.raft.cmd.RaftCommand;
import me.theyinspire.pandora.raft.cmd.RaftServerCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:48 PM)
 */
public class RaftCommandDeserializer implements CommandDeserializer {

    @Override
    public Command<?> deserializeCommand(final String command, final ServerConfiguration serverConfiguration) {
        final DefaultDocumentReader reader = new DefaultDocumentReader(command);
        final String keyword = reader.read("\\S+", true);
        if (!"vote".equals(keyword) && !"append".equals(keyword)) {
            if ("term".equals(keyword)) {
                return RaftCommands.term();
            } else if ("leader".equals(keyword)) {
                return RaftCommands.leader();
            } else if ("mode".equals(keyword)) {
                return RaftCommands.mode();
            }
            return null;
        }
        final int term = Integer.parseInt(reader.read("\\S+", true));
        final String signature = reader.read("\\S+", true);
        final int headIndex = Integer.parseInt(reader.read("\\S+", true));
        final int headTerm = Integer.parseInt(reader.read("\\S+", true));
        final LogReference logHead = new ImmutableLogReference(headIndex, headTerm);
        if ("vote".equals(keyword)) {
            return RaftCommands.vote(term, signature, logHead);
        }
        final int commit = Integer.parseInt(reader.read("\\S+", true));
        final List<LogEntry> entries = new ArrayList<>();
        while (reader.hasMore()) {
            reader.skip(Pattern.compile("\\s+"));
            if (reader.hasMore()) {
                reader.expect("<\0\0", false);
                final String serializedEntryCommand = reader.read(".*?\0\0>", false).replaceFirst("\0\0>$", "");
                final Command<?> deserializedCommand = AggregateCommandDeserializer.getInstance()
                                                                                   .deserializeCommand(serializedEntryCommand,
                                                                                                       serverConfiguration);
                entries.add(new ImmutableLogEntry(deserializedCommand, term));
            }
        }
        return RaftCommands.append(term, signature, logHead, commit, entries);
    }

    @Override
    public <R> R deserializeResponse(final Command<R> command, final String response) {
        if (!(command instanceof RaftCommand)) {
            throw new IllegalStateException();
        }
        if (command instanceof RaftServerCommand) {
            if (!response.matches("\\d+\\s+(true|false)")) {
                throw new IllegalStateException();
            }
            final String[] split = response.split("\\s+");
            //noinspection unchecked
            return (R) new ImmutableRaftResponse(Integer.parseInt(split[0]), Boolean.parseBoolean(split[1]));
        } else {
            //noinspection unchecked
            return (R) response;
        }
    }

}
