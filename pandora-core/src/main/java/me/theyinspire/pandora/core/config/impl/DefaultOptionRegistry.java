package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.config.*;
import me.theyinspire.pandora.core.protocol.Protocol;

import java.util.*;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 2:23 AM)
 */
public class DefaultOptionRegistry implements OptionRegistry {

    private static final OptionRegistry INSTANCE = new DefaultOptionRegistry();

    public static OptionRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<String, List<ProtocolOption>> protocolOptions;
    private final Map<String, List<DataStoreOption>> dataStoreOptions;
    private final Map<String, Protocol> protocols;

    private DefaultOptionRegistry() {
        protocolOptions = new HashMap<>();
        protocols = new HashMap<>();
        dataStoreOptions = new HashMap<>();
    }

    @Override
    public List<ProtocolOption> getProtocolOptions(Protocol protocol) {
        if (!protocolOptions.containsKey(protocol.getName())) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(protocolOptions.get(protocol.getName()));
    }

    @Override
    public List<DataStoreOption> getDataStoreOptions(String dataStore) {
        if (!dataStoreOptions.containsKey(dataStore)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(dataStoreOptions.get(dataStore));
    }

    @Override
    public ScopedOptionRegistry getProtocolOptionRegistry(Protocol protocol) {
        if (!protocols.containsKey(protocol.getName())) {
            protocols.put(protocol.getName(), protocol);
        }
        return new ProtocolOptionRegistry(protocol.getName(), protocolOptions);
    }

    @Override
    public ScopedOptionRegistry getDataStoreOptionRegistry(String dataStore) {
        return new DataStoreOptionRegistry(dataStore, dataStoreOptions);
    }

    private static abstract class AbstractScopedOptionRegistry implements ScopedOptionRegistry {

        private final String scope;
        private final Map<String, ?> options;

        private AbstractScopedOptionRegistry(String scope, Map<String, ?> options) {
            this.scope = scope;
            this.options = options;
        }

        String getScope() {
            return scope;
        }

        @Override
        public void register(String name, String description) {
            register(name, description, null);
        }

        @Override
        public String getDefaultValue(String name, String fallback) {
            if (!options.containsKey(scope)) {
                return fallback;
            }
            return getOptions(scope).stream()
                    .filter(option -> option.getName().equals(name))
                    .map(Option::getDefaultValue)
                    .findFirst()
                    .orElse(fallback);
        }

        private List<? extends Option> getOptions(String scope) {
            //noinspection unchecked
            return (List<? extends Option>) options.get(scope);
        }

        @Override
        public String getDefaultValue(String name) {
            return getDefaultValue(name, null);
        }

    }

    private class ProtocolOptionRegistry extends AbstractScopedOptionRegistry {

        private ProtocolOptionRegistry(String scope, Map<String, ?> options) {
            super(scope, options);
        }

        @Override
        public void register(String name, String description, String defaultValue) {
            final List<ProtocolOption> list;
            if (protocolOptions.containsKey(getScope())) {
                list = DefaultOptionRegistry.this.protocolOptions.get(getScope());
            } else {
                list = new ArrayList<>();
            }
            list.add(new ImmutableProtocolOption(name, description, defaultValue, protocols.get(getScope())));
            protocolOptions.put(getScope(), list);
        }

    }

    private class DataStoreOptionRegistry extends AbstractScopedOptionRegistry {

        private DataStoreOptionRegistry(String scope, Map<String, ?> options) {
            super(scope, options);
        }

        @Override
        public void register(String name, String description, String defaultValue) {
            final List<DataStoreOption> list;
            if (dataStoreOptions.containsKey(getScope())) {
                list = DefaultOptionRegistry.this.dataStoreOptions.get(getScope());
            } else {
                list = new ArrayList<>();
            }
            list.add(new ImmutableDataStoreOption(name, description, defaultValue, getScope()));
            dataStoreOptions.put(getScope(), list);
        }

    }

}
