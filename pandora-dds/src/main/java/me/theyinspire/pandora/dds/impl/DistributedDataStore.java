package me.theyinspire.pandora.dds.impl;

import me.theyinspire.pandora.core.datastore.DataStoreConfiguration;
import me.theyinspire.pandora.core.datastore.DestroyableDataStore;
import me.theyinspire.pandora.core.datastore.InitializingDataStore;
import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.datastore.cmd.*;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.UriServerConfigurationWriter;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.core.server.impl.DefaultUriServerConfigurationWriter;
import me.theyinspire.pandora.dds.Replica;
import me.theyinspire.pandora.dds.ReplicaRegistry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 5:58 PM)
 */
public class DistributedDataStore implements LockingDataStore, InitializingDataStore, DestroyableDataStore {

    private final LockingDataStore delegate;
    private final ReplicaRegistry replicaRegistry;
    private final UriServerConfigurationWriter serverConfigurationWriter;

    public DistributedDataStore(LockingDataStore delegate, ReplicaRegistry replicaRegistry) {
        serverConfigurationWriter = new DefaultUriServerConfigurationWriter();
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
        if (locked(key)) {
            return delegate.store(key, value);
        }
        final String lock = lock(key);
        try {
            final Map<String, String> locks = lockKeyOnReplicaSet(key);
            if (locks == null) {
                return false;
            }
            if (!storeOnReplicaSet(key, value, locks)) {
                return false;
            }
            unlockKeyOnReplicaSet(key, locks);
            return delegate.store(key, value);
        } finally {
            unlock(key, lock);
        }
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
    public Serializable get(String key) {
        return delegate.get(key);
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
    public boolean delete(String key) {
        if (locked(key)) {
            return delegate.delete(key);
        }
        final String lock = lock(key);
        try {
            final Map<String, String> locks = lockKeyOnReplicaSet(key);
            if (locks == null) {
                return false;
            }
            if (!deleteOnReplicaSet(key, locks)) {
                return false;
            }
            unlockKeyOnReplicaSet(key, locks);
            return delegate.delete(key);
        } finally {
            unlock(key, lock);
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

    private boolean storeOnReplicaSet(String key, Serializable value, Map<String, String> locks) {
        final Set<Replica> replicaSet = replicaRegistry.getReplicaSet(this);
        final StoreCommand storeCommand = DataStoreCommands.store(key, value);
        for (Replica replica : replicaSet) {
            final RestoreCommand restoreCommand = LockingDataStoreCommands.restore(key, locks.get(replica.getSignature()));
            try {
                replica.send(storeCommand);
            } catch (Exception e) {
                for (Replica modifiedReplica : replicaSet) {
                    modifiedReplica.send(restoreCommand);
                }
                return false;
            }
        }
        return true;
    }

    private boolean deleteOnReplicaSet(String key, Map<String, String> locks) {
        final Set<Replica> replicaSet = replicaRegistry.getReplicaSet(this);
        final DeleteCommand deleteCommand = DataStoreCommands.delete(key);
        for (Replica replica : replicaSet) {
            final RestoreCommand restoreCommand = LockingDataStoreCommands.restore(key, locks.get(replica.getSignature()));
            try {
                replica.send(deleteCommand);
            } catch (Exception e) {
                for (Replica modifiedReplica : replicaSet) {
                    modifiedReplica.send(restoreCommand);
                }
                return false;
            }
        }
        return true;
    }

    private Map<String, String> lockKeyOnReplicaSet(String key) {
        final Map<String, String> locks = new HashMap<>();
        final LockCommand lockCommand = LockingDataStoreCommands.lock(key);
        final Set<Replica> lockedReplicaSet = new HashSet<>();
        for (Replica replica : replicaRegistry.getReplicaSet(this)) {
            final String lock;
            try {
                try {
                    lock = replica.send(lockCommand);
                } catch (Exception e) {
                    for (Replica modifiedReplica : lockedReplicaSet) {
                        final UnlockCommand unlockCommand = LockingDataStoreCommands.unlock(key, locks.get(modifiedReplica.getSignature()));
                        modifiedReplica.send(unlockCommand);
                    }
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
            locks.put(replica.getSignature(), lock);
            lockedReplicaSet.add(replica);
        }
        return locks;
    }

    private void unlockKeyOnReplicaSet(String key, Map<String, String> locks) {
        for (Replica replica : replicaRegistry.getReplicaSet(this)) {
            try {
                replica.send(LockingDataStoreCommands.unlock(key, locks.get(replica.getSignature())));
            } catch (Exception e) {
                throw new ServerException("Failed to unlock key <" + key + "> on replica <" + replica + ">");
            }
        }
    }

    private <R> R tryWithRollback(Replica replica, DataStoreCommand<R> command, DataStoreCommand<?> rollback, Set<Replica> replicaSet) {
        try {
            return replica.send(command);
        } catch (Exception e) {
            for (Replica modifiedReplica : replicaSet) {
                modifiedReplica.send(rollback);
            }
            throw new ServerException("Failed to work the command " + command, e);
        }
    }

    @Override
    public void init(ServerConfiguration serverConfiguration, DataStoreConfiguration dataStoreConfiguration) {
        replicaRegistry.init(getSignature(), serverConfigurationWriter.write(serverConfiguration), this);
    }

    @Override
    public void destroy(ServerConfiguration serverConfiguration) {
        replicaRegistry.destroy(getSignature(), serverConfigurationWriter.write(serverConfiguration), this);
    }

}
