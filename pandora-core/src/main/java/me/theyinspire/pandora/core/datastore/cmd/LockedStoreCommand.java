package me.theyinspire.pandora.core.datastore.cmd;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/17/17, 2:13 PM)
 */
public interface LockedStoreCommand extends LockingDataStoreCommand<Boolean>, HasKey, HasValue, HasLock {
}
