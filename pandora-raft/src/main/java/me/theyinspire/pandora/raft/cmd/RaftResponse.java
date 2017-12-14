package me.theyinspire.pandora.raft.cmd;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:29 PM)
 */
public interface RaftResponse {

    boolean success();

    int term();

}
