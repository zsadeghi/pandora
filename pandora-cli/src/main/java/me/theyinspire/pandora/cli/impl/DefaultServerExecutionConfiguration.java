package me.theyinspire.pandora.cli.impl;

import me.theyinspire.pandora.core.config.Configuration;
import me.theyinspire.pandora.core.config.ExecutionMode;
import me.theyinspire.pandora.core.config.ServerExecutionConfiguration;
import me.theyinspire.pandora.core.config.impl.DefaultDataStoreConfiguration;
import me.theyinspire.pandora.core.config.impl.DefaultServerConfiguration;
import me.theyinspire.pandora.core.datastore.*;
import me.theyinspire.pandora.core.datastore.impl.DefaultDataStoreRegistry;
import me.theyinspire.pandora.core.protocol.Protocol;
import me.theyinspire.pandora.core.protocol.ProtocolRegistry;
import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;
import me.theyinspire.pandora.core.server.ServerConfiguration;

import java.util.*;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 12:15 PM)
 */
public class DefaultServerExecutionConfiguration extends AbstractExecutionConfiguration implements ServerExecutionConfiguration {

    private final List<Protocol> protocols;
    private final String dataStoreName;
    private final DataStore dataStore;
    private final Map<Protocol, ServerConfiguration> configurations;
    private final DataStoreConfiguration dataStoreConfiguration;

    public DefaultServerExecutionConfiguration(Map<String, String> data, Configuration parent) {
        super(ExecutionMode.SERVER, data, parent);
        configurations = new HashMap<>();
        this.protocols = Collections.unmodifiableList(deduceProtocols());
        this.dataStoreName = deduceDataStoreName();
        dataStoreConfiguration = new DefaultDataStoreConfiguration(this, dataStoreName);
        this.dataStore = deduceDataStore();
        final Map<String, List<Runnable>> shutdownHooks = new HashMap<>();
        for (Protocol protocol : protocols) {
            shutdownHooks.put(protocol.getName(), new ArrayList<>());
            configurations.put(protocol, new DefaultServerConfiguration(this, protocol, dataStore, shutdownHooks.get(protocol.getName())));
        }
        if (dataStore instanceof InitializingDataStore) {
            InitializingDataStore store = (InitializingDataStore) dataStore;
            for (ServerConfiguration configuration : configurations.values()) {
                store.init(configuration, dataStoreConfiguration);
            }
        }
        if (dataStore instanceof DestroyableDataStore) {
            DestroyableDataStore store = (DestroyableDataStore) dataStore;
            for (Protocol protocol : protocols) {
                shutdownHooks.get(protocol.getName()).add(() -> ((DestroyableDataStore) dataStore).destroy(getServerConfiguration(protocol)));
            }
        }
    }

    private String deduceDataStoreName() {
        final DataStoreRegistry registry = DefaultDataStoreRegistry.getInstance();
        final List<String> knownDataStores = registry.getKnownDataStores();
        if (knownDataStores.isEmpty()) {
            throw new IllegalArgumentException("No known data stores");
        }
        return get("data-store", knownDataStores.get(0));
    }

    private DataStore deduceDataStore() {
        final DataStoreRegistry registry = DefaultDataStoreRegistry.getInstance();
        final List<String> knownDataStores = registry.getKnownDataStores();
        if (knownDataStores.isEmpty()) {
            throw new IllegalArgumentException("No known data stores");
        }
        return registry.get(dataStoreName, dataStoreConfiguration);
    }

    private List<Protocol> deduceProtocols() {
        final List<Protocol> protocols = new ArrayList<>();
        final ProtocolRegistry protocolRegistry = DefaultProtocolRegistry.getInstance();
        if (!has("protocols")) {
            final List<Protocol> knownProtocols = protocolRegistry.getKnownProtocols();
            if (knownProtocols.isEmpty()) {
                throw new IllegalStateException("There are no known protocols");
            }
            protocols.add(knownProtocols.get(0));
        } else {
            final String[] protocolNames = get("protocols").trim().split("\\s*,\\s*");
            for (String protocolName : protocolNames) {
                final Protocol protocol = protocolRegistry.getProtocolByName(protocolName);
                protocols.add(protocol);
            }
        }
        return protocols;
    }

    @Override
    public List<Protocol> getProtocols() {
        return protocols;
    }

    @Override
    public ServerConfiguration getServerConfiguration(Protocol protocol) {
        return configurations.get(protocol);
    }

    @Override
    public DataStoreConfiguration getDataStoreConfiguration() {
        return dataStoreConfiguration;
    }

}
