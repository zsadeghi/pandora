package me.theyinspire.pandora.cli;

import me.theyinspire.pandora.cli.error.ConfigurationException;
import me.theyinspire.pandora.cli.impl.DefaultConfigurationReader;
import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.error.ClientException;
import me.theyinspire.pandora.core.config.Option;
import me.theyinspire.pandora.core.config.impl.DefaultOptionRegistry;
import me.theyinspire.pandora.core.datastore.DataStoreRegistry;
import me.theyinspire.pandora.core.datastore.impl.DefaultDataStoreRegistry;
import me.theyinspire.pandora.core.protocol.Protocol;
import me.theyinspire.pandora.core.protocol.ProtocolRegistry;
import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;
import me.theyinspire.pandora.core.server.Server;
import me.theyinspire.pandora.core.server.error.ServerException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:25 PM)
 */
public class Launcher {

    public static void main(String[] args) throws Exception {
        final ProtocolRegistry protocolRegistry = setUpProtocolRegistry();
        final DataStoreRegistry dataStoreRegistry = setUpDataStoreRegistry();
        final DefaultConfigurationReader configurationReader = new DefaultConfigurationReader();
        try {
            final ExecutionConfiguration executionConfiguration = configurationReader.read(args);
            if (ExecutionMode.CLIENT.equals(executionConfiguration.getExecutionMode())) {
                final ClientExecutionConfiguration configuration = (ClientExecutionConfiguration) executionConfiguration;
                final Client client = protocolRegistry.getClient(configuration.getProtocol(), configuration.getClientConfiguration());
                final String response = client.send(configuration.getCommand());
                System.out.println(response);
            } else if (ExecutionMode.SERVER.equals(executionConfiguration.getExecutionMode())) {
                final ServerExecutionConfiguration configuration = (ServerExecutionConfiguration) executionConfiguration;
                final List<Server> servers = configuration.getProtocols().stream()
                        .map(configuration::getServerConfiguration)
                        .map(serverConfiguration -> protocolRegistry.getServer(serverConfiguration.getProtocol(), serverConfiguration))
                        .collect(Collectors.toList());
                final List<Thread> serverThreads = servers.stream()
                        .map(server -> new Thread(server::start))
                        .collect(Collectors.toList());
                servers.forEach((server) -> {
                    Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
                });
                serverThreads.forEach(Thread::start);
                for (Thread serverThread : serverThreads) {
                    serverThread.join();
                }
            } else {
                throw new ConfigurationException("Invalid execution mode: " + executionConfiguration.getExecutionMode());
            }

        } catch (ConfigurationException configurationException) {
            if (args.length > 1) {
                printError(configurationException);
            }
            printUsage(protocolRegistry, dataStoreRegistry);
        } catch (ClientException | ServerException e) {
            printError(e);
        }
    }

    private static DataStoreRegistry setUpDataStoreRegistry() throws ClassNotFoundException {
        Class.forName("me.theyinspire.pandora.core.Loader");
        return DefaultDataStoreRegistry.getInstance();
    }

    private static ProtocolRegistry setUpProtocolRegistry() throws ClassNotFoundException {
        Class.forName("me.theyinspire.pandora.tcp.Loader");
        Class.forName("me.theyinspire.pandora.rmi.Loader");
        Class.forName("me.theyinspire.pandora.udp.Loader");
        Class.forName("me.theyinspire.pandora.rest.Loader");
        return DefaultProtocolRegistry.getInstance();
    }

    private static void printUsage(ProtocolRegistry protocolRegistry, DataStoreRegistry dataStoreRegistry) {
        if (protocolRegistry.getKnownProtocols().isEmpty()) {
            throw new IllegalStateException("No known protocols were found");
        }
        final String defaultProtocol = protocolRegistry.getKnownProtocols().get(0).getName();
        if (dataStoreRegistry.getKnownDataStores().isEmpty()) {
            throw new IllegalStateException("No known data stores");
        }
        final String defaultDataStore = dataStoreRegistry.getKnownDataStores().get(0);
        System.out.println("Usage: /path/to/launcher <mode> [options] [-- <command>]");
        System.out.println();
        System.out.println("You can start this application in either client mode or server mode.");
        System.out.println();
        System.out.println("Client mode:");
        System.out.println("To start the application in client mode, you must specify mode as `client`:");
        System.out.println("\t/path/to/launcher client [options] -- <command>");
        System.out.println("Options in client mode include:");
        System.out.println("\t[--protocol=" + defaultProtocol + "]");
        System.out.println("\t\tThe protocol to use. Can be one of " + protocolRegistry.getKnownProtocols().stream().map(Protocol::getName).sorted().collect(Collectors.toList()));
        System.out.println("<command> must be of one of the following formats:");
        System.out.println("\tput <key> <value>");
        System.out.println("\t\tStores <key> in the data store with value <value>");
        System.out.println("\tget <key>");
        System.out.println("\t\tLooks up the item stored under key <key>");
        System.out.println("\tdel <key>");
        System.out.println("\t\tDeletes the item stored under key <key>");
        System.out.println("\thas <key>");
        System.out.println("\t\tReturns `true` if the data store has item with key <key>");
        System.out.println("\tstore");
        System.out.println("\t\tReturns all the items in the data store with format `key:<key>:value:<value>:`");
        System.out.println("\tkeys");
        System.out.println("\t\tReturns all the keys stored in the data store");
        System.out.println("\tsize");
        System.out.println("\t\tReturns the size of the data store");
        System.out.println("\tempty");
        System.out.println("\t\tReturns `true` if the data store is empty");
        System.out.println("\ttruncate");
        System.out.println("\t\tDeletes all items from the data store");
        System.out.println("Server mode:");
        System.out.println("To start the application in server mode, you must specify mode as `server`:");
        System.out.println("\t/path/to/launcher server [options]");
        System.out.println("Options in client mode include:");
        System.out.println("\t[--protocols=" + defaultProtocol + "]");
        System.out.println("\t\tA comma-separated list of protocols to use. Can be a subset of " + protocolRegistry.getKnownProtocols().stream().map(Protocol::getName).sorted().collect(Collectors.toList()));
        System.out.println("\t[--data-store=" + defaultDataStore + "]");
        System.out.println("\t\tThe data store instance to use. Can be one of " + dataStoreRegistry.getKnownDataStores());
        System.out.println();
        System.out.println("Protocol specific options are:");
        for (Protocol protocol : protocolRegistry.getKnownProtocols()) {
            final List<Option> options = DefaultOptionRegistry.getInstance().getOptions(protocol);
            for (Option option : options) {
                System.out.print("\t");
                if (option.isOptional()) {
                    System.out.print("[");
                }
                System.out.print("--" + protocol.getName() + "-" + option.getName());
                if (option.isOptional()) {
                    System.out.print("=" + option.getDefaultValue());
                    System.out.print("]");
                }
                System.out.println();
                System.out.println("\t\t" + option.getDescription());
            }
        }
    }

    private static void printError(Throwable exception) {
        System.err.println(exception.getMessage());
        System.err.flush();
        if (exception.getCause() != null) {
            System.err.print("Caused by: ");
            printError(exception.getCause());
        }
    }

}
