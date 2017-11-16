package me.theyinspire.pandora.core.config.impl;

import me.theyinspire.pandora.core.config.DataStoreOption;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 10:59 AM)
 */
public class ImmutableDataStoreOption extends AbstractOption implements DataStoreOption {

    private final String dataStoreName;

    public ImmutableDataStoreOption(String name, String description, String defaultValue, String dataStoreName) {
        super(name, description, defaultValue);
        this.dataStoreName = dataStoreName;
    }

    @Override
    public String getDataStoreName() {
        return dataStoreName;
    }

}
