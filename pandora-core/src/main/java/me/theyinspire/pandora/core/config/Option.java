package me.theyinspire.pandora.core.config;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/12/17, 2:16 AM)
 */
public interface Option {

    String getName();

    String getDescription();

    String getDefaultValue();

    boolean isOptional();

}
