package me.theyinspire.pandora.raft;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/12/17, 10:19 PM)
 */
public interface Stoppable extends Runnable {

    void stop();

}
