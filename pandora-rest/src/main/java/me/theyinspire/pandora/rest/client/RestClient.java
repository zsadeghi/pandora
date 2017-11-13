package me.theyinspire.pandora.rest.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.error.ClientException;
import me.theyinspire.pandora.core.datastore.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.datastore.cmd.CommandSerializer;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommand;
import me.theyinspire.pandora.core.datastore.cmd.DataStoreCommandDispatcher;
import me.theyinspire.pandora.core.datastore.cmd.impl.DefaultCommandDeserializer;
import me.theyinspire.pandora.core.datastore.cmd.impl.DefaultCommandSerializer;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/11/17, 7:43 PM)
 */
public class RestClient implements Client {

    private final CommandDeserializer deserializer;
    private final CommandSerializer serializer;
    private final DataStoreCommandDispatcher dispatcher;
    private final DataStoreClient client;

    public RestClient(String host, int port, String prefix, ObjectMapper mapper) {
        String baseURl = prefix == null ? "" : prefix
                .replaceAll("/+", "/")
                .replaceFirst("^/", "")
                .replaceFirst("/$", "");
        deserializer = new DefaultCommandDeserializer();
        serializer = new DefaultCommandSerializer();
        client = new DataStoreClient("http://" + host + ":" + port + "/" + baseURl, mapper);
        dispatcher = new DataStoreCommandDispatcher(client);
    }

    @Override
    public String send(String content) throws ClientException {
        if (content.trim().equals("exit")) {
            return client.exit();
        }
        final DataStoreCommand<?> command = deserializer.deserializeCommand(content);
        final Object result;
        try {
            result = dispatcher.dispatch(command);
        } catch (Exception e) {
            throw new ClientException("Failed to send the request to the server", e);
        }
        return serializer.serializeResponse(command, result);
    }

}
