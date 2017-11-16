package me.theyinspire.pandora.core.config;

import me.theyinspire.pandora.core.protocol.Protocol;

import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 2:25 AM)
 */
public interface OptionRegistry {

    List<ProtocolOption> getProtocolOptions(Protocol protocol);

    List<DataStoreOption> getDataStoreOptions(String dataStore);

    ScopedOptionRegistry getProtocolOptionRegistry(Protocol protocol);

    ScopedOptionRegistry getDataStoreOptionRegistry(String dataStore);

}
