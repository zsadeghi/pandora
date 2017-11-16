package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.config.Configuration;
import me.theyinspire.pandora.core.config.ScopedOptionRegistry;
import me.theyinspire.pandora.core.datastore.DataStoreConfiguration;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 2:37 PM)
 */
public class DefaultDataStoreConfiguration extends AbstractScopedConfiguration implements DataStoreConfiguration {

    private final String dataStore;

    public DefaultDataStoreConfiguration(Configuration delegate, String dataStore) {
        super(delegate);
        this.dataStore = dataStore;
    }

    protected String prefix(String key) {
        return "ds-" + dataStore + "-" + key;
    }

    @Override
    protected ScopedOptionRegistry getOptionRegistry() {
        return DefaultOptionRegistry.getInstance().getDataStoreOptionRegistry(dataStore);
    }

}
