package me.theyinspire.pandora.core.server;

import me.theyinspire.pandora.core.config.MachineConfiguration;
import me.theyinspire.pandora.core.datastore.DataStore;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/31/17, 10:03 PM)
 */
public interface ServerConfiguration extends MachineConfiguration {

    DataStore getDataStore();

}
