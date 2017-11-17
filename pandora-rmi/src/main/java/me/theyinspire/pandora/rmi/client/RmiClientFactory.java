package me.theyinspire.pandora.rmi.client;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.ClientFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 4:13 PM)
 */
public class RmiClientFactory implements ClientFactory {
    @Override
    public Client getInstance(ClientConfiguration configuration) {
        return new RmiClient(configuration, configuration.getHost(), configuration.get("instance", "dataStore"));
    }
}
