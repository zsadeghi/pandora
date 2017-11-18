package me.theyinspire.pandora.core.datastore.cmd;

import java.io.Serializable;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/17/17, 2:13 PM)
 */
public interface LockedGetCommand extends LockingDataStoreCommand<Serializable>, HasKey, HasLock {
}
