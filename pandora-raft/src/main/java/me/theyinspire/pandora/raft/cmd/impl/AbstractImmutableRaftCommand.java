package me.theyinspire.pandora.raft.cmd.impl;

import me.theyinspire.pandora.raft.cmd.LogHead;
import me.theyinspire.pandora.raft.cmd.RaftCommand;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:34 PM)
 */
public abstract class AbstractImmutableRaftCommand implements RaftCommand {

    private final int term;
    private final String signature;
    private final LogHead head;

    protected AbstractImmutableRaftCommand(final int term,
                                           final String signature,
                                           final LogHead head) {
        this.term = term;
        this.signature = signature;
        this.head = new ImmutableLogHead(head);
    }

    @Override
    public int term() {
        return term;
    }

    @Override
    public String signature() {
        return signature;
    }

    @Override
    public LogHead head() {
        return head;
    }

}
