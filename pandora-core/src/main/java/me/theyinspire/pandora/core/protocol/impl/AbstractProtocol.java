package me.theyinspire.pandora.core.protocol.impl;

import me.theyinspire.pandora.core.protocol.Protocol;
import me.theyinspire.pandora.core.protocol.ProtocolReader;
import me.theyinspire.pandora.core.protocol.ProtocolWriter;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 12:57 PM)
 */
public abstract class AbstractProtocol<R extends ProtocolReader, W extends ProtocolWriter> implements Protocol {

    private final R reader;
    private final W writer;

    public AbstractProtocol(R reader, W writer) {
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public final R getReader() {
        return reader;
    }

    @Override
    public final W getWriter() {
        return writer;
    }

}
