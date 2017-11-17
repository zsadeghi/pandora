package me.theyinspire.pandora.dds.impl;

import me.theyinspire.pandora.core.config.Configuration;
import me.theyinspire.pandora.core.config.ScopedOptionRegistry;
import me.theyinspire.pandora.core.config.impl.DefaultDataStoreConfiguration;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.DataStoreConfiguration;
import me.theyinspire.pandora.core.datastore.DataStoreFactory;
import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.datastore.impl.DefaultDataStoreRegistry;
import me.theyinspire.pandora.core.error.ConfigurationException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 6:17 PM)
 */
public class DistributedDataStoreFactory implements DataStoreFactory {

    @Override
    public String getName() {
        return "dds";
    }

    @Override
    public DataStore getDataStore(DataStoreConfiguration configuration) {
        final String delegateName = configuration.require("delegate");
        final Configuration executionConfig = configuration.getConfiguration();
        final DataStoreConfiguration delegateConfig = new DefaultDataStoreConfiguration(executionConfig, delegateName);
        final DataStore delegate = DefaultDataStoreRegistry.getInstance().get(delegateName, delegateConfig);
        if (!(delegate instanceof LockingDataStore)) {
            throw new ConfigurationException("Data store type <" + delegateName + ">  is not capable of handling locking operations");
        }
        return new DistributedDataStore((LockingDataStore) delegate, null);
    }

    @Override
    public void defineOptions(ScopedOptionRegistry optionRegistry) {
        final List<String> discoveryModes = Arrays.stream(ReplicaDiscoveryMode.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.toList());
        optionRegistry.register("delegate", "The type of the underlying data store to use", "memory");
        optionRegistry.register("replica-file", "The name of the file containing replica URIs");
        optionRegistry.register("beacon", "The beacon port for UDP replica registry", "10101");
        optionRegistry.register("registry-uri", "The URI for the registry containing replica information");
        optionRegistry.register("discovery", "The mode of discovery, can be one of " + discoveryModes, ReplicaDiscoveryMode.ANNOUNCE.name().toLowerCase());
    }

    private enum ReplicaDiscoveryMode {

        FILE, ANNOUNCE, REGISTRY

    }

}
