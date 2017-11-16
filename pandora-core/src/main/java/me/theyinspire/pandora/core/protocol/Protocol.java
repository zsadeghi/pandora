package me.theyinspire.pandora.core.protocol;

import me.theyinspire.pandora.core.config.ScopedOptionRegistry;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/28/17, 12:54 PM)
 */
public interface Protocol {

    String getName();

    default ProtocolReader getReader() {
        return null;
    }

    default ProtocolWriter getWriter() {
        return null;
    }

    default String transform(String message) {
        return message;
    }

    void defineOptions(ScopedOptionRegistry registry);

}
