package me.theyinspire.pandora.core.server.impl;

import me.theyinspire.pandora.core.protocol.Protocol;
import me.theyinspire.pandora.core.server.*;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommand;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommandDispatcher;
import me.theyinspire.pandora.core.datastore.cmd.impl.DefaultCommandDeserializer;
import me.theyinspire.pandora.core.datastore.cmd.impl.DefaultCommandSerializer;
import me.theyinspire.pandora.core.datastore.impl.SynchronizedDataStore;
import me.theyinspire.pandora.core.server.error.ServerException;
import org.apache.commons.logging.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 3:48 PM)
 */
public abstract class AbstractServer<P extends Protocol, I extends Incoming, O extends Outgoing, T extends ServerTransaction<I, O>, S extends ServerSession<P, T>> implements Server {

    public static final int BACKLOG = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executor;
    private final DataStoreCommandDispatcher dispatcher;
    private final DefaultCommandSerializer serializer;
    private final DefaultCommandDeserializer deserializer;
    private boolean running;

    public AbstractServer(DataStore dataStore) {
        executor = Executors.newFixedThreadPool(BACKLOG);
        dispatcher = new DataStoreCommandDispatcher(dataStore);
        running = false;
        serializer = new DefaultCommandSerializer();
        deserializer = new DefaultCommandDeserializer();
    }

    @Override
    public void start() throws ServerException {
        if (running) {
            throw new ServerException("Server already running");
        }
        getLog().info("Starting server execution loop");
        final S session = setUp();
        running = true;
        while (running) {
            getLog().info("Waiting for a transaction");
            final T transaction = session.startTransaction();
            getLog().debug("Transaction started. Receiving data.");
            final I received = transaction.receive();
            if ("exit".equalsIgnoreCase(received.getContent())) {
                getLog().info("Exit sequence started.");
                transaction.send(compose(received, "bye"));
                transaction.close();
                stop();
                continue;
            }
            final DataStoreCommand<?> command = deserializeCommand(received.getContent());
            getLog().debug("Scheduling command " + command);
            executor.submit(() -> {
                getLog().info("Executing command: " + command);
                final Object result = dispatcher.dispatch(command);
                final String serialized = serializeResponse(command, result);
                O reply = compose(received, serialized);
                getLog().debug("Responding to the query.");
                transaction.send(reply);
                transaction.close();
                getLog().debug("Transaction closed");
            });
        }
    }

    @Override
    public void stop() {
        if (!running) {
            throw new ServerException("Server is not running");
        }
        getLog().info("Shutting down the server.");
        running = false;
        executor.shutdown();
    }

    private DataStoreCommand<?> deserializeCommand(String command) {
        return deserializer.deserializeCommand(command);
    }

    private String serializeResponse(DataStoreCommand<?> command, Object reply) {
        return serializer.serializeResponse(command, reply);
    }

    protected abstract S setUp();

    protected abstract O compose(I received, String serialized);

    protected abstract Log getLog();

}
