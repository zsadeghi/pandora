package me.theyinspire.pandora.core.config;

import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 2:15 AM)
 */
public interface ScopedOptionRegistry {

    void register(String name, String description, String defaultValue);

    List<Option> getOptions();

    void register(String name, String description);

    String getDefaultValue(String name, String fallback);

    String getDefaultValue(String name);

}
