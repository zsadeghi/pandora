package me.theyinspire.pandora.core.datastore.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandDeserializer;
import me.theyinspire.pandora.core.datastore.cmd.*;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.str.DocumentReader;
import me.theyinspire.pandora.core.str.impl.DefaultDocumentReader;

public class LockingDataStoreCommandDeserializer implements CommandDeserializer {

    private static final String XPUT = "xput";
    private static final String XGET = "xget";
    private static final String XDEL = "xdel";
    private static final String KEYS = "keys";
    private static final String LOCK = "lock";
    private static final String UNLOCK = "unlock";
    private static final String RESTORE = "restore";
    private static final String LOCKED = "locked";

    @Override
    public Command<?> deserializeCommand(String command, ServerConfiguration serverConfiguration) {
        if (command == null) {
            return null;
        }
        final DocumentReader reader = new DefaultDocumentReader(command);
        final String word = readWord(reader).toLowerCase();
        switch (word) {
            case LOCK:
                return LockingDataStoreCommands.lock(reader.rest().trim());
            case UNLOCK:
                return LockingDataStoreCommands.unlock(readWord(reader), reader.rest().trim());
            case RESTORE:
                return LockingDataStoreCommands.restore(readWord(reader), reader.rest().trim());
            case LOCKED:
                return LockingDataStoreCommands.isLocked(reader.rest().trim());
            case XGET:
                return LockingDataStoreCommands.get(readWord(reader), reader.rest().trim());
            case XDEL:
                return LockingDataStoreCommands.delete(readWord(reader), reader.rest().trim().split("\\s+")[0]);
            case XPUT:
                return LockingDataStoreCommands.store(readWord(reader), readWord(reader), reader.rest().trim());
        }
        return null;
    }

    private String readWord(DocumentReader reader) {
        return reader.expect("\\S+", true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R deserializeResponse(Command<R> command, String response) {
        if (command instanceof LockingDataStoreCommand<?>) {
            if (command instanceof IsLockedCommand) {
                return (R) (Boolean) (response.startsWith("locked ") && Boolean.parseBoolean(response.substring(response.lastIndexOf(':') + 2)));
            } else if (command instanceof LockedStoreCommand) {
                return (R) (Boolean) response.startsWith("put");
            } else if (command instanceof LockedDeleteCommand) {
                return (R) (Boolean) response.startsWith("delete");
            } else if (command instanceof LockCommand) {
                return (R) response.substring(response.lastIndexOf(' ') + 1);
            } else {
                return null;
            }
        } else if (command instanceof TestCommand) {
            return (R) response;
        }
        throw new IllegalStateException();
    }
}
