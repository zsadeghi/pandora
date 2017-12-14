package me.theyinspire.pandora.core.server.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.cmd.CommandSerializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandDeserializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandSerializer;
import me.theyinspire.pandora.core.cmd.impl.DefaultErrorSerializer;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommandDispatcher;
import me.theyinspire.pandora.core.error.CommunicationException;
import me.theyinspire.pandora.core.protocol.Protocol;
import me.theyinspire.pandora.core.server.*;
import me.theyinspire.pandora.core.server.error.ServerException;
import org.apache.commons.logging.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 3:48 PM)
 */
public abstract class AbstractServer<P extends Protocol, I extends Incoming, O extends Outgoing, T extends ServerTransaction<I, O>, S extends ServerSession<P, T>> implements Server {

    @SuppressWarnings("WeakerAccess")
    public static final int BACKLOG = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executor;
    private final DataStoreCommandDispatcher dispatcher;
    private final CommandSerializer serializer;
    private final CommandDeserializer deserializer;
    private final ServerConfiguration configuration;
    private boolean running;

    public AbstractServer(ServerConfiguration configuration, DataStore dataStore) {
        this.configuration = configuration;
        executor = Executors.newFixedThreadPool(BACKLOG);
        dispatcher = new DataStoreCommandDispatcher(dataStore);
        running = false;
        serializer = AggregateCommandSerializer.getInstance();
        deserializer = AggregateCommandDeserializer.getInstance();
    }

    @Override
    public ServerConfiguration getConfiguration() {
        return configuration;
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
            final I received;
            try {
                received = transaction.receive();
            } catch (CommunicationException e) {
                getLog().error(e);
                transaction.send(compose(transaction.empty(), "Error while reading input"));
                transaction.close();
                continue;
            }
            if ("exit".equalsIgnoreCase(received.getContent())) {
                getLog().info("Exit sequence started.");
                transaction.send(compose(received, "bye"));
                transaction.close();
                stop();
                continue;
            }
            final Command<?> command = deserializeCommand(received.getContent());
            getLog().debug("Scheduling command " + command);
            executor.submit(() -> {
                String serialized;
                try {
                    getLog().info("Executing command: " + command);
                    final Object result = dispatcher.dispatch(command);
                    serialized = serializeResponse(command, result);
                } catch (Exception e) {
                    e.printStackTrace();
                    serialized = "error occurred: " + DefaultErrorSerializer.getInstance().serialize(e);
                }
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
            return;
        }
        getLog().info("Shutting down the server.");
        running = false;
        executor.shutdown();
        onAfterStop();
    }

    private Command<?> deserializeCommand(String command) {
        try {
            return deserializer.deserializeCommand(command, configuration);
        } catch (Exception e) {
            return null;
        }
    }

    private String serializeResponse(Command<?> command, Object reply) {
        return serializer.serializeResponse(command, reply);
    }

    protected abstract S setUp();

    protected abstract O compose(I received, String serialized);

    protected abstract Log getLog();

}
