package me.theyinspire.pandora.core.datastore.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandSerializer;
import me.theyinspire.pandora.core.datastore.cmd.*;

import java.util.Collections;
import java.util.List;

public class LockingDataStoreCommandSerializer implements CommandSerializer {

    @Override
    public List<Class<? extends Command>> accepts() {
        return Collections.singletonList(LockingDataStoreCommand.class);
    }

    @Override
    public String serializeCommand(Command<?> command) {
        if (command instanceof LockCommand) {
            return "lock " + ((LockCommand) command).getKey();
        } else if (command instanceof UnlockCommand) {
            return "unlock " + ((UnlockCommand) command).getKey() + " " + ((UnlockCommand) command).getLock();
        } else if (command instanceof IsLockedCommand) {
            return "locked " + ((IsLockedCommand) command).getKey();
        } else if (command instanceof RestoreCommand) {
            return "restore " + ((RestoreCommand) command).getKey() + " " + ((RestoreCommand) command).getLock();
        } else if (command instanceof LockedGetCommand) {
            return "xget " + ((LockedGetCommand) command).getKey() + " " + ((LockedGetCommand) command).getLock();
        } else if (command instanceof LockedDeleteCommand) {
            return "xdel " + ((LockedDeleteCommand) command).getKey() + " " + ((LockedDeleteCommand) command).getLock();
        } else if (command instanceof LockedStoreCommand) {
            return "xput " + ((LockedStoreCommand) command).getKey() + " " + ((LockedStoreCommand) command).getLock() + " " + ((LockedStoreCommand) command).getValue();
        }
        return null;
    }

    @Override
    public String serializeResponse(Command<?> command, Object response) {
        final String serialized;
        if (command instanceof LockCommand) {
            serialized = "locked "  + ((LockCommand) command).getKey() + " " + response;
        } else if (command instanceof UnlockCommand) {
            serialized = "unlocked "  + ((UnlockCommand) command).getKey();
        } else if (command instanceof RestoreCommand) {
            serialized = "restored "  + ((RestoreCommand) command).getKey();
        } else if (command instanceof IsLockedCommand) {
            serialized = "locked "  + ((IsLockedCommand) command).getKey() + ": " + response;
        } else if (command instanceof LockedStoreCommand) {
            if (Boolean.TRUE.equals(response)) {
                serialized = String.format("put key=%s", ((LockedStoreCommand) command).getKey());
            } else {
                serialized = "error: could not put data into the store";
            }
        } else if (command instanceof LockedGetCommand) {
            serialized = String.format("get key=%s val=%s", ((LockedGetCommand) command).getKey(), String.valueOf(response));
        } else if (command instanceof LockedDeleteCommand) {
            if (Boolean.TRUE.equals(response)) {
                serialized = String.format("delete key=%s", ((LockedDeleteCommand) command).getKey());
            } else {
                serialized = "error: could not delete the data from the store (" + ((LockedDeleteCommand) command).getKey() + ")";
            }
        } else {
            throw new IllegalStateException();
        }
        return serialized.trim();
    }

}
