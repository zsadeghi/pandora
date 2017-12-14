package me.theyinspire.pandora.core.cmd;

import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (12/12/17, 1:24 PM)
 */
public interface CommandWithArguments extends Command<String> {

    String getCommand();

    List<String> getArguments();

}
