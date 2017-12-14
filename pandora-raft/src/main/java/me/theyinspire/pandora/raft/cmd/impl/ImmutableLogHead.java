package me.theyinspire.pandora.raft.cmd.impl;

import me.theyinspire.pandora.raft.cmd.LogHead;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:35 PM)
 */
public class ImmutableLogHead implements LogHead {

    private final int index;
    private final int term;

    public ImmutableLogHead(LogHead head) {
        this(head.index(), head.term());
    }

    public ImmutableLogHead(final int index, final int term) {
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
