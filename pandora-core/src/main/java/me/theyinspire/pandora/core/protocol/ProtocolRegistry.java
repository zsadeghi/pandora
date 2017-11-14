package me.theyinspire.pandora.core.protocol;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.ClientFactory;
import me.theyinspire.pandora.core.server.Server;
import me.theyinspire.pandora.core.server.ServerConfiguration;
import me.theyinspire.pandora.core.server.ServerFactory;

import java.util.List;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:23 PM)
 */
public interface ProtocolRegistry {

    void register(Protocol protocol, ClientFactory clientFactory, ServerFactory serverFactory);

    Server getServer(Protocol protocol, ServerConfiguration configuration);

    Client getClient(Protocol protocol, ClientConfiguration configuration);

    Protocol getProtocolByName(String protocol);

    List<Protocol> getKnownProtocols();

}
