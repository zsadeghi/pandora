package me.theyinspire.pandora.raft.cmd.impl;

import me.theyinspire.pandora.raft.LogEntry;
import me.theyinspire.pandora.raft.LogReference;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:35 PM)
 */
public class ImmutableLogReference implements LogReference {

    private final int index;
    private final int term;

    public ImmutableLogReference(int index, LogEntry entry) {
        this(entry.term(), index);
    }

    public ImmutableLogReference(LogReference head) {
        this(head.index(), head.term());
    }

    public ImmutableLogReference(final int index, final int term) {
        this.index = index;
        this.term = term;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public int term() {
        return term;
    }

}
