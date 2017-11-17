package me.theyinspire.pandora.dds.impl;

import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.dds.Replica;
import me.theyinspire.pandora.dds.ReplicaRegistry;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 8:07 PM)
 */
public abstract class AbstractReplicaRegistry implements ReplicaRegistry {

    protected abstract Set<Replica> getReplicaSet();

    @Override
    public Set<Replica> getReplicaSet(LockingDataStore dataStore) {
        return getReplicaSet().stream()
                .filter(replica -> !replica.getSignature().equals(dataStore.getSignature()))
                .collect(Collectors.toSet());
    }

}
