package me.theyinspire.pandora.raft.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.datastore.*;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.replica.ReplicaRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/12/17, 6:20 PM)
 */
public class RaftDataStore implements LockingDataStore, CommandReceiver, InitializingDataStore, DestroyableDataStore {

    private static final Log LOG = LogFactory.getLog("pandora.server.raft");
    private static final int HALFLIFE = 10000;
    private final LockingDataStore delegate;
    private final ReplicaRegistry replicaRegistry;

    public RaftDataStore(final LockingDataStore delegate,
                         final ReplicaRegistry replicaRegistry) {
        this.delegate = delegate;
        this.replicaRegistry = replicaRegistry;
        LOG.info("Starting RAFT node: " + delegate.getSignature());
    }

    @Override
    public String getUri(final ServerConfiguration configuration) {
        return delegate.getUri(configuration);
    }

    @Override
    public String lock(final String key) {
        return delegate.lock(key);
    }

    @Override
    public void restore(final String key, final String lock) {
        delegate.restore(key, lock);
    }

    @Override
    public void unlock(final String key, final String lock) {
        delegate.unlock(key, lock);
    }

    @Override
    public boolean store(final String key, final Serializable value, final String lock) {
        return delegate.store(key, value, lock);
    }

    @Override
    public boolean delete(final String key, final String lock) {
        return delegate.delete(key, lock);
    }

    @Override
    public Serializable get(final String key, final String lock) {
        return delegate.get(key, lock);
    }

    @Override
    public boolean locked(final String key) {
        return delegate.locked(key);
    }

    @Override
    public String getSignature() {
        return delegate.getSignature();
    }

    @Override
    public long size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean store(final String key, final Serializable value) {
        return delegate.store(key, value);
    }

    @Override
    public Serializable get(final String key) {
        return delegate.get(key);
    }

    @Override
    public boolean delete(final String key) {
        return delegate.delete(key);
    }

    @Override
    public Set<String> keys() {
        return delegate.keys();
    }

    @Override
    public long truncate() {
        return delegate.truncate();
    }

    @Override
    public boolean has(final String key) {
        return delegate.has(key);
    }

    @Override
    public Map<String, Serializable> all() {
        return delegate.all();
    }

    @Override
    public <R> R receive(final Command<R> command) {
        throw new UnsupportedOperationException("Command not recognized: " + command);
    }

    @Override
    public void destroy(final ServerConfiguration serverConfiguration) {
        replicaRegistry.destroy(getSignature());
    }

    @Override
    public void init(final ServerConfiguration serverConfiguration,
                     final DataStoreConfiguration dataStoreConfiguration) {
        replicaRegistry.init(getSignature(), getUri(serverConfiguration));
    }

}
