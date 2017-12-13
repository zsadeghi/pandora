package me.theyinspire.pandora.dds.impl;

import me.theyinspire.pandora.core.config.ScopedOptionRegistry;
import me.theyinspire.pandora.core.config.ServerExecutionConfiguration;
import me.theyinspire.pandora.core.config.impl.DefaultDataStoreConfiguration;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.DataStoreConfiguration;
import me.theyinspire.pandora.core.datastore.DataStoreFactory;
import me.theyinspire.pandora.core.datastore.LockingDataStore;
import me.theyinspire.pandora.core.datastore.impl.DefaultDataStoreRegistry;
import me.theyinspire.pandora.core.error.ConfigurationException;
import me.theyinspire.pandora.replica.ReplicaRegistry;
import me.theyinspire.pandora.replica.ReplicaRegistryInitializer;
import me.theyinspire.pandora.replica.impl.BeaconReplicaRegistry;
import me.theyinspire.pandora.replica.impl.ConfigurationFileReplicaRegistry;
import me.theyinspire.pandora.replica.impl.DataStoreReplicaRegistry;

import java.io.File;
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
        final ServerExecutionConfiguration executionConfig =
                (ServerExecutionConfiguration) configuration.getConfiguration();
        final DataStoreConfiguration delegateConfig = new DefaultDataStoreConfiguration(executionConfig, delegateName);
        final DataStore delegate = DefaultDataStoreRegistry.getInstance().get(delegateName, delegateConfig);
        if (!(delegate instanceof LockingDataStore)) {
            throw new ConfigurationException(
                    "Data store type <" + delegateName + ">  is not capable of handling locking operations");
        }
        final LockingDataStore lockingDataStore = (LockingDataStore) delegate;
        final DistributedDataStoreReplicaRegistryInitializer initializer = new DistributedDataStoreReplicaRegistryInitializer();
        final ReplicaRegistry replicaRegistry = getReplicaRegistry(configuration, initializer);
        final DistributedDataStore dataStore = new DistributedDataStore(lockingDataStore, replicaRegistry);
        initializer.setDataStore(dataStore);
        return dataStore;
    }

    private ReplicaRegistry getReplicaRegistry(DataStoreConfiguration configuration,
                                               final ReplicaRegistryInitializer initializer) {
        final ReplicaDiscoveryMode discoveryMode = ReplicaDiscoveryMode.valueOf(
                configuration.require("discovery").toUpperCase());
        final ReplicaRegistry replicaRegistry;
        switch (discoveryMode) {
            case FILE:
                replicaRegistry = new ConfigurationFileReplicaRegistry(new File(configuration.require("replica-file")),
                                                                       Integer.parseInt(configuration.require(
                                                                               "replica-file-refresh")),
                                                                       initializer);
                break;
            case BEACON:
                replicaRegistry = new BeaconReplicaRegistry(Integer.parseInt(configuration.require("beacon-port")),
                                                            initializer);
                break;
            case REGISTRY:
                replicaRegistry = new DataStoreReplicaRegistry(configuration.require("registry-uri"), initializer);
                break;
            default:
                throw new ConfigurationException("Unknown replica registry type");
        }
        return replicaRegistry;
    }

    @Override
    public void defineOptions(ScopedOptionRegistry optionRegistry) {
        final List<String> discoveryModes = Arrays.stream(ReplicaDiscoveryMode.values())
                                                  .map(Enum::name)
                                                  .map(String::toLowerCase)
                                                  .sorted()
                                                  .collect(Collectors.toList());
        optionRegistry.register("delegate", "The type of the underlying data store to use", "memory");
        optionRegistry.register("replica-file",
                                "The name of the file containing replica URIs. Required if you set discovery to "
                                        + "`file`.");
        optionRegistry.register("replica-file-refresh",
                                "Milliseconds between refreshes of the file. `0` means no refresh", "0");
        optionRegistry.register("beacon-port", "The beacon port for UDP replica registry", "10101");
        optionRegistry.register("registry-uri",
                                "The URI for the registry containing replica information. Required if you set discovery to `registry`.");
        optionRegistry.register("discovery", "The mode of discovery, can be one of " + discoveryModes,
                                ReplicaDiscoveryMode.BEACON.name().toLowerCase());
    }

    private enum ReplicaDiscoveryMode {

        FILE,
        BEACON,
        REGISTRY

    }

}
