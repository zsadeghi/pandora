package me.theyinspire.pandora.core.datastore.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandSerializer;
import me.theyinspire.pandora.core.datastore.cmd.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 1:44 PM)
 */
public class DataStoreCommandSerializer implements CommandSerializer {

    @Override
    public List<Class<? extends Command>> accepts() {
        return Arrays.asList(DataStoreCommand.class,
                             LockingDataStoreCommand.class);
    }

    @Override
    public String serializeCommand(Command<?> command) {
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
        } else if (command instanceof LockCommand) {
            return "lock " + ((LockCommand) command).getKey();
        } else if (command instanceof UnlockCommand) {
            return "unlock " + ((UnlockCommand) command).getKey() + " " + ((UnlockCommand) command).getLock();
        } else if (command instanceof IsLockedCommand) {
            return "locked " + ((IsLockedCommand) command).getKey();
        } else if (command instanceof RestoreCommand) {
            return "restore " + ((RestoreCommand) command).getKey() + " " + ((RestoreCommand) command).getLock();
        } else if (command instanceof GetUriCommand) {
            return "uri";
        } else if (command instanceof SignatureCommand) {
            return "signature";
        } else if (command instanceof LockedGetCommand) {
            return "xget " + ((LockedGetCommand) command).getKey() + " " + ((LockedGetCommand) command).getLock();
        } else if (command instanceof LockedDeleteCommand) {
            return "xdel " + ((LockedDeleteCommand) command).getKey() + " " + ((LockedDeleteCommand) command).getLock();
        } else if (command instanceof LockedStoreCommand) {
            return "xput " + ((LockedStoreCommand) command).getKey() + " " + ((LockedStoreCommand) command).getLock() + " " + ((LockedStoreCommand) command).getValue();
        } else if (command instanceof TestCommand) {
            return "test";
        }
        return null;
    }

    @Override
    public String serializeResponse(Command<?> command, Object response) {
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
                serialized = "error: could not delete the data from the store (" + ((DeleteCommand) command).getKey()
                        + ")";
            }
        } else if (command instanceof TestCommand) {
            return (String) response;
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
        } else if (command instanceof LockCommand) {
            serialized = "locked "  + ((LockCommand) command).getKey() + " " + response;
        } else if (command instanceof UnlockCommand) {
            serialized = "unlocked "  + ((UnlockCommand) command).getKey();
        } else if (command instanceof RestoreCommand) {
            serialized = "restored "  + ((RestoreCommand) command).getKey();
        } else if (command instanceof IsLockedCommand) {
            serialized = "locked "  + ((IsLockedCommand) command).getKey() + ": " + response;
        } else if (command instanceof GetUriCommand) {
            serialized = String.valueOf(response);
        } else if (command instanceof SignatureCommand) {
            serialized = String.valueOf(response);
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
                serialized = "error: could not delete the data from the store (" + ((LockedDeleteCommand) command).getKey()
                        + ")";
            }
        } else {
            throw new IllegalStateException();
        }
        return serialized.trim();
    }

}
