package me.theyinspire.pandora.raft.cmd.impl;

import me.theyinspire.pandora.raft.LogEntry;
import me.theyinspire.pandora.raft.LogReference;
import me.theyinspire.pandora.raft.cmd.*;

import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:34 PM)
 */
public final class RaftCommands {

    private RaftCommands() {
        throw new UnsupportedOperationException();
    }

    public static AppendRaftCommand append(final int term, final String signature, final LogReference head, final int commit,
                                           final List<LogEntry> entries) {
        return new AppendRaftCommandImpl(term, signature, head, commit, entries);
    }

    public static VoteRaftCommand vote(final int term, final String signature, final LogReference head) {
        return new VoteRaftCommandImpl(term, signature, head);
    }

    public static LeaderRaftCommand leader() {
        return new LeaderRaftCommandImpl();
    }

    public static ModeRaftCommand mode() {
        return new ModeRaftCommandImpl();
    }

    public static TermRaftCommand term() {
        return new TermRaftCommandImpl();
    }

    private static class AppendRaftCommandImpl extends AbstractImmutableRaftCommand implements AppendRaftCommand {

        private final int commit;
        private final List<LogEntry> entries;

        private AppendRaftCommandImpl(final int term,
                                      final String signature,
                                      final LogReference head, final int commit,
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

        @Override
        public String toString() {
            final String string = super.toString();
            return string.substring(0, string.length() - 2) + ",entries=" + entries + ")}";
        }

    }

    private static class VoteRaftCommandImpl extends AbstractImmutableRaftCommand implements VoteRaftCommand {

        private VoteRaftCommandImpl(final int term, final String signature,
                                      final LogReference head) {
            super(term, signature, head);
        }

    }

    private static class ModeRaftCommandImpl implements ModeRaftCommand {

    }

    private static class LeaderRaftCommandImpl implements LeaderRaftCommand {

    }

    private static class TermRaftCommandImpl implements TermRaftCommand {

    }

}
