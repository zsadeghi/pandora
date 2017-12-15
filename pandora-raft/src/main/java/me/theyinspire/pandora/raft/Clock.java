package me.theyinspire.pandora.raft;

public interface Clock {

    void reset();

    boolean timedOut();

    void waitQuietly();

    void waitQuietly(double factor);

}
