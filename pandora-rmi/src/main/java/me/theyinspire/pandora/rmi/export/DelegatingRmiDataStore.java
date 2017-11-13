package me.theyinspire.pandora.rmi.export;

import me.theyinspire.pandora.core.datastore.DataStore;
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
    public String exit() {
        server.stop();
        return "bye";
    }

}
