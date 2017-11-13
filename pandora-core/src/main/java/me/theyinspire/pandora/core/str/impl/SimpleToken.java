package me.theyinspire.pandora.core.str.impl;

import me.theyinspire.pandora.core.str.Token;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 7:15 PM)
 */
public class SimpleToken implements Token {

    private final int tag;
    private final int start;
    private final int end;
    private final int margin;

    public SimpleToken(int start, int end) {
        this(start, end, NO_TAG);
    }

    public SimpleToken(int start, int end, int tag) {
        this(start, end, 0, tag);
    }

    public SimpleToken(int start, int end, int margin, int tag) {
        this.tag = tag;
        this.start = start;
        this.margin = margin;
        this.end = end;
    }

    @Override
    public int getTag() {
        return tag;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public int getMargin() {
        return margin;
    }

    @Override
    public int getLength() {
        return end - start;
    }

    @Override
    public boolean isTagged() {
        return tag != NO_TAG;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d,%d,%d)", start, end, margin, tag);
    }

}