package me.theyinspire.pandora.core.datastore.cmd;

import me.theyinspire.pandora.core.datastore.mock.DataStoreOperations;
import me.theyinspire.pandora.core.datastore.mock.MockDataStore;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:29 PM)
 */
public class DataStoreCommandDispatcherTest {

    private DataStoreCommandDispatcher dispatcher;
    private MockDataStore dataStore;

    @BeforeMethod
    public void setUp() throws Exception {
        dataStore = new MockDataStore();
        dispatcher = new DataStoreCommandDispatcher(dataStore);
    }

    @Test
    public void testSize() throws Exception {
        SizeCommand command = Commands.size();
        dispatcher.dispatch(command);
        assertThat(dataStore.getOperations(), contains(DataStoreOperations.SIZE));
        assertThat(dataStore.getArguments(), is(Collections.singletonList(Collections.emptyList())));
    }

    @Test
    public void testIsEmpty() throws Exception {
        IsEmptyCommand command = Commands.isEmpty();
        dispatcher.dispatch(command);
        assertThat(dataStore.getOperations(), contains(DataStoreOperations.IS_EMPTY));
        assertThat(dataStore.getArguments(), is(Collections.singletonList(Collections.emptyList())));
    }

    @Test
    public void testStore() throws Exception {
        final String key = "key";
        final String value = "value";
        StoreCommand command = Commands.store(key, value);
        dispatcher.dispatch(command);
        assertThat(dataStore.getOperations(), contains(DataStoreOperations.STORE));
        assertThat(dataStore.getArguments(), is(Collections.singletonList(Arrays.asList(key, value))));
    }

    @Test
    public void testGet() throws Exception {
        final String key = "key";
        GetCommand command = Commands.get(key);
        dispatcher.dispatch(command);
        assertThat(dataStore.getOperations(), contains(DataStoreOperations.GET));
        assertThat(dataStore.getArguments(), is(Collections.singletonList(Collections.singletonList(key))));
    }

    @Test
    public void testDelete() throws Exception {
        final String key = "key";
        DeleteCommand command = Commands.delete(key);
        dispatcher.dispatch(command);
        assertThat(dataStore.getOperations(), contains(DataStoreOperations.DELETE));
        assertThat(dataStore.getArguments(), is(Collections.singletonList(Collections.singletonList(key))));
    }

    @Test
    public void testKeys() throws Exception {
        KeysCommand command = Commands.keys();
        dispatcher.dispatch(command);
        assertThat(dataStore.getOperations(), contains(DataStoreOperations.KEYS));
        assertThat(dataStore.getArguments(), is(Collections.singletonList(Collections.emptyList())));
    }

    @Test
    public void testTruncate() throws Exception {
        TruncateCommand command = Commands.truncate();
        dispatcher.dispatch(command);
        assertThat(dataStore.getOperations(), contains(DataStoreOperations.TRUNCATE));
        assertThat(dataStore.getArguments(), is(Collections.singletonList(Collections.emptyList())));
    }

    @Test
    public void testHas() throws Exception {
        final String key = "key";
        HasCommand command = Commands.has(key);
        dispatcher.dispatch(command);
        assertThat(dataStore.getOperations(), contains(DataStoreOperations.HAS));
        assertThat(dataStore.getArguments(), is(Collections.singletonList(Collections.singletonList(key))));
    }

    @Test
    public void testAll() throws Exception {
        AllCommand command = Commands.all();
        dispatcher.dispatch(command);
        assertThat(dataStore.getOperations(), contains(DataStoreOperations.ALL));
        assertThat(dataStore.getArguments(), is(Collections.singletonList(Collections.emptyList())));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testUnknownCommand() throws Exception {
        dispatcher.dispatch(new DataStoreCommand<Object>() {
        });
    }
}
