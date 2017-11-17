package me.theyinspire.pandora.dds.impl;

import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.datastore.cmd.*;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.dds.Replica;
import me.theyinspire.pandora.dds.ReplicaRegistry;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 5:58 PM)
 */
public class DistributedDataStore implements LockingDataStore {

    private final LockingDataStore delegate;
    private final ReplicaRegistry replicaRegistry;

    public DistributedDataStore(LockingDataStore delegate, ReplicaRegistry replicaRegistry) {
        this.delegate = delegate;
        this.replicaRegistry = replicaRegistry;
    }

    @Override
    public long size() {
        return delegate.size();
    }

    @Override
    public String getUri(ServerConfiguration configuration) {
        return delegate.getUri(configuration);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean store(String key, Serializable value) {
        lock(key);
        try {
            if (!lockKeyOnReplicaSet(key)) {
                return false;
            }
            if (!storeOnReplicaSet(key, value)) {
                return false;
            }
            unlockKeyOnReplicaSet(key);
            return delegate.store(key, value);
        } finally {
            unlock(key);
        }
    }

    @Override
    public void lock(String key) {
        delegate.lock(key);
    }

    @Override
    public void restore(String key) {
        delegate.restore(key);
    }

    @Override
    public Serializable get(String key) {
        return delegate.get(key);
    }

    @Override
    public void unlock(String key) {
        delegate.unlock(key);
    }

    @Override
    public boolean delete(String key) {
        lock(key);
        try {
            if (!lockKeyOnReplicaSet(key)) {
                return false;
            }
            if (!deleteOnReplicaSet(key)) {
                return false;
            }
            unlockKeyOnReplicaSet(key);
            return delegate.delete(key);
        } finally {
            unlock(key);
        }
    }

    @Override
    public boolean locked(String key) {
        return delegate.locked(key);
    }

    @Override
    public Set<String> keys() {
        return delegate.keys();
    }

    @Override
    public long truncate() {
        for (Replica replica : replicaRegistry.getReplicaSet(this)) {
            replica.send(DataStoreCommands.truncate());
        }
        return delegate.truncate();
    }

    @Override
    public String getSignature() {
        return delegate.getSignature();
    }

    @Override
    public boolean has(String key) {
        return delegate.has(key);
    }

    @Override
    public Map<String, Serializable> all() {
        return delegate.all();
    }

    private boolean storeOnReplicaSet(String key, Serializable value) {
        final Set<Replica> replicaSet = replicaRegistry.getReplicaSet(this);
        final StoreCommand storeCommand = DataStoreCommands.store(key, value);
        final RestoreCommand restoreCommand = LockingDataStoreCommands.restore(key);
        for (Replica replica : replicaSet) {
            if (!tryWithRollback(replica, storeCommand, restoreCommand, replicaSet)) {
                return false;
            }
        }
        return true;
    }

    private boolean deleteOnReplicaSet(String key) {
        final Set<Replica> replicaSet = replicaRegistry.getReplicaSet(this);
        final DeleteCommand deleteCommand = DataStoreCommands.delete(key);
        final RestoreCommand restoreCommand = LockingDataStoreCommands.restore(key);
        for (Replica replica : replicaSet) {
            if (!tryWithRollback(replica, deleteCommand, restoreCommand, replicaSet)) {
                return false;
            }
        }
        return true;
    }

    private boolean lockKeyOnReplicaSet(String key) {
        final LockCommand lockCommand = LockingDataStoreCommands.lock(key);
        final UnlockCommand unlockCommand = LockingDataStoreCommands.unlock(key);
        final Set<Replica> lockedReplicaSet = new HashSet<>();
        for (Replica replica : replicaRegistry.getReplicaSet(this)) {
            if (!tryWithRollback(replica, lockCommand, unlockCommand, lockedReplicaSet)) {
                return false;
            }
            lockedReplicaSet.add(replica);
        }
        return true;
    }

    private void unlockKeyOnReplicaSet(String key) {
        for (Replica replica : replicaRegistry.getReplicaSet(this)) {
            try {
                replica.send(LockingDataStoreCommands.unlock(key));
            } catch (Exception e) {
                throw new ServerException("Failed to unlock key <" + key + "> on replica <" + replica + ">");
            }
        }
    }

    private boolean tryWithRollback(Replica replica, DataStoreCommand<?> command, DataStoreCommand<?> rollback, Set<Replica> replicaSet) {
        try {
            replica.send(command);
        } catch (Exception e) {
            for (Replica modifiedReplica : replicaSet) {
                modifiedReplica.send(rollback);
            }
            return false;
        }
        return true;
    }

}
