package me.theyinspire.pandora.replica;

import me.theyinspire.pandora.core.cmd.Command;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 5:56 PM)
 */
public interface Replica {

    String getSignature();

    <R> R send(Command<R> command);

}
