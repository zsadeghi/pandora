package me.theyinspire.pandora.dds.impl;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.cmd.CommandSerializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandDeserializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandSerializer;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommand;
import me.theyinspire.pandora.dds.Replica;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 8:04 PM)
 */
public class ImmutableReplica implements Replica {

    private final String signature;
    private final Client client;
    private final CommandDeserializer deserializer;
    private final CommandSerializer serializer;

    public ImmutableReplica(String signature, Client client) {
        this.signature = signature;
        this.client = client;
        deserializer = AggregateCommandDeserializer.getInstance();
        serializer = AggregateCommandSerializer.getInstance();
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public <R> R send(DataStoreCommand<R> command) {
        final String serializedCommand = serializer.serializeCommand(command);
        final String response = client.send(serializedCommand);
        final Object result = deserializer.deserializeResponse(command, response);
        //noinspection unchecked
        return (R) result;
    }

}
