package me.theyinspire.pandora.core.datastore.cmd.impl;

import me.theyinspire.pandora.core.datastore.cmd.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 1:44 PM)
 */
public class DefaultCommandSerializer implements CommandSerializer {

    @Override
    public String serializeCommand(DataStoreCommand<?> command) {
        if (command instanceof SizeCommand) {
            return "size";
        } else if (command instanceof IsEmptyCommand) {
            return "empty";
        } else if (command instanceof StoreCommand) {
            return "put " + ((StoreCommand) command).getKey() + " " + ((StoreCommand) command).getValue();
        } else if (command instanceof GetCommand) {
            return "get " + ((GetCommand) command).getKey();
        } else if (command instanceof DeleteCommand) {
            return "del " + ((DeleteCommand) command).getKey();
        } else if (command instanceof KeysCommand) {
            return "keys";
        } else if (command instanceof TruncateCommand) {
            return "truncate";
        } else if (command instanceof HasCommand) {
            return "has " + ((HasCommand) command).getKey();
        } else if (command instanceof AllCommand) {
            return "store";
        }
        throw new UnsupportedOperationException("Unknown command: " + command);
    }

    @Override
    public String serializeResponse(DataStoreCommand<?> command, Object response) {
        final String serialized;
        if (command instanceof SizeCommand) {
            serialized = String.valueOf(response);
        } else if (command instanceof IsEmptyCommand) {
            serialized = String.valueOf(response);
        } else if (command instanceof StoreCommand) {
            if (Boolean.TRUE.equals(response)) {
                serialized = String.format("put key=%s", ((StoreCommand) command).getKey());
            } else {
                serialized = "error: could not put data into the store";
            }
        } else if (command instanceof GetCommand) {
            serialized = String.format("get key=%s val=%s", ((GetCommand) command).getKey(), String.valueOf(response));
        } else if (command instanceof DeleteCommand) {
            if (Boolean.TRUE.equals(response)) {
                serialized = String.format("delete key=%s", ((DeleteCommand) command).getKey());
            } else {
                serialized = "error: could not delete the data from the store";
            }
        } else if (command instanceof AllCommand) {
            final StringBuilder builder = new StringBuilder();
            //noinspection unchecked
            final Map<String, Serializable> map = (Map<String, Serializable>) response;
            for (Map.Entry<String, Serializable> entry : map.entrySet()) {
                builder.append("key:");
                builder.append(entry.getKey());
                builder.append(":value:");
                builder.append(entry.getValue());
                builder.append(":\n");
            }
            serialized = builder.toString();
        } else if (command instanceof TruncateCommand) {
            serialized = String.valueOf(response);
        } else if (command instanceof KeysCommand) {
            //noinspection unchecked
            serialized = ((Set<String>) response).stream().reduce((a, b) -> a + "," + b).orElse("");
        } else if (command instanceof HasCommand) {
            serialized = String.valueOf(response);
        } else {
            throw new IllegalStateException();
        }
        return serialized.trim();
    }

}
