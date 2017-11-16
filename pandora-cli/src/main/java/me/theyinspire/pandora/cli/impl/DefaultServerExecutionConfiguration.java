package me.theyinspire.pandora.cli.impl;

import me.theyinspire.pandora.cli.ExecutionMode;
import me.theyinspire.pandora.cli.ServerExecutionConfiguration;
import me.theyinspire.pandora.cli.error.ConfigurationException;
import me.theyinspire.pandora.core.config.ScopedOptionRegistry;
import me.theyinspire.pandora.core.config.impl.DefaultOptionRegistry;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.DataStoreConfiguration;
import me.theyinspire.pandora.core.datastore.DataStoreRegistry;
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

    public DefaultServerExecutionConfiguration(Map<String, String> data) {
        super(ExecutionMode.SERVER, data);
        this.protocols = Collections.unmodifiableList(deduceProtocols());
        this.dataStoreName = deduceDataStoreName();
        dataStoreConfiguration = new DefaultDataStoreConfiguration(dataStoreName);
        this.dataStore = deduceDataStore();
        configurations = new HashMap<>();
        for (Protocol protocol : protocols) {
            configurations.put(protocol, new DefaultServerConfiguration(protocol));
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

    private class DefaultServerConfiguration implements ServerConfiguration {

        private final Protocol protocol;

        private DefaultServerConfiguration(Protocol protocol) {
            this.protocol = protocol;
        }

        private String prefix(String key) {
            return protocol.getName() + "-" + key;
        }

        private String getDefault(String key) {
            final ScopedOptionRegistry registry = DefaultOptionRegistry.getInstance().getProtocolOptionRegistry(getProtocol());
            final String defaultValue = registry.getDefaultValue(key, null);
            if (defaultValue == null) {
                throw new ConfigurationException("Missing required argument: " + prefix(key));
            }
            return defaultValue;
        }

        @Override
        public String get(String key) {
            return DefaultServerExecutionConfiguration.this.get(prefix(key));
        }

        @Override
        public String require(String key) {
            return DefaultServerExecutionConfiguration.this.get(prefix(key), getDefault(key));
        }

        @Override
        public String get(String key, String defaultValue) {
            return DefaultServerExecutionConfiguration.this.get(prefix(key));
        }

        @Override
        public boolean has(String key) {
            return DefaultServerExecutionConfiguration.this.has(prefix(key));
        }

        @Override
        public Protocol getProtocol() {
            return protocol;
        }

        @Override
        public String getHost() {
            return require("host");
        }

        @Override
        public int getPort() {
            return Integer.parseInt(require("port"));
        }

        @Override
        public DataStore getDataStore() {
            return dataStore;
        }
    }

    private class DefaultDataStoreConfiguration implements DataStoreConfiguration {

        private final String dataStore;

        private DefaultDataStoreConfiguration(String dataStore) {
            this.dataStore = dataStore;
        }

        private String prefix(String key) {
            return "ds-" + dataStore + "-" + key;
        }

        private String getDefault(String key) {
            final ScopedOptionRegistry registry = DefaultOptionRegistry.getInstance().getDataStoreOptionRegistry(dataStore);
            final String defaultValue = registry.getDefaultValue(key, null);
            if (defaultValue == null) {
                throw new ConfigurationException("Missing required argument: " + prefix(key));
            }
            return defaultValue;
        }

        @Override
        public String get(String key) {
            return DefaultServerExecutionConfiguration.this.get(prefix(key));
        }

        @Override
        public String require(String key) {
            return DefaultServerExecutionConfiguration.this.get(prefix(key), getDefault(key));
        }

        @Override
        public String get(String key, String defaultValue) {
            return DefaultServerExecutionConfiguration.this.get(prefix(key));
        }

        @Override
        public boolean has(String key) {
            return DefaultServerExecutionConfiguration.this.has(prefix(key));
        }

    }

}
