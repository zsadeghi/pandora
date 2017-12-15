package me.theyinspire.pandora.dds.impl;

import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.error.ConfigurationException;
import me.theyinspire.pandora.replica.ReplicaRegistry;
import me.theyinspire.pandora.replica.ReplicaRegistryInitializer;
import me.theyinspire.pandora.replica.impl.AbstractReplicatedDataStoreFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 6:17 PM)
 */
public class DistributedDataStoreFactory extends AbstractReplicatedDataStoreFactory {

    @Override
    public String getName() {
        return "dds";
    }

    @Override
    protected ReplicaRegistryInitializer getInitializer() {
        return new DistributedDataStoreReplicaRegistryInitializer();
    }

    @Override
    protected DataStore createDataStore(DataStore delegate, ReplicaRegistry replicaRegistry, ReplicaRegistryInitializer initializer) {
        if (!(delegate instanceof LockingDataStore)) {
            throw new ConfigurationException(
                    "Data store type <" + delegate + ">  is not capable of handling locking operations");
        }
        final LockingDataStore lockingDataStore = (LockingDataStore) delegate;
        final DistributedDataStore dataStore = new DistributedDataStore(lockingDataStore, replicaRegistry);
        ((DistributedDataStoreReplicaRegistryInitializer) initializer).setDataStore(dataStore);
        return dataStore;
    }

}
