package me.theyinspire.pandora.rest.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.error.ClientException;
import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.cmd.CommandSerializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandDeserializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandSerializer;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommand;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommandDispatcher;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/11/17, 7:43 PM)
 */
public class RestClient implements Client {

    private final CommandDeserializer deserializer;
    private final CommandSerializer serializer;
    private final DataStoreCommandDispatcher dispatcher;
    private final DataStoreClient client;
    private final ClientConfiguration configuration;

    public RestClient(ClientConfiguration configuration, String host, int port, String prefix, ObjectMapper mapper) {
        this.configuration = configuration;
        String baseURl = prefix == null ? "" : prefix
                .replaceAll("/+", "/")
                .replaceFirst("^/", "")
                .replaceFirst("/$", "");
        deserializer = AggregateCommandDeserializer.getInstance();
        serializer = AggregateCommandSerializer.getInstance();
        client = new DataStoreClient("http://" + host + ":" + port + "/" + baseURl, mapper);
        dispatcher = new DataStoreCommandDispatcher(client);
    }

    @Override
    public ClientConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String send(String content) throws ClientException {
        if (content.trim().equals("exit")) {
            return client.exit();
        }
        final Command<?> command = deserializer.deserializeCommand(content, null);
        final Object result;
        if (command instanceof DataStoreCommand<?>) {
            try {
                result = dispatcher.dispatch((DataStoreCommand<?>) command);
            } catch (Exception e) {
                throw new ClientException("Failed to send the request to the server", e);
            }
        } else {
            result = CommandDeserializer.UNKNOWN;
        }
        return serializer.serializeResponse(command, result);
    }

}
