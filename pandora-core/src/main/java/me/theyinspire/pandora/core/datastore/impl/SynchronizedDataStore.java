package me.theyinspire.pandora.core.datastore.impl;

import me.theyinspire.pandora.core.cmd.CommandWithArguments;
import me.theyinspire.pandora.core.datastore.*;
import me.theyinspire.pandora.core.server.ServerConfiguration;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:25 PM)
 */
public class SynchronizedDataStore implements LockingDataStore, InitializingDataStore, DestroyableDataStore, CommandReceiver {

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
    public synchronized String lock(String key) {
        if (delegate instanceof LockingDataStore) {
            return ((LockingDataStore) delegate).lock(key);
        }
        return null;
    }

    @Override
    public synchronized void restore(String key, String lock) {
        if (delegate instanceof LockingDataStore) {
            ((LockingDataStore) delegate).restore(key, lock);
        }
    }

    @Override
    public synchronized void unlock(String key, String lock) {
        if (delegate instanceof LockingDataStore) {
            ((LockingDataStore) delegate).unlock(key, lock);
        }
    }

    @Override
    public boolean store(String key, Serializable value, String lock) {
        return delegate instanceof LockingDataStore && ((LockingDataStore) delegate).store(key, value, lock);
    }

    @Override
    public boolean delete(String key, String lock) {
        return delegate instanceof LockingDataStore && ((LockingDataStore) delegate).delete(key, lock);
    }

    @Override
    public Serializable get(String key, String lock) {
        if (delegate instanceof LockingDataStore) {
            return ((LockingDataStore) delegate).get(key, lock);
        }
        return null;
    }

    @Override
    public synchronized boolean locked(String key) {
        return delegate instanceof LockingDataStore && ((LockingDataStore) delegate).locked(key);
    }

    @Override
    public String getSignature() {
        if (delegate instanceof LockingDataStore) {
            return ((LockingDataStore) delegate).getSignature();
        }
        return null;
    }

    public DataStore getDelegate() {
        return delegate;
    }

    @Override
    public void init(ServerConfiguration serverConfiguration, DataStoreConfiguration dataStoreConfiguration) {
        if (delegate instanceof InitializingDataStore) {
            InitializingDataStore dataStore = (InitializingDataStore) delegate;
            dataStore.init(serverConfiguration, dataStoreConfiguration);
        }
    }

    @Override
    public void destroy(ServerConfiguration serverConfiguration) {
        if (delegate instanceof DestroyableDataStore) {
            ((DestroyableDataStore) delegate).destroy(serverConfiguration);
        }
    }

    @Override
    public String receive(final CommandWithArguments command) {
        if (delegate instanceof CommandReceiver) {
            CommandReceiver receiver = (CommandReceiver) delegate;
            return receiver.receive(command);
        }
        return null;
    }

}
