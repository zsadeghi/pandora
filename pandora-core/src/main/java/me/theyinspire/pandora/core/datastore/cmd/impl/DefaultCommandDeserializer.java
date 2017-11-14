package me.theyinspire.pandora.core.datastore.cmd.impl;

import me.theyinspire.pandora.core.datastore.cmd.*;
import me.theyinspire.pandora.core.str.DocumentReader;
import me.theyinspire.pandora.core.str.impl.DefaultDocumentReader;

import java.util.*;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 2:12 PM)
 */
public class DefaultCommandDeserializer implements CommandDeserializer {

    public static final String SIZE = "size";
    public static final String EMPTY = "empty";
    public static final String STORE = "store";
    public static final String PUT = "put";
    public static final String GET = "get";
    public static final String DEL = "del";
    public static final String KEYS = "keys";
    public static final String TRUNCATE = "truncate";
    public static final String HAS = "has";

    @Override
    public DataStoreCommand<?> deserializeCommand(String command) {
        if (command == null) {
            return null;
        }
        final DocumentReader reader = new DefaultDocumentReader(command);
        final String word = reader.expect("\\S+", true).toLowerCase();
        switch (word) {
            case SIZE:
                return Commands.size();
            case EMPTY:
                return Commands.isEmpty();
            case STORE:
                return Commands.all();
            case KEYS:
                return Commands.keys();
            case TRUNCATE:
                return Commands.truncate();
            case HAS:
                return Commands.has(reader.rest().trim());
            case GET:
                return Commands.get(reader.rest().trim());
            case DEL:
                return Commands.delete(reader.rest().trim());
            case PUT:
                final String key = reader.expect("\\S+", true).toLowerCase();
                return Commands.store(key, reader.rest().trim());
            default:
                throw new IllegalArgumentException("Invalid command: " + command);
        }
    }

    @Override
    public Object deserializeResponse(DataStoreCommand<?> command, String response) {
        if (command instanceof SizeCommand) {
            return Long.parseLong(response);
        } else if (command instanceof IsEmptyCommand) {
            return Boolean.parseBoolean(response);
        } else if (command instanceof StoreCommand) {
            return response.startsWith("put");
        } else if (command instanceof GetCommand) {
            if (!response.matches("get key=.*? val=.*?")) {
                throw new IllegalArgumentException("Poorly formatted response: " + response);
            }
            response = response.substring(response.indexOf("val=") + 4);
            return response;
        } else if (command instanceof DeleteCommand) {
            return response.startsWith("delete");
        } else if (command instanceof KeysCommand) {
            return response.isEmpty() ? Collections.emptySet() : new HashSet<>(Arrays.asList(response.split(",")));
        } else if (command instanceof TruncateCommand) {
            return Long.parseLong(response);
        } else if (command instanceof HasCommand) {
            return Boolean.parseBoolean(response);
        } else if (command instanceof AllCommand) {
            final Map<String, Object> map = new HashMap<>();
            final String[] lines = response.split("\n");
            for (String line : lines) {
                line = line.replaceFirst("^key:", "");
                line = line.substring(0, line.length() - 1);
                final String[] portions = line.split(":value:");
                map.put(portions[0], portions[1]);
            }
            return map;
        }
        throw new UnsupportedOperationException("Unknown command: " + command);
    }

}
