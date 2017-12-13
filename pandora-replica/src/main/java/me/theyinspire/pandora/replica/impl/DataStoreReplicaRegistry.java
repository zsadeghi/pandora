package me.theyinspire.pandora.replica.impl;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.UriClientConfigurationReader;
import me.theyinspire.pandora.core.client.impl.DefaultUriClientConfigurationReader;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.cmd.CommandSerializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandDeserializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandSerializer;
import me.theyinspire.pandora.core.datastore.cmd.AllCommand;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommands;
import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;
import me.theyinspire.pandora.replica.Replica;
import me.theyinspire.pandora.replica.ReplicaRegistryInitializer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 10:48 PM)
 */
public class DataStoreReplicaRegistry extends AbstractReplicaRegistry {

    private final Client client;
    private final CommandSerializer serializer;
    private final CommandDeserializer deserializer;
    private final UriClientConfigurationReader configurationReader;

    public DataStoreReplicaRegistry(String registryUri, final ReplicaRegistryInitializer initializer) {
        super(initializer);
        configurationReader = new DefaultUriClientConfigurationReader();
        final ClientConfiguration clientConfiguration = configurationReader.read(registryUri);
        this.client = DefaultProtocolRegistry.getInstance().getClient(clientConfiguration.getProtocol(), clientConfiguration);
        serializer = AggregateCommandSerializer.getInstance();
        deserializer = AggregateCommandDeserializer.getInstance();
    }

    @Override
    public Set<Replica> getReplicaSet() {
        final AllCommand allCommand = DataStoreCommands.all();
        final String response = client.send(serializer.serializeCommand(allCommand));
        final Map<String, Serializable> nodes = deserializer.deserializeResponse(allCommand, response);
        final Set<Replica> replicaSet = new HashSet<>();
        for (Map.Entry<String, Serializable> entry : nodes.entrySet()) {
            final ClientConfiguration clientConfiguration = configurationReader.read((String) entry.getValue());
            final Client client = DefaultProtocolRegistry.getInstance().getClient(clientConfiguration.getProtocol(), clientConfiguration);
            replicaSet.add(new ImmutableReplica(entry.getKey(), client));
        }
        return replicaSet;
    }

    @Override
    protected void onBeforeInit(String signature, String uri) {
        client.send(serializer.serializeCommand(DataStoreCommands.store(signature, uri)));
    }

    @Override
    public void destroy(String signature) {
        // This will remove this node as an available replica once the server has shutdown
        client.send(serializer.serializeCommand(DataStoreCommands.delete(signature)));
    }

}
