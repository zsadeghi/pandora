package me.theyinspire.pandora.core.server.impl;

import me.theyinspire.pandora.core.server.Incoming;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 1:18 PM)
 */
public class SimpleIncoming implements Incoming {

    private final String content;

    public SimpleIncoming(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

}
