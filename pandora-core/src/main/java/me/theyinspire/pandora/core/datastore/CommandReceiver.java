package me.theyinspire.pandora.core.datastore;

import me.theyinspire.pandora.core.cmd.CommandWithArguments;

/**
 * @author Mohammad Milad Naseri (mmnaseri@programmer.net)
 * @since 1.0 (12/12/17, 1:38 PM)
 */
public interface CommandReceiver {

    String receive(CommandWithArguments command);

}
