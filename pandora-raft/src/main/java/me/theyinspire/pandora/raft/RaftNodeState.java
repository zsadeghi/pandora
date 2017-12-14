package me.theyinspire.pandora.raft;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/11/17, 8:15 PM)
 */
public class RaftNodeState {

    private int currentTerm;
    private String votedFor;
    private List<SerializedCommand> log;
    private int commitIndex;
    private int lastApplied;

    public RaftNodeState() {
        currentTerm = 0;
        votedFor = null;
        log = new CopyOnWriteArrayList<>();
        commitIndex = 0;
        lastApplied = 0;
    }

    public int getCurrentTerm() {
        return currentTerm;
    }

    public RaftNodeState setCurrentTerm(final int currentTerm) {
        this.currentTerm = currentTerm;
        return this;
    }

    public String getVotedFor() {
        return votedFor;
    }

    public RaftNodeState setVotedFor(final String votedFor) {
        this.votedFor = votedFor;
        return this;
    }

    public List<SerializedCommand> getLog() {
        return log;
    }

    public RaftNodeState setLog(final List<SerializedCommand> log) {
        this.log = log;
        return this;
    }

    public int getCommitIndex() {
        return commitIndex;
    }

    public RaftNodeState setCommitIndex(final int commitIndex) {
        this.commitIndex = commitIndex;
        return this;
    }

    public int getLastApplied() {
        return lastApplied;
    }

    public RaftNodeState setLastApplied(final int lastApplied) {
        this.lastApplied = lastApplied;
        return this;
    }

}
