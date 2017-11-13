package me.theyinspire.pandora.core.server.impl;

import me.theyinspire.pandora.core.server.Outgoing;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 1:22 PM)
 */
public class SimpleOutgoing implements Outgoing {

    private final String content;

    public SimpleOutgoing(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

}
