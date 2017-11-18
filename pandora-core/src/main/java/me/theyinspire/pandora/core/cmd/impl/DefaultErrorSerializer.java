package me.theyinspire.pandora.core.cmd.impl;

import me.theyinspire.pandora.core.cmd.ErrorSerializer;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/17/17, 5:10 PM)
 */
public class DefaultErrorSerializer implements ErrorSerializer {

    public static final DefaultErrorSerializer INSTANCE = new DefaultErrorSerializer();

    public static DefaultErrorSerializer getInstance() {
        return INSTANCE;
    }

    private DefaultErrorSerializer() {
    }

    @Override
    public String serialize(Throwable throwable) {
        final StringBuilder builder = new StringBuilder();
        if (throwable.getMessage() == null) {
            builder.append(throwable.getClass().getName());
        } else {
            builder.append(throwable.getMessage());
        }
        if (throwable.getCause() != null) {
            builder.append(" caused by ");
            builder.append(serialize(throwable.getCause()));
        }
        return builder.toString();
    }

}
