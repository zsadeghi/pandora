package me.theyinspire.pandora.raft.impl;

import me.theyinspire.pandora.raft.Stoppable;

import java.util.concurrent.atomic.AtomicBoolean;

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
