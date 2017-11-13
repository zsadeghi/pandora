package me.theyinspire.pandora.rmi.client;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.error.ClientException;
import me.theyinspire.pandora.core.datastore.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.datastore.cmd.CommandSerializer;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommand;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommandDispatcher;
import me.theyinspire.pandora.core.datastore.cmd.impl.DefaultCommandDeserializer;
import me.theyinspire.pandora.core.datastore.cmd.impl.DefaultCommandSerializer;
import me.theyinspire.pandora.rmi.export.RmiDataStore;
import me.theyinspire.pandora.rmi.export.RmiDataStoreWrapper;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 2:45 PM)
 */
public class RmiClient implements Client {

    private final CommandSerializer serializer;
    private final CommandDeserializer deserializer;
    private final DataStoreCommandDispatcher dispatcher;
    private final RmiDataStore dataStore;

    public RmiClient(String host) {
        this(host, "dataStore");
    }

    public RmiClient(String host, String name) {
        final Registry registry;
        try {
            registry = LocateRegistry.getRegistry(host);
        } catch (RemoteException e) {
            throw new ClientException("Failed to acquire a link to the registry", e);
        }
        try {
            dataStore = ((RmiDataStore) registry.lookup(name));
        } catch (RemoteException e) {
            throw new ClientException("Failed to locate object", e);
        } catch (NotBoundException e) {
            throw new ClientException("Object not bound", e);
        }
        dispatcher = new DataStoreCommandDispatcher(new RmiDataStoreWrapper(dataStore));
        serializer = new DefaultCommandSerializer();
        deserializer = new DefaultCommandDeserializer();
    }

    @Override
    public String send(String content) throws ClientException {
        if (content.trim().equals("exit")) {
            return dataStore.exit();
        }
        final DataStoreCommand<?> command = deserializer.deserializeCommand(content);
        final Object result = dispatcher.dispatch(command);
        return serializer.serializeResponse(command, result);
    }

}
