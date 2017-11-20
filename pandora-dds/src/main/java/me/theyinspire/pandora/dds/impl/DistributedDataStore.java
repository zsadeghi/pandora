package me.theyinspire.pandora.dds.impl;

import me.theyinspire.pandora.core.datastore.DataStoreConfiguration;
import me.theyinspire.pandora.core.datastore.DestroyableDataStore;
import me.theyinspire.pandora.core.datastore.InitializingDataStore;
import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.datastore.cmd.LockingDataStoreCommands;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.dds.Replica;
import me.theyinspire.pandora.dds.ReplicaRegistry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 5:58 PM)
 */
public class DistributedDataStore implements LockingDataStore, InitializingDataStore, DestroyableDataStore {

    private final LockingDataStore delegate;
    private final ReplicaRegistry replicaRegistry;

    public DistributedDataStore(LockingDataStore delegate, ReplicaRegistry replicaRegistry) {
        this.delegate = delegate;
        this.replicaRegistry = replicaRegistry;
    }

    @Override
    public String getUri(ServerConfiguration configuration) {
        return delegate.getUri(configuration);
    }

    @Override
    public String lock(String key) {
        return delegate.lock(key);
    }

    @Override
    public void restore(String key, String lock) {
        delegate.restore(key, lock);
    }

    @Override
    public void unlock(String key, String lock) {
        delegate.unlock(key, lock);
    }

    @Override
    public boolean store(String key, Serializable value, String lock) {
        return delegate.store(key, value, lock);
    }

    @Override
    public boolean delete(String key, String lock) {
        return delegate.delete(key, lock);
    }

    @Override
    public Serializable get(String key, String lock) {
        return delegate.get(key, lock);
    }

    @Override
    public boolean locked(String key) {
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
    public boolean store(String key, Serializable value) {
        // if this is a new item, we don't need to employ any sort of locking mechanism, as
        // this operation will take precedence over competing insertion/deletion operations
        // for the same key
        final String localLock;
        if (has(key)) {
            localLock = lock(key);
        } else {
            localLock = null;
        }
        // first, lock all replicas, and note which ones will need to remain unlocked
        final Map<String, String> locks = new HashMap<>();
        final Set<Replica> replicaSet = replicaRegistry.getReplicaSet(this);
        try {
            for (Replica replica : replicaSet) {
                locks.put(replica.getSignature(), replica.send(LockingDataStoreCommands.lock(key)));
            }
            for (Replica replica : replicaSet) {
                final String replicaLock = locks.get(replica.getSignature());
                replica.send(LockingDataStoreCommands.store(key, replicaLock, value));
            }
            for (Replica replica : replicaSet) {
                final String replicaLock = locks.get(replica.getSignature());
                replica.send(LockingDataStoreCommands.unlock(key, replicaLock));
            }
            if (localLock != null) {
                store(key, value, localLock);
                unlock(key, localLock);
            } else {
                delegate.store(key, value);
            }
            return true;
        } catch (Exception e) {
            for (Replica replica : replicaSet) {
                if (!locks.containsKey(replica.getSignature())) {
                    continue;
                }
                replica.send(LockingDataStoreCommands.restore(key, locks.get(replica.getSignature())));
            }
            if (localLock != null) {
                restore(key, localLock);
            }
            throw new ServerException("Failed to update value: " + key, e);
        }
    }

    @Override
    public Serializable get(String key) {
        return delegate.get(key);
    }

    @Override
    public boolean delete(String key) {
        if (!has(key)) {
            return false;
        }
        final String localLock = lock(key);
        final Set<Replica> replicaSet = replicaRegistry.getReplicaSet(this);
        final Map<String, String> locks = new HashMap<>();
        try {
            for (Replica replica : replicaSet) {
                final String lock = replica.send(LockingDataStoreCommands.lock(key));
                locks.put(replica.getSignature(), lock);
            }
            for (Replica replica : replicaSet) {
                final Boolean deleted = replica.send(LockingDataStoreCommands.delete(key, locks.get(replica.getSignature())));
                if (!deleted) {
                    throw new ServerException("Failed to delete <" + key + "> from replica: " + replica);
                }
            }
            for (Replica replica : replicaSet) {
                replica.send(LockingDataStoreCommands.unlock(key, locks.get(replica.getSignature())));
            }
            unlock(key, localLock);
        } catch (Exception e){
            for (Replica replica : replicaSet) {
                if (!locks.containsKey(replica.getSignature())) {
                    continue;
                }
                replica.send(LockingDataStoreCommands.restore(key, locks.get(replica.getSignature())));
            }
            restore(key, localLock);
            throw new ServerException("Failed to delete key across the cluster", e);
        }
        return true;
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
    public boolean has(String key) {
        return delegate.has(key);
    }

    @Override
    public Map<String, Serializable> all() {
        return delegate.all();
    }

    @Override
    public void destroy(ServerConfiguration serverConfiguration) {
        replicaRegistry.destroy(getSignature(), getUri(serverConfiguration), this);
    }

    @Override
    public void init(ServerConfiguration serverConfiguration, DataStoreConfiguration dataStoreConfiguration) {
        replicaRegistry.init(getSignature(), getUri(serverConfiguration), this);
    }

}
