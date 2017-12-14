package me.theyinspire.pandora.raft;

import me.theyinspire.pandora.core.cmd.Command;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:31 PM)
 */
public interface LogEntry {

    Command<?> command();

    int term();

}
