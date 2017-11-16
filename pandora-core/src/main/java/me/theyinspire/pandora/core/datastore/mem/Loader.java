package me.theyinspire.pandora.core.datastore.mem;

import me.theyinspire.pandora.core.datastore.impl.DefaultDataStoreRegistry;
import me.theyinspire.pandora.core.datastore.mem.InMemoryDataStoreFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/31/17, 10:26 PM)
 */
public class Loader {

    static {
        DefaultDataStoreRegistry.getInstance().register("memory", new InMemoryDataStoreFactory());
    }

}
