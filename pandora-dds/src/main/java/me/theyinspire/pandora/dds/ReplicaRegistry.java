package me.theyinspire.pandora.dds;

import me.theyinspire.pandora.core.datastore.LockingDataStore;

import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 5:47 PM)
 */
public interface ReplicaRegistry {

    Set<Replica> getReplicaSet(LockingDataStore dataStore);

}
