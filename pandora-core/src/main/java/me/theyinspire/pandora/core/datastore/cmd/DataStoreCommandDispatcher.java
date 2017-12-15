package me.theyinspire.pandora.core.datastore.cmd;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.datastore.CommandReceiver;
import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.datastore.LockingDataStore;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:31 PM)
 */
public class DataStoreCommandDispatcher {

    private final DataStore dataStore;

    public DataStoreCommandDispatcher(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @SuppressWarnings("unchecked")
    public <R> R dispatch(Command<R> command) {
        if (command instanceof SizeCommand) {
            return (R) (Long) dataStore.size();
        } else if (command instanceof IsEmptyCommand) {
            return (R) (Boolean) dataStore.isEmpty();
        } else if (command instanceof StoreCommand) {
            StoreCommand storeCommand = (StoreCommand) command;
            return (R) (Boolean) dataStore.store(storeCommand.getKey(), storeCommand.getValue());
        } else if (command instanceof GetCommand) {
            GetCommand getCommand = (GetCommand) command;
            return (R) dataStore.get(getCommand.getKey());
        } else if (command instanceof DeleteCommand) {
            DeleteCommand deleteCommand = (DeleteCommand) command;
            return (R) (Boolean) dataStore.delete(deleteCommand.getKey());
        } else if (command instanceof KeysCommand) {
            return (R) dataStore.keys();
        } else if (command instanceof TruncateCommand) {
            return (R) (Long) dataStore.truncate();
        } else if (command instanceof HasCommand) {
            HasCommand hasCommand = (HasCommand) command;
            return (R) (Boolean) dataStore.has(hasCommand.getKey());
        } else if (command instanceof AllCommand) {
            return (R) dataStore.all();
        } else if (command instanceof GetUriCommand) {
            return (R) dataStore.getUri(((GetUriCommand) command).getServerConfiguration());
        } else if (command instanceof SignatureCommand) {
            return (R) dataStore.getSignature();
        } else if (command instanceof LockingDataStoreCommand<?> && dataStore instanceof LockingDataStore) {
            if (command instanceof LockCommand) {
                return (R) ((LockingDataStore) dataStore).lock(((LockCommand) command).getKey());
            } else if (command instanceof UnlockCommand) {
                ((LockingDataStore) dataStore).unlock(((UnlockCommand) command).getKey(), ((UnlockCommand) command).getLock());
                return null;
            } else if (command instanceof RestoreCommand) {
                ((LockingDataStore) dataStore).restore(((RestoreCommand) command).getKey(), ((RestoreCommand) command).getLock());
                return null;
            } else if (command instanceof IsLockedCommand) {
                return (R) (Boolean) ((LockingDataStore) dataStore).locked(((IsLockedCommand) command).getKey());
            } else if (command instanceof LockedStoreCommand) {
                return (R) (Boolean) ((LockingDataStore) dataStore).store(((LockedStoreCommand) command).getKey(), ((LockedStoreCommand) command).getValue(), ((LockedStoreCommand) command).getLock());
            } else if (command instanceof LockedDeleteCommand) {
                return (R) (Boolean) ((LockingDataStore) dataStore).delete(((LockedDeleteCommand) command).getKey(), ((LockedDeleteCommand) command).getLock());
            } else if (command instanceof LockedGetCommand) {
                return (R) ((LockingDataStore) dataStore).get(((LockedGetCommand) command).getKey(), ((LockedGetCommand) command).getLock());
            }
        }
        if (dataStore instanceof CommandReceiver) {
            return ((CommandReceiver) dataStore).receive(command);
        }
        throw new UnsupportedOperationException("Unknown command: " + command);
    }

}
