package me.theyinspire.pandora.dds.impl;

import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommands;
import me.theyinspire.pandora.dds.Replica;
import me.theyinspire.pandora.dds.ReplicaRegistry;

import java.io.Serializable;
import java.util.Map;
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


    @Override
    public final void init(String signature, String uri, DistributedDataStore dataStore) {
        onBeforeDataSync(signature, uri, dataStore);
        final Set<Replica> replicaSet = getReplicaSet(dataStore);
        for (Replica replica : replicaSet) {
            final Map<String, Serializable> values = replica.send(DataStoreCommands.all());
            for (Map.Entry<String, Serializable> entry : values.entrySet()) {
                final String lock = dataStore.lock(entry.getKey());
                dataStore.store(entry.getKey(), entry.getValue());
                dataStore.unlock(entry.getKey(), lock);
            }
        }
        onAfterDataSync(signature, uri, dataStore);
    }

    protected void onBeforeDataSync(String signature, String uri, DistributedDataStore dataStore) {

    }

    protected void onAfterDataSync(String signature, String uri, DistributedDataStore dataStore) {

    }

}
