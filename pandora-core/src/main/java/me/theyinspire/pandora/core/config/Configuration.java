package me.theyinspire.pandora.core.config;

import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/31/17, 10:02 PM)
 */
public interface Configuration {

    String get(String key);

    String require(String key);

    String get(String key, String defaultValue);

    boolean has(String key);

    Set<String> keys();

}
