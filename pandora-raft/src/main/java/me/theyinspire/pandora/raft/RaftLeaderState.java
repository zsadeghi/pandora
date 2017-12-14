package me.theyinspire.pandora.raft;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/12/17, 9:52 PM)
 */
public class RaftLeaderState {

    private Map<String, Integer> lastIndex;
    private Map<String, Integer> matchIndex;

    public RaftLeaderState() {
        lastIndex = new ConcurrentHashMap<>();
        matchIndex = new ConcurrentHashMap<>();
    }

    public Map<String, Integer> getLastIndex() {
        return lastIndex;
    }

    public RaftLeaderState setLastIndex(final Map<String, Integer> lastIndex) {
        this.lastIndex = lastIndex;
        return this;
    }

    public Map<String, Integer> getMatchIndex() {
        return matchIndex;
    }

    public RaftLeaderState setMatchIndex(final Map<String, Integer> matchIndex) {
        this.matchIndex = matchIndex;
        return this;
    }

}
