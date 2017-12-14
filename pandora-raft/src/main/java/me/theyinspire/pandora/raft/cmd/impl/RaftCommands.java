package me.theyinspire.pandora.raft.cmd.impl;

import me.theyinspire.pandora.raft.cmd.AppendRaftCommand;
import me.theyinspire.pandora.raft.cmd.LogEntry;
import me.theyinspire.pandora.raft.cmd.LogHead;
import me.theyinspire.pandora.raft.cmd.VoteRaftCommand;

import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:34 PM)
 */
public final class RaftCommands {

    private RaftCommands() {
        throw new UnsupportedOperationException();
    }

    public static AppendRaftCommand append(final int term, final String signature, final LogHead head, final int commit,
                                           final List<LogEntry> entries) {
        return new AppendRaftCommandImpl(term, signature, head, commit, entries);
    }

    public static VoteRaftCommand vote(final int term, final String signature, final LogHead head) {
        return new VoteRaftCommandImpl(term, signature, head);
    }

    private static class AppendRaftCommandImpl extends AbstractImmutableRaftCommand implements AppendRaftCommand {

        private final int commit;
        private final List<LogEntry> entries;

        private AppendRaftCommandImpl(final int term,
                                        final String signature,
                                        final LogHead head, final int commit,
                                        final List<LogEntry> entries) {
            super(term, signature, head);
            this.commit = commit;
            this.entries = entries;
        }

        @Override
        public int commit() {
            return commit;
        }

        @Override
        public List<LogEntry> entries() {
            return entries;
        }

    }

    private static class VoteRaftCommandImpl extends AbstractImmutableRaftCommand implements VoteRaftCommand {

        private VoteRaftCommandImpl(final int term, final String signature,
                                      final LogHead head) {
            super(term, signature, head);
        }

    }

}
