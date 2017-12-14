package me.theyinspire.pandora.raft;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/12/17, 6:15 PM)
 */
public class SerializedCommand {

    private final String command;
    private final int term;

    public SerializedCommand(final String command, final int term) {
        this.command = command;
        this.term = term;
    }

    public String getCommand() {
        return command;
    }

    public int getTerm() {
        return term;
    }

    @Override
    public String toString() {
        return term + "@" + command;
    }

}
