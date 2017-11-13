package me.theyinspire.pandora.core.protocol.impl;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.ClientFactory;
import me.theyinspire.pandora.core.config.ProtocolOptionRegistry;
import me.theyinspire.pandora.core.config.impl.DefaultOptionRegistry;
import me.theyinspire.pandora.core.protocol.Protocol;
import me.theyinspire.pandora.core.protocol.ProtocolRegistry;
import me.theyinspire.pandora.core.server.Server;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.ServerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:24 PM)
 */
public class DefaultProtocolRegistry implements ProtocolRegistry {

    private static final ProtocolRegistry INSTANCE = new DefaultProtocolRegistry();

    public static ProtocolRegistry getInstance() {
        return INSTANCE;
    }

    private final List<Protocol> knownProtocols;
    private final Map<Protocol, ClientFactory> clientFactories;
    private final Map<Protocol, ServerFactory> serverFactories;

    private DefaultProtocolRegistry() {
        knownProtocols = new ArrayList<>();
        clientFactories = new HashMap<>();
        serverFactories = new HashMap<>();
    }

    @Override
    public void register(Protocol protocol, ClientFactory clientFactory, ServerFactory serverFactory) {
        if (knownProtocols.stream().map(Protocol::getName).collect(Collectors.toList()).contains(protocol.getName())) {
            throw new IllegalArgumentException("Protocol already registered: " + protocol.getName());
        }
        knownProtocols.add(protocol);
        clientFactories.put(protocol, clientFactory);
        serverFactories.put(protocol, serverFactory);
        final ProtocolOptionRegistry optionRegistry = DefaultOptionRegistry.getInstance().getProtocolOptionRegistry(protocol);
        protocol.defineOptions(optionRegistry);
    }

    @Override
    public Server getServer(Protocol protocol, ServerConfiguration configuration) {
        return serverFactories.get(protocol).getInstance(configuration);
    }

    @Override
    public Client getClient(Protocol protocol, ClientConfiguration configuration) {
        return clientFactories.get(protocol).getInstance(configuration);
    }

    @Override
    public Protocol getProtocolByName(String protocol) {
        for (Protocol p : clientFactories.keySet()) {
            if (p.getName().equalsIgnoreCase(protocol)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unknown protocol: " + protocol);
    }

    @Override
    public List<Protocol> getKnownProtocols() {
        return Collections.unmodifiableList(knownProtocols);
    }

}
