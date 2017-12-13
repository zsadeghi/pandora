package me.theyinspire.pandora.replica.impl;

import me.theyinspire.pandora.replica.ReplicaRegistry;
import me.theyinspire.pandora.replica.ReplicaRegistryInitializer;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 8:07 PM)
 */
public abstract class AbstractReplicaRegistry implements ReplicaRegistry {

    private final ReplicaRegistryInitializer initializer;

    protected AbstractReplicaRegistry(final ReplicaRegistryInitializer initializer) {
        this.initializer = initializer;
    }

    @Override
    public final void init(String signature, String uri) {
        onBeforeInit(signature, uri);
        if (initializer != null) {
            initializer.init(this);
        }
    }

    protected void onBeforeInit(String signature, String uri) {
    }

}
