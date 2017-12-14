package me.theyinspire.pandora.raft.cmd;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:35 PM)
 */
public interface LogHead {

    int index();

    int term();

}
