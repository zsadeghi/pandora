package me.theyinspire.pandora.core.datastore.cmd;

import me.theyinspire.pandora.core.server.ServerConfiguration;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 4:22 PM)
 */
public interface GetUriCommand extends DataStoreCommand<String> {

    ServerConfiguration getServerConfiguration();

}
