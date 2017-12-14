package me.theyinspire.pandora.raft.cmd;

import me.theyinspire.pandora.raft.LogEntry;

import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:33 PM)
 */
public interface AppendRaftCommand extends RaftServerCommand {

    @Override
    default String keyword() {
        return "append";
    }

    int commit();

    List<LogEntry> entries();

}
