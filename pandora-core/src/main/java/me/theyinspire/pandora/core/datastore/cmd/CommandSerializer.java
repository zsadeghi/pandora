package me.theyinspire.pandora.core.datastore.cmd;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 1:43 PM)
 */
public interface CommandSerializer {

    String serializeCommand(DataStoreCommand<?> command);

    String serializeResponse(DataStoreCommand<?> command, Object response);

}
