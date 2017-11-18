package me.theyinspire.pandora.core.cmd;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/17/17, 5:10 PM)
 */
public interface ErrorSerializer {

    String serialize(Throwable throwable);

}
