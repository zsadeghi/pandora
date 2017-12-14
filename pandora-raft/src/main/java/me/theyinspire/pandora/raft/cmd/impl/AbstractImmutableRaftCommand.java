package me.theyinspire.pandora.raft.cmd.impl;

import me.theyinspire.pandora.raft.LogReference;
import me.theyinspire.pandora.raft.cmd.RaftCommand;
import me.theyinspire.pandora.raft.cmd.RaftServerCommand;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:34 PM)
 */
public abstract class AbstractImmutableRaftCommand implements RaftServerCommand {

    private final int term;
    private final String signature;
    private final LogReference head;

    protected AbstractImmutableRaftCommand(final int term,
                                           final String signature,
                                           final LogReference head) {
        this.term = term;
        this.signature = signature;
        this.head = new ImmutableLogReference(head);
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
    public LogReference head() {
        return head;
    }

    @Override
    public String toString() {
        return "{" + keyword() + "(term=" + term + ",signature=" + signature + ",head=" + head + ")}";
    }

}
