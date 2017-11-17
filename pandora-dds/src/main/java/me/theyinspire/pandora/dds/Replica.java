package me.theyinspire.pandora.dds;

import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommand;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 5:56 PM)
 */
public interface Replica {

    String getSignature();

    <R> R send(DataStoreCommand<R> command);

}
