package me.theyinspire.pandora.dds;

import me.theyinspire.pandora.core.datastore.impl.DefaultDataStoreRegistry;
import me.theyinspire.pandora.dds.impl.DistributedDataStoreFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 6:16 PM)
 */
public class Loader {

    static {
        DefaultDataStoreRegistry.getInstance().register(new DistributedDataStoreFactory());
    }

}
