package me.theyinspire.pandora.core.datastore;

import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/26/17, 6:10 PM)
 */
public abstract class BaseDataStoreTest {

    private DataStore dataStore;

    protected abstract DataStore getDataStore();

    @BeforeMethod
    public void setUp() throws Exception {
        dataStore = getDataStore();
        assertThat(dataStore, Matchers.is(Matchers.notNullValue()));
    }

    @Test
    public void testThatItIsInitiallyEmpty() throws Exception {
        assertThat(dataStore.size(), Matchers.is(0L));
        assertThat(dataStore.isEmpty(), Matchers.is(true));
    }

    @Test
    public void testThatItDoesNotReturnValuesItDoesNotHave() throws Exception {
        assertThat(dataStore.get("key"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testThatItCanStoreAValue() throws Exception {
        final String key = "key";
        final String value = "value";
        assertThat(dataStore.store(key, value), Matchers.is(true));
    }

    @Test
    public void testThatItCanRememberAValue() throws Exception {
        final String key = "key";
        final String value = "value";
        dataStore.store(key, value);
        assertThat(dataStore.get(key), Matchers.<Serializable>is(value));
    }

    @Test
    public void testThatValuesCanBeOverridden() throws Exception {
        final String key = "key";
        final String firstValue = "value";
        final String secondValue = "value";
        dataStore.store(key, firstValue);
        dataStore.store(key, secondValue);
        assertThat(dataStore.get(key), Matchers.<Serializable>is(secondValue));
    }

    @Test
    public void testThatItDoesNotDeleteValuesItDoesNotHave() throws Exception {
        assertThat(dataStore.delete("key"), Matchers.is(false));
    }

    @Test
    public void testThatItDeletesValuesItContains() throws Exception {
        dataStore.store("key", "value");
        assertThat(dataStore.delete("key"), Matchers.is(true));
    }

    @Test
    public void testThatItCanRememberAllKeys() throws Exception {
        dataStore.store("key1", null);
        dataStore.store("key2", null);
        dataStore.store("key3", null);
        assertThat(dataStore.keys(), Matchers.containsInAnyOrder("key1", "key2", "key3"));
    }

    @Test
    public void testThatItCanTruncateAllValues() throws Exception {
        dataStore.store("key1", null);
        dataStore.store("key2", null);
        dataStore.store("key3", null);
        assertThat(dataStore.size(), Matchers.is(3L));
        assertThat(dataStore.truncate(), Matchers.is(3L));
        assertThat(dataStore.size(), Matchers.is(0L));
    }

    @Test
    public void testIntegrity() throws Exception {
        for (int i = 0; i < 10; i++) {
            dataStore.store("key" + i, i);
        }
        assertThat(dataStore.size(), Matchers.is(10L));
        final Map<String, Serializable> all = dataStore.all();
        assertThat(all.size(), Matchers.is(10));
        for (int i = 0; i < 10; i++) {
            final String key = "key" + i;
            assertThat(all.keySet(), Matchers.hasItem(key));
            assertThat(all.get(key), Matchers.is(i));
        }
        for (int i = 0; i < 10; i++) {
            final String key = "key" + i;
            assertThat(dataStore.has(key), Matchers.is(true));
            assertThat(dataStore.get(key), Matchers.<Serializable>is(i));
            assertThat(dataStore.delete(key), Matchers.is(true));
            assertThat(dataStore.get(key), Matchers.is(Matchers.nullValue()));
            assertThat(dataStore.has(key), Matchers.is(false));
        }
        assertThat(dataStore.isEmpty(), Matchers.is(true));
    }

}
