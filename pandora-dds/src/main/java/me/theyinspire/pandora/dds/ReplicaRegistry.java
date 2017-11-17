package me.theyinspire.pandora.dds;

import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.dds.impl.DistributedDataStore;

import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 5:47 PM)
 */
public interface ReplicaRegistry {

    Set<Replica> getReplicaSet(LockingDataStore dataStore);

    default void notify(String signature, String uri, DistributedDataStore dataStore) {}

}
