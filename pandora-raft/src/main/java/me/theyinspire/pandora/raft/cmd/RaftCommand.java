package me.theyinspire.pandora.raft.cmd;

import me.theyinspire.pandora.core.cmd.Command;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:29 PM)
 */
public interface RaftCommand<R> extends Command<R> {

    String keyword();

}
