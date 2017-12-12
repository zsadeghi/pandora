package me.theyinspire.pandora.rmi.export;

import me.theyinspire.pandora.core.cmd.CommandWithArguments;
import me.theyinspire.pandora.core.datastore.CommandReceiver;
import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.error.ServerException;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 2:58 PM)
 */
public class RmiDataStoreWrapper implements LockingDataStore, CommandReceiver {

    private final RmiDataStore delegate;

    public RmiDataStoreWrapper(RmiDataStore delegate) {
        this.delegate = delegate;
    }

    @Override
    public long size() {
        try {
            return delegate.size();
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public boolean isEmpty() {
        try {
            return delegate.isEmpty();
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public boolean store(String key, Serializable value) {
        try {
            return delegate.store(key, value);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public Serializable get(String key) {
        try {
            return delegate.get(key);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public boolean delete(String key) {
        try {
            return delegate.delete(key);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public Set<String> keys() {
        try {
            return delegate.keys();
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public long truncate() {
        try {
            return delegate.truncate();
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public boolean has(String key) {
        try {
            return delegate.has(key);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public Map<String, Serializable> all() {
        try {
            return delegate.all();
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public String getUri(ServerConfiguration configuration) {
        try {
            return delegate.getUri(configuration);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public String lock(String key) {
        try {
            return delegate.lock(key);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public void restore(String key, String lock) {
        try {
            delegate.restore(key, lock);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public void unlock(String key, String lock) {
        try {
            delegate.unlock(key, lock);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public boolean store(String key, Serializable value, String lock) {
        try {
            return delegate.store(key, value, lock);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public boolean delete(String key, String lock) {
        try {
            return delegate.delete(key, lock);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public Serializable get(String key, String lock) {
        try {
            return delegate.get(key, lock);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public boolean locked(String key) {
        try {
            return delegate.locked(key);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public String getSignature() {
        try {
            return delegate.getSignature();
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

    @Override
    public String receive(final CommandWithArguments command) {
        try {
            return delegate.receive(command);
        } catch (RemoteException e) {
            throw new ServerException("RMI transaction failed", e);
        }
    }

}
