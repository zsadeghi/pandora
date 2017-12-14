package me.theyinspire.pandora.raft.cmd;

import me.theyinspire.pandora.raft.cmd.impl.ImmutableRaftResponse;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:29 PM)
 */
public interface RaftResponse {

    boolean success();

    int term();

    static RaftResponse reject(int term) {
        return new ImmutableRaftResponse(term, false);
    }

    static RaftResponse accept(int term) {
        return new ImmutableRaftResponse(term, true);
    }

}
