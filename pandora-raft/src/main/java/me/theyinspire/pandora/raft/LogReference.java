package me.theyinspire.pandora.raft;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/13/17, 7:35 PM)
 */
public interface LogReference extends Comparable<LogReference> {

    int index();

    int term();

    default boolean isRoot() {
        return index() < 0;
    }

    @Override
    default int compareTo(LogReference other) {
        final int termComparison = Integer.compare(term(), other.term());
        if (termComparison != 0) {
            return termComparison;
        }
        return Integer.compare(index(), other.index());
    }

}
