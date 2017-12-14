package me.theyinspire.pandora.raft.cmd;

public interface ModeRaftCommand extends RaftCommand<String> {

    @Override
    default String keyword() {
        return "mode";
    }
}
