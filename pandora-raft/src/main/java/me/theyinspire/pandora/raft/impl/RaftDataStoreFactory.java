package me.theyinspire.pandora.raft.impl;

import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.DataStoreFactory;
import me.theyinspire.pandora.replica.ReplicaRegistry;
import me.theyinspire.pandora.replica.ReplicaRegistryInitializer;
import me.theyinspire.pandora.replica.impl.AbstractReplicatedDataStoreFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/12/17, 11:24 PM)
 */
public class RaftDataStoreFactory extends AbstractReplicatedDataStoreFactory implements DataStoreFactory {

    @Override
    public String getName() {
        return "raft";
    }

    @Override
    protected DataStore createDataStore(DataStore delegate, ReplicaRegistry replicaRegistry, ReplicaRegistryInitializer initializer) {
        return new RaftDataStore(delegate, replicaRegistry);
    }

}
