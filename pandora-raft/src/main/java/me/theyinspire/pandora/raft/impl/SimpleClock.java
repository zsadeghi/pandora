package me.theyinspire.pandora.raft.impl;

import me.theyinspire.pandora.raft.Clock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.function.Supplier;

public class SimpleClock implements Clock {

    private static final Log LOG = LogFactory.getLog("pandora.server.clock");

    private long timeout;
    private long timestamp;
    private Supplier<Long> timeoutSupplier;

    public SimpleClock(Supplier<Long> timeoutSupplier) {
        this.timeoutSupplier = timeoutSupplier;
        this.timeout = timeoutSupplier.get();
        this.timestamp = 0;
    }

    @Override
    public void reset() {
        this.timestamp = System.currentTimeMillis();
        this.timeout = timeoutSupplier.get();
        LOG.info("Resetting the clock: " + timestamp + " with timeout " + timeout + "ms");
    }

    @Override
    public boolean timedOut() {
        final long currentTimeMillis = System.currentTimeMillis();
        final boolean timedOut = currentTimeMillis - timestamp > timeout;
        if (timedOut) {
            LOG.info("Clock timed out at " + currentTimeMillis + " with difference " + (currentTimeMillis - timestamp) + "ms > " + timeout + "ms");
        }
        return timedOut;
    }

    @Override
    public void waitQuietly() {
        waitQuietly(1);
    }

    @Override
    public void waitQuietly(double factor) {
        final long wait = (long) (timeout * factor);
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
