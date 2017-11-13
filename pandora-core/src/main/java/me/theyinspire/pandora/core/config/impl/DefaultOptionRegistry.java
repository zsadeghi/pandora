package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.config.Option;
import me.theyinspire.pandora.core.config.OptionRegistry;
import me.theyinspire.pandora.core.config.ProtocolOptionRegistry;
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

    private final Map<String, List<Option>> options;

    private DefaultOptionRegistry() {
        options = new HashMap<>();
    }

    @Override
    public List<Option> getOptions(Protocol protocol) {
        return Collections.unmodifiableList(options.get(protocol.getName()));
    }

    @Override
    public ProtocolOptionRegistry getProtocolOptionRegistry(Protocol protocol) {
        return new DefaultProtocolOptionRegistry(protocol);
    }

    private class DefaultProtocolOptionRegistry implements ProtocolOptionRegistry {

        private final Protocol protocol;

        private DefaultProtocolOptionRegistry(Protocol protocol) {
            this.protocol = protocol;
        }

        @Override
        public void register(String name, String description, String defaultValue) {
            final List<Option> list;
            if (options.containsKey(protocol.getName())) {
                list = DefaultOptionRegistry.this.options.get(protocol.getName());
            } else {
                list = new ArrayList<>();
            }
            list.add(new ImmutableOption(name, description, defaultValue, protocol));
            options.put(protocol.getName(), list);
        }

        @Override
        public void register(String name, String description) {
            register(name, description, null);
        }

        @Override
        public String getDefaultValue(String name, String fallback) {
            if (!options.containsKey(protocol.getName())) {
                return fallback;
            }
            return options.get(protocol.getName()).stream()
                    .filter(option -> option.getName().equals(name))
                    .map(Option::getDefaultValue)
                    .findFirst()
                    .orElse(fallback);
        }

        @Override
        public String getDefaultValue(String name) {
            return getDefaultValue(name, null);
        }

    }

}
