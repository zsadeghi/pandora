package me.theyinspire.pandora.core.config;

import me.theyinspire.pandora.core.datastore.DataStoreConfiguration;
import me.theyinspire.pandora.core.protocol.Protocol;
import me.theyinspire.pandora.core.server.ServerConfiguration;

import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 12:03 PM)
 */
public interface ServerExecutionConfiguration extends ExecutionConfiguration {

    List<Protocol> getProtocols();

    ServerConfiguration getServerConfiguration(Protocol protocol);

    DataStoreConfiguration getDataStoreConfiguration();

}
