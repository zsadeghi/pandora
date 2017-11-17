package me.theyinspire.pandora.core.datastore.impl;

import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.server.ServerConfiguration;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:25 PM)
 */
public class SynchronizedDataStore implements LockingDataStore {

    private final DataStore delegate;

    public SynchronizedDataStore(DataStore delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized long size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public synchronized boolean store(String key, Serializable value) {
        return delegate.store(key, value);
    }

    @Override
    public synchronized Serializable get(String key) {
        return delegate.get(key);
    }

    @Override
    public synchronized boolean delete(String key) {
        return delegate.delete(key);
    }

    @Override
    public synchronized Set<String> keys() {
        return delegate.keys();
    }

    @Override
    public synchronized long truncate() {
        return delegate.truncate();
    }

    @Override
    public synchronized boolean has(String key) {
        return delegate.has(key);
    }

    @Override
    public synchronized Map<String, Serializable> all() {
        return delegate.all();
    }

    @Override
    public synchronized String getUri(ServerConfiguration configuration) {
        if (delegate instanceof LockingDataStore) {
            return ((LockingDataStore) delegate).getUri(configuration);
        }
        return null;
    }

    @Override
    public synchronized void lock(String key) {
        if (delegate instanceof LockingDataStore) {
            ((LockingDataStore) delegate).lock(key);
        }
    }

    @Override
    public synchronized void restore(String key) {
        if (delegate instanceof LockingDataStore) {
            ((LockingDataStore) delegate).restore(key);
        }
    }

    @Override
    public synchronized void unlock(String key) {
        if (delegate instanceof LockingDataStore) {
            ((LockingDataStore) delegate).unlock(key);
        }
    }

    @Override
    public synchronized boolean locked(String key) {
        return delegate instanceof LockingDataStore && ((LockingDataStore) delegate).locked(key);
    }

}
