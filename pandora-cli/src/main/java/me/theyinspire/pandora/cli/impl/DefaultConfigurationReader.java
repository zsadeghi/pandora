package me.theyinspire.pandora.cli.impl;

import me.theyinspire.pandora.core.config.ConfigurationReader;
import me.theyinspire.pandora.core.config.ExecutionConfiguration;
import me.theyinspire.pandora.core.config.ExecutionMode;
import me.theyinspire.pandora.core.error.ConfigurationException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:43 PM)
 */
public class DefaultConfigurationReader implements ConfigurationReader {

    @Override
    public ExecutionConfiguration read(String... args) throws ConfigurationException {
        if (args.length < 1) {
            throw new ConfigurationException("You need to specify an execution mode");
        }
        final ExecutionMode executionMode;
        try {
            executionMode = ExecutionMode.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("Invalid execution mode: " + args[0].toUpperCase(), e);
        }
        final Map<String, String> config = new HashMap<>();
        int index = 1;
        while (index < args.length) {
            if ("--".equals(args[index])) {
                index ++;
                break;
            }
            if (!args[index].startsWith("--")) {
                throw new ConfigurationException("Invalid argument: " + args[index]);
            }
            args[index] = args[index].substring(2);
            final String key;
            final String value;
            if (args[index].contains("=")) {
                key = args[index].substring(0, args[index].indexOf('='));
                value = args[index].substring(args[index].indexOf('=') + 1);
            } else {
                key = args[index];
                value = "true";
            }
            config.put(key, value);
            index ++;
        }
        final String command;
        if (index < args.length) {
            if (!ExecutionMode.CLIENT.equals(executionMode)) {
                throw new ConfigurationException("Commands can only be issued through the client");
            }
            final StringBuilder builder = new StringBuilder();
            for (int i = index; i < args.length; i++) {
                builder.append(args[i]).append(' ');
            }
            command = builder.toString().trim();
        } else {
            command = "";
        }
        if (ExecutionMode.CLIENT.equals(executionMode)) {
            return new DefaultClientExecutionConfiguration(config, command);
        } else if (ExecutionMode.SERVER.equals(executionMode)) {
            return new DefaultServerExecutionConfiguration(config);
        }
        return null;
    }

}
