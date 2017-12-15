package me.theyinspire.pandora.core.datastore.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.datastore.cmd.*;
import me.theyinspire.pandora.core.server.ServerConfiguration;
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
    private static final String SIGNATURE = "signature";
    private static final String URI = "uri";
    private static final String TEST = "test";

    @Override
    public Command<?> deserializeCommand(String command, ServerConfiguration serverConfiguration) {
        if (command == null) {
            return null;
        }
        final DocumentReader reader = new DefaultDocumentReader(command);
        final String word = readWord(reader).toLowerCase();
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
            case URI:
                return LockingDataStoreCommands.getUri(serverConfiguration);
            case SIGNATURE:
                return LockingDataStoreCommands.signature();
            case GET:
                return DataStoreCommands.get(reader.rest().trim().split("\\s+")[0]);
            case DEL:
                return DataStoreCommands.delete(reader.rest().trim().split("\\s+")[0]);
            case PUT:
                return DataStoreCommands.store(readWord(reader), reader.rest().trim());
            case TEST:
                return DataStoreCommands.test();
        }
        return null;
    }

    private String readWord(DocumentReader reader) {
        return reader.expect("\\S+", true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R deserializeResponse(Command<R> command, String response) {
        if (command instanceof SizeCommand) {
            return (R) (Long) Long.parseLong(response);
        } else if (command instanceof IsEmptyCommand) {
            return (R) (Boolean) Boolean.parseBoolean(response);
        } else if (command instanceof StoreCommand) {
            return (R) (Boolean) response.startsWith("put");
        } else if (command instanceof GetCommand || command instanceof LockedGetCommand) {
            if (!response.matches("get key=.*? val=.*?")) {
                throw new IllegalArgumentException("Poorly formatted response: " + response);
            }
            response = response.substring(response.indexOf("val=") + 4);
            return (R) response;
        } else if (command instanceof DeleteCommand) {
            return (R) (Boolean) response.startsWith("delete");
        } else if (command instanceof KeysCommand) {
            return response.isEmpty() ? (R) Collections.emptySet() : (R) new HashSet<>(Arrays.asList(response.split(",")));
        } else if (command instanceof TruncateCommand) {
            return (R) (Long) Long.parseLong(response);
        } else if (command instanceof HasCommand) {
            return (R) (Boolean) Boolean.parseBoolean(response);
        } else if (command instanceof AllCommand) {
            final Map<String, Object> map = new HashMap<>();
            if (!response.isEmpty()) {
                final String[] lines = response.split("\n");
                for (String line : lines) {
                    line = line.replaceFirst("^key:", "");
                    line = line.substring(0, line.length() - 1);
                    final String[] portions = line.split(":value:");
                    map.put(portions[0], portions[1]);
                }
            }
            return (R) map;
        } else if (command instanceof TestCommand) {
            return (R) response;
        } else if (command instanceof GetUriCommand) {
            return (R) response;
        } else if (command instanceof SignatureCommand) {
            return (R) response;
        }
        throw new IllegalStateException();
    }

}
