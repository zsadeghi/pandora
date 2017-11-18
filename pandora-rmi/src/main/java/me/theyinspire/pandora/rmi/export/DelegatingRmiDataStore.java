package me.theyinspire.pandora.rmi.export;

import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.rmi.server.RmiServer;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 2:52 PM)
 */
public class DelegatingRmiDataStore implements RmiDataStore {

    private final DataStore delegate;
    private final RmiServer server;

    public DelegatingRmiDataStore(DataStore delegate, RmiServer server) {
        this.delegate = delegate;
        this.server = server;
    }

    @Override
    public long size() throws RemoteException {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() throws RemoteException {
        return delegate.isEmpty();
    }

    @Override
    public boolean store(String key, Serializable value) throws RemoteException {
        return delegate.store(key, value);
    }

    @Override
    public Serializable get(String key) throws RemoteException {
        return delegate.get(key);
    }

    @Override
    public boolean delete(String key) throws RemoteException {
        return delegate.delete(key);
    }

    @Override
    public Set<String> keys() throws RemoteException {
        return delegate.keys();
    }

    @Override
    public long truncate() throws RemoteException {
        return delegate.truncate();
    }

    @Override
    public boolean has(String key) throws RemoteException {
        return delegate.has(key);
    }

    @Override
    public Map<String, Serializable> all() throws RemoteException {
        return delegate.all();
    }

    @Override
    public String exit() throws RemoteException {
        server.stop();
        return "bye";
    }

    @Override
    public String getUri(ServerConfiguration configuration) throws RemoteException {
        if (delegate instanceof LockingDataStore) {
            return ((LockingDataStore) delegate).getUri(configuration);
        }
        return null;
    }

    @Override
    public String lock(String key) throws RemoteException {
        if (delegate instanceof LockingDataStore) {
            return ((LockingDataStore) delegate).lock(key);
        }
        return null;
    }

    @Override
    public void restore(String key, String lock) throws RemoteException {
        if (delegate instanceof LockingDataStore) {
            ((LockingDataStore) delegate).restore(key, lock);
        }
    }

    @Override
    public void unlock(String key, String lock) throws RemoteException {
        if (delegate instanceof LockingDataStore) {
            ((LockingDataStore) delegate).unlock(key, lock);
        }
    }

    @Override
    public boolean locked(String key) throws RemoteException {
        return delegate instanceof LockingDataStore && ((LockingDataStore) delegate).locked(key);
    }

    @Override
    public String getSignature() throws RemoteException {
        return delegate instanceof LockingDataStore ? ((LockingDataStore) delegate).getSignature() : null;
    }

    @Override
    public boolean store(String key, Serializable value, String lock) throws RemoteException {
        return delegate instanceof LockingDataStore && ((LockingDataStore) delegate).store(key, value, lock);
    }

    @Override
    public boolean delete(String key, String lock) throws RemoteException {
        return delegate instanceof LockingDataStore && ((LockingDataStore) delegate).delete(key, lock);
    }

    @Override
    public Serializable get(String key, String lock) throws RemoteException {
        if (delegate instanceof LockingDataStore) {
            return ((LockingDataStore) delegate).get(key, lock);
        }
        return null;
    }

}
