package me.theyinspire.pandora.core.datastore.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.datastore.cmd.*;
import me.theyinspire.pandora.core.str.DocumentReader;
import me.theyinspire.pandora.core.str.impl.DefaultDocumentReader;

import java.util.*;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 2:12 PM)
 */
public class DataStoreCommandDeserializer implements CommandDeserializer {

    private static final String SIZE = "size";
    private static final String EMPTY = "empty";
    private static final String STORE = "store";
    private static final String PUT = "put";
    private static final String GET = "get";
    private static final String DEL = "del";
    private static final String KEYS = "keys";
    private static final String TRUNCATE = "truncate";
    private static final String HAS = "has";

    @Override
    public Command<?> deserializeCommand(String command) {
        if (command == null) {
            return null;
        }
        final DocumentReader reader = new DefaultDocumentReader(command);
        final String word = reader.expect("\\S+", true).toLowerCase();
        switch (word) {
            case SIZE:
                return DataStoreCommands.size();
            case EMPTY:
                return DataStoreCommands.isEmpty();
            case STORE:
                return DataStoreCommands.all();
            case KEYS:
                return DataStoreCommands.keys();
            case TRUNCATE:
                return DataStoreCommands.truncate();
            case HAS:
                return DataStoreCommands.has(reader.rest().trim());
            case GET:
                return DataStoreCommands.get(reader.rest().trim());
            case DEL:
                return DataStoreCommands.delete(reader.rest().trim());
            case PUT:
                final String key = reader.expect("\\S+", true).toLowerCase();
                return DataStoreCommands.store(key, reader.rest().trim());
        }
        return null;
    }

    @Override
    public Object deserializeResponse(Command<?> command, String response) {
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
        return UNKNOWN;
    }

}
