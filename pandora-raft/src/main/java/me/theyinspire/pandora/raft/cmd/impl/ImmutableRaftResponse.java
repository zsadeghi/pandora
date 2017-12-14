package me.theyinspire.pandora.raft.cmd.impl;

import me.theyinspire.pandora.raft.cmd.RaftResponse;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:30 PM)
 */
public class ImmutableRaftResponse implements RaftResponse {

    private final boolean success;
    private final int term;

    public ImmutableRaftResponse(final int term, final boolean success) {
        this.success = success;
        this.term = term;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int term() {
        return term;
    }

    @Override
    public String toString() {
        return term + " " + success;
    }

}
