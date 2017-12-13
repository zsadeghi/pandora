package me.theyinspire.pandora.replica.impl;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.UriClientConfigurationReader;
import me.theyinspire.pandora.core.client.impl.DefaultUriClientConfigurationReader;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.cmd.CommandSerializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandDeserializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandSerializer;
import me.theyinspire.pandora.core.datastore.cmd.LockingDataStoreCommands;
import me.theyinspire.pandora.core.datastore.cmd.SignatureCommand;
import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.replica.Replica;
import me.theyinspire.pandora.replica.ReplicaRegistryInitializer;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 8:11 PM)
 */
public class ConfigurationFileReplicaRegistry extends AbstractReplicaRegistry {

    private final Set<Client> clients;
    private final CommandSerializer serializer;
    private final CommandDeserializer deserializer;
    private final ConfigurationFileReader fileReader;

    public ConfigurationFileReplicaRegistry(File file, int refreshRate, final ReplicaRegistryInitializer initializer) {
        super(initializer);
        serializer = AggregateCommandSerializer.getInstance();
        deserializer = AggregateCommandDeserializer.getInstance();
        clients = new CopyOnWriteArraySet<>();
        fileReader = new ConfigurationFileReader(clients, file, refreshRate);
        fileReader.runOne();
        if (refreshRate > 0) {
            new Thread(fileReader).start();
        }
    }

    @Override
    public Set<Replica> getReplicaSet() {
        final SignatureCommand signatureCommand = LockingDataStoreCommands.signature();
        final Set<Replica> replicaSet = new HashSet<>();
        for (Client client : clients) {
            final String serializedCommand = serializer.serializeCommand(signatureCommand);
            final String result;
            try {
                result = client.send(serializedCommand);
            } catch (Exception e) {
                continue;
            }
            final String signature = (String) deserializer.deserializeResponse(signatureCommand, result);
            replicaSet.add(new ImmutableReplica(signature, client));
        }
        return replicaSet;
    }

    @Override
    public void destroy(String signature) {
        fileReader.stop();
    }

    private static class ConfigurationFileReader implements Runnable {

        private final AtomicBoolean running = new AtomicBoolean(true);
        private final Set<Client> clients;
        private final File file;
        private final int refreshRate;

        private ConfigurationFileReader(Set<Client> clients, File file, int refreshRate) {
            this.clients = clients;
            this.file = file;
            this.refreshRate = refreshRate;
        }

        @Override
        public void run() {
            while (running.get()) {
                runOne();
                if (refreshRate == 0) {
                    break;
                }
                try {
                    Thread.sleep(refreshRate);
                } catch (InterruptedException e) {
                    throw new ServerException("Interrupting while sleeping between file refreshes", e);
                }
            }
        }

        public void runOne() {
            final BufferedReader reader;
            final UriClientConfigurationReader configurationReader = new DefaultUriClientConfigurationReader();
            final Set<Client> clients = new HashSet<>();
            try {
                reader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                throw new ServerException("Failed to read configuration file", e);
            }
            try {
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
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    //noinspection ThrowFromFinallyBlock
                    throw new ServerException("Failed to close the file", e);
                }
            }
            this.clients.clear();
            this.clients.addAll(clients);
        }

        public void stop() {
            running.set(false);
        }

    }

}
