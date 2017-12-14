package me.theyinspire.pandora.raft;

import me.theyinspire.pandora.core.cmd.impl.AggregateCommandDeserializer;
import me.theyinspire.pandora.core.cmd.impl.AggregateCommandSerializer;
import me.theyinspire.pandora.core.datastore.impl.DefaultDataStoreRegistry;
import me.theyinspire.pandora.raft.cmd.impl.RaftCommandDeserializer;
import me.theyinspire.pandora.raft.cmd.impl.RaftCommandSerializer;
import me.theyinspire.pandora.raft.impl.RaftDataStoreFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/12/17, 11:24 PM)
 */
public class Loader {

    static {
        DefaultDataStoreRegistry.getInstance().register(new RaftDataStoreFactory());
        AggregateCommandDeserializer.getInstance().add(new RaftCommandDeserializer());
        AggregateCommandSerializer.getInstance().add(new RaftCommandSerializer());
    }

}
