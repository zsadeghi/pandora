package me.theyinspire.pandora.core.client.impl;

import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.UriClientConfigurationReader;
import me.theyinspire.pandora.core.client.error.ClientException;
import me.theyinspire.pandora.core.config.impl.DefaultClientConfiguration;
import me.theyinspire.pandora.core.config.impl.DefaultConfiguration;
import me.theyinspire.pandora.core.protocol.Protocol;
import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/16/17, 3:44 PM)
 */
public class DefaultUriClientConfigurationReader implements UriClientConfigurationReader {

    @Override
    public ClientConfiguration read(String uri) {
        final URI context;
        try {
            context = new URI(uri);
        } catch (URISyntaxException e) {
            throw new ClientException("Failed to read URI", e);
        }
        final String protocolName = context.getScheme();
        final String host = context.getHost();
        final String port = String.valueOf(context.getPort());
        final String[] settings = context.getQuery() == null ? new String[0] : context.getQuery().split("&");
        final Map<String, String> data = new HashMap<>();
        data.put(option(protocolName, "host"), host);
        data.put(option(protocolName, "port"), port);
        for (String setting : settings) {
            final String[] split = setting.split("=", 2);
            data.put(option(protocolName, split[0]), split[1]);
        }
        final DefaultConfiguration configuration = new DefaultConfiguration(data);
        final Protocol protocol = DefaultProtocolRegistry.getInstance().getProtocolByName(protocolName);
        return new DefaultClientConfiguration(configuration, protocol);
    }

    private String option(String protocolName, String key) {
        return protocolName + "-" + key;
    }

}
