package me.theyinspire.pandora.core.config;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 2:15 AM)
 */
public interface ProtocolOptionRegistry {

    void register(String name, String description, String defaultValue);

    void register(String name, String description);

    String getDefaultValue(String name, String fallback);

    String getDefaultValue(String name);

}
