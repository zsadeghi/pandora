package me.theyinspire.pandora.raft.cmd;

public interface TermRaftCommand extends RaftCommand<String> {

    @Override
    default String keyword() {
        return "term";
    }
}
