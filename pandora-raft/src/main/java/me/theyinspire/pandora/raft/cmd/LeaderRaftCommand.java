package me.theyinspire.pandora.raft.cmd;

public interface LeaderRaftCommand extends RaftCommand<String> {

    @Override
    default String keyword() {
        return "leader";
    }

}
