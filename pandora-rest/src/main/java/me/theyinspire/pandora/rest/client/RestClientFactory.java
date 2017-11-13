package me.theyinspire.pandora.rest.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.ClientFactory;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/11/17, 8:05 PM)
 */
public class RestClientFactory implements ClientFactory {

    @Override
    public Client getInstance(ClientConfiguration configuration) {
        return new RestClient(configuration.getHost(), configuration.getPort(), configuration.get("base", ""), new ObjectMapper());
    }

}
