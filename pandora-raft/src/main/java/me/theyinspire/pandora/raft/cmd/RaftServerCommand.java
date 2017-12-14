package me.theyinspire.pandora.raft.cmd;

import me.theyinspire.pandora.raft.LogReference;

public interface RaftServerCommand extends RaftCommand<RaftResponse> {

    int term();

    String signature();

    LogReference head();

}
