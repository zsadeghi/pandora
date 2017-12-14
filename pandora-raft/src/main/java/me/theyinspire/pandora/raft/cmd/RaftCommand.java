package me.theyinspire.pandora.raft.cmd;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.raft.LogReference;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:29 PM)
 */
public interface RaftCommand extends Command<RaftResponse> {

    String keyword();

    int term();

    String signature();

    LogReference head();

}