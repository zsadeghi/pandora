package me.theyinspire.pandora.raft.cmd;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:33 PM)
 */
public interface VoteRaftCommand extends RaftCommand {

    @Override
    default String keyword() {
        return "vote";
    }

}
