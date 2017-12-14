package me.theyinspire.pandora.raft;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/12/17, 10:20 PM)
 */
public abstract class AbstractStoppable implements Stoppable {

    private final AtomicBoolean stopped;

    public AbstractStoppable() {
        stopped = new AtomicBoolean(false);
    }

    @Override
    public void stop() {
        stopped.set(true);
    }

    @Override
    public void run() {
        while (!stopped.get()) {
            iterate();
        }
    }

    protected abstract void iterate();

}
