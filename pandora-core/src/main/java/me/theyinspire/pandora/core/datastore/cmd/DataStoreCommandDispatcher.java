package me.theyinspire.pandora.core.datastore.cmd;

import me.theyinspire.pandora.core.datastore.DataStore;

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
    public <R> R dispatch(DataStoreCommand<R> command) {
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
        }
        throw new UnsupportedOperationException("Unknown command: " + command);
    }

}
