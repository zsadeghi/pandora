package me.theyinspire.pandora.dds.impl;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientConfiguration;
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
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 8:11 PM)
 */
public class ConfigurationFileReplicaRegistry extends AbstractReplicaRegistry {

    private Set<Replica> replicaSet;

    public ConfigurationFileReplicaRegistry(File file) {
        replicaSet = new CopyOnWriteArraySet<>();
        final CommandSerializer serializer = AggregateCommandSerializer.getInstance();
        final CommandDeserializer deserializer = AggregateCommandDeserializer.getInstance();
        final BufferedReader reader;
        final DefaultUriClientConfigurationReader configurationReader = new DefaultUriClientConfigurationReader();
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new ServerException("Failed to read configuration file", e);
        }
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
            final SignatureCommand signatureCommand = LockingDataStoreCommands.signature();
            final String result = client.send(serializer.serializeCommand(signatureCommand));
            final String signature = (String) deserializer.deserializeResponse(signatureCommand, result);
            replicaSet.add(new ImmutableReplica(signature, client));
        }
    }

    @Override
    protected Set<Replica> getReplicaSet() {
        return replicaSet;
    }

}
