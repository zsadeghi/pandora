package me.theyinspire.pandora.dds.impl;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.error.ClientException;
import me.theyinspire.pandora.core.client.impl.DefaultUriClientConfigurationReader;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.cmd.CommandSerializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandDeserializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandSerializer;
import me.theyinspire.pandora.core.datastore.cmd.LockingDataStoreCommands;
import me.theyinspire.pandora.core.datastore.cmd.SignatureCommand;
import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.dds.Replica;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 8:11 PM)
 */
public class ConfigurationFileReplicaRegistry extends AbstractReplicaRegistry {

    private final Set<Client> clients;
    private final CommandSerializer serializer;
    private final CommandDeserializer deserializer;

    public ConfigurationFileReplicaRegistry(File file) {
        serializer = AggregateCommandSerializer.getInstance();
        deserializer = AggregateCommandDeserializer.getInstance();
        final BufferedReader reader;
        final DefaultUriClientConfigurationReader configurationReader = new DefaultUriClientConfigurationReader();
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new ServerException("Failed to read configuration file", e);
        }
        clients = new HashSet<>();
        while (true) {
            final String line;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                throw new ServerException("Failed to read file contents", e);
            }
            if (line == null) {
                break;
            }
            final ClientConfiguration clientConfiguration = configurationReader.read(line);
            final Client client = DefaultProtocolRegistry.getInstance().getClient(clientConfiguration.getProtocol(), clientConfiguration);
            clients.add(client);
        }
    }

    @Override
    protected Set<Replica> getReplicaSet() {
        final SignatureCommand signatureCommand = LockingDataStoreCommands.signature();
        final Set<Replica> replicaSet = new HashSet<>();
        for (Client client : clients) {
            final String serializedCommand = serializer.serializeCommand(signatureCommand);
            final String result;
            try {
                result = client.send(serializedCommand);
            } catch (ClientException e) {
                continue;
            }
            final String signature = (String) deserializer.deserializeResponse(signatureCommand, result);
            replicaSet.add(new ImmutableReplica(signature, client));
        }
        return replicaSet;
    }

}
