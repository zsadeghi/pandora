package me.theyinspire.pandora.core.cmd.impl;

import me.theyinspire.pandora.core.cmd.Command;
import me.theyinspire.pandora.core.cmd.CommandSerializer;
import me.theyinspire.pandora.core.datastore.cmd.impl.DataStoreCommandSerializer;

import java.util.*;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 10:16 AM)
 */
public class AggregateCommandSerializer implements CommandSerializer {

    private static final AggregateCommandSerializer INSTANCE = new AggregateCommandSerializer();

    public static AggregateCommandSerializer getInstance() {
        return INSTANCE;
    }

    private List<Class<? extends Command>> accepts;
    private final Map<Class<? extends Command>, CommandSerializer> serializers;

    private AggregateCommandSerializer() {
        final List<CommandSerializer> serializers = Arrays.asList(new DataStoreCommandSerializer());
        this.serializers = new HashMap<>();
        accepts = new ArrayList<>();
        for (CommandSerializer serializer : serializers) {
            accepts.addAll(serializer.accepts());
            for (Class<? extends Command> commandType : serializer.accepts()) {
                this.serializers.put(commandType, serializer);
            }
        }
    }

    @Override
    public List<Class<? extends Command>> accepts() {
        return accepts;
    }

    @Override
    public String serializeCommand(Command<?> command) {
        return getSerializer(command).serializeCommand(command);
    }

    @Override
    public String serializeResponse(Command<?> command, Object response) {
        return getSerializer(command).serializeResponse(command, response);
    }

    private CommandSerializer getSerializer(Command<?> command) {
        for (Class<? extends Command> commandType : accepts) {
            if (commandType.isInstance(command)) {
                return serializers.get(commandType);
            }
        }
        return null;
    }

}
