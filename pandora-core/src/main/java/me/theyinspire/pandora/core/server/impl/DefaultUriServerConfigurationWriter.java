package me.theyinspire.pandora.core.server.impl;

import me.theyinspire.pandora.core.config.Option;
import me.theyinspire.pandora.core.config.ScopedOptionRegistry;
import me.theyinspire.pandora.core.config.impl.DefaultOptionRegistry;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.UriServerConfigurationWriter;

import java.util.Arrays;
import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 3:52 PM)
 */
public class DefaultUriServerConfigurationWriter implements UriServerConfigurationWriter {

    @Override
    public String write(ServerConfiguration configuration) {
        final StringBuilder builder = new StringBuilder();
        builder.append(configuration.getProtocol().getName());
        builder.append("://");
        builder.append(configuration.getHost());
        builder.append(":");
        builder.append(configuration.getPort());
        builder.append("/");
        final ScopedOptionRegistry registry = DefaultOptionRegistry.getInstance().getProtocolOptionRegistry(configuration.getProtocol());
        final List<String> filtered = Arrays.asList("host", "port");
        final String query = registry.getOptions().stream()
                .map(Option::getName)
                .filter(option -> !filtered.contains(option))
                .map(option -> option + "=" + registry.getDefaultValue(option))
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
        if (!query.isEmpty()) {
            builder.append("?");
            builder.append(query);
        }
        return builder.toString();
    }

}
