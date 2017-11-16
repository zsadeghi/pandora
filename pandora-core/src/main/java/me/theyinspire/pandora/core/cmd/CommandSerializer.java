package me.theyinspire.pandora.core.cmd;

import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 10:13 AM)
 */
public interface CommandSerializer {

    List<Class<? extends Command>> accepts();

    String serializeCommand(Command<?> command);

    String serializeResponse(Command<?> command, Object response);

}
