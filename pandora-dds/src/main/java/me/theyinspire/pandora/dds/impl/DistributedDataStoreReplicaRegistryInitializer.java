package me.theyinspire.pandora.dds.impl;

import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommands;
import me.theyinspire.pandora.replica.Replica;
import me.theyinspire.pandora.replica.ReplicaRegistry;
import me.theyinspire.pandora.replica.ReplicaRegistryInitializer;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/12/17, 5:51 PM)
 */
public class DistributedDataStoreReplicaRegistryInitializer implements ReplicaRegistryInitializer {

    private DistributedDataStore dataStore;

    @Override
    public void init(final ReplicaRegistry registry) {
        final Set<Replica> replicaSet = registry.getReplicaSetFor(this.dataStore.getSignature());
        for (Replica replica : replicaSet) {
            final Map<String, Serializable> values = replica.send(DataStoreCommands.all());
            for (Map.Entry<String, Serializable> entry : values.entrySet()) {
                final String lock = this.dataStore.lock(entry.getKey());
                this.dataStore.store(entry.getKey(), entry.getValue(), lock);
                this.dataStore.unlock(entry.getKey(), lock);
            }
        }
    }

    public void setDataStore(final DistributedDataStore dataStore) {
        this.dataStore = dataStore;
    }
}
