package me.theyinspire.pandora.raft.cmd;

public interface LogRaftCommand extends RaftCommand<String> {

    @Override
    default String keyword() {
        return "log";
    }

}
