package me.theyinspire.pandora.cli;

import me.theyinspire.pandora.cli.impl.DefaultConfigurationReader;
import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.config.*;
import me.theyinspire.pandora.core.config.impl.DefaultOptionRegistry;
import me.theyinspire.pandora.core.datastore.DataStoreRegistry;
import me.theyinspire.pandora.core.datastore.impl.DefaultDataStoreRegistry;
import me.theyinspire.pandora.core.error.ConfigurationException;
import me.theyinspire.pandora.core.protocol.Protocol;
import me.theyinspire.pandora.core.protocol.ProtocolRegistry;
import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;
import me.theyinspire.pandora.core.server.Server;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 3:25 PM)
 */
public class Launcher {

    public static void main(String[] args) {
        args = "interactive --tcp-port=8081".split(" ");
        final ProtocolRegistry protocolRegistry;
        final DataStoreRegistry dataStoreRegistry;
        try {
            protocolRegistry = setUpProtocolRegistry();
            dataStoreRegistry = setUpDataStoreRegistry();
        } catch (ClassNotFoundException e) {
            printError(e);
            return;
        }
        final DefaultConfigurationReader configurationReader = new DefaultConfigurationReader();
        try {
            final ExecutionConfiguration executionConfiguration = configurationReader.read(null, args);
            if (ExecutionMode.CLIENT.equals(executionConfiguration.getExecutionMode())) {
                final ClientExecutionConfiguration configuration = (ClientExecutionConfiguration) executionConfiguration;
                final String command = configuration.getCommand();
                sendCommand(protocolRegistry, configuration, command);
            } else if (ExecutionMode.SERVER.equals(executionConfiguration.getExecutionMode())) {
                final ServerExecutionConfiguration configuration = (ServerExecutionConfiguration) executionConfiguration;
                final List<Server> servers = configuration.getProtocols().stream()
                        .map(configuration::getServerConfiguration)
                        .map(serverConfiguration -> protocolRegistry.getServer(serverConfiguration.getProtocol(), serverConfiguration))
                        .collect(Collectors.toList());
                final List<Thread> serverThreads = servers.stream()
                        .map(server -> new Thread(server::start))
                        .collect(Collectors.toList());
                servers.forEach((server) -> Runtime.getRuntime().addShutdownHook(new Thread(server::stop)));
                serverThreads.forEach(Thread::start);
                for (Thread serverThread : serverThreads) {
                    serverThread.join();
                }
            } else if (ExecutionMode.INTERACTIVE.equals(executionConfiguration.getExecutionMode())) {
                ClientExecutionConfiguration currentConfiguration = (ClientExecutionConfiguration) executionConfiguration;
                System.out.println("Entering interactive mode. Type `/quit` to exit. Type `/help` for more commands.");
                final Scanner scanner = new Scanner(System.in);
                while (true) {
                    System.out.print(currentConfiguration.getProtocol().getName().toLowerCase());
                    System.out.print("://");
                    System.out.print(currentConfiguration.getClientConfiguration().getHost().toLowerCase());
                    System.out.print(":");
                    System.out.print(currentConfiguration.getClientConfiguration().getPort());
                    System.out.print("> ");
                    final String line = scanner.nextLine().trim();
                    if (line.toLowerCase().equals("/quit")) {
                        break;
                    } else if (line.toLowerCase().equals("/help")) {
                        System.out.println("/quit");
                        System.out.println("\tExit the interactive mode");
                        System.out.println("/ls");
                        System.out.println("\tPrint out the current configuration");
                        System.out.println("/config [args]");
                        System.out.println("\tRead the args as configuration parameters, and switch to the new configuration.");
                        System.out.println("\tThe arguments follow the same pattern as with the normal application usage, without");
                        System.out.println("\tthe need for <mode> to be specified.");
                        System.out.println("/usage");
                        System.out.println("\tPrint out the regular application usage.");
                        System.out.println("{anything else}");
                        System.out.println("\tSends out the input as a command to the server");
                    } else if (line.toLowerCase().equals("/ls")) {
                        final List<String> keys = currentConfiguration.keys().stream().sorted().collect(Collectors.toList());
                        for (String key : keys) {
                            System.out.println(key + " = " + currentConfiguration.get(key));
                        }
                    } else if (line.toLowerCase().startsWith("/config ")) {
                        final String[] newArgs = ("client " + line.substring("/config ".length())).split(" ");
                        currentConfiguration = (ClientExecutionConfiguration) configurationReader.read(currentConfiguration, newArgs);
                    } else if (line.toLowerCase().equals("/usage")) {
                        printUsage(protocolRegistry, dataStoreRegistry);
                    } else {
                        final String command = line.trim();
                        try {
                            sendCommand(protocolRegistry, currentConfiguration, command);
                        } catch (Exception e) {
                            printError(e);
                        }
                    }
                }
                System.out.println("Exiting interactive mode.");
            } else {
                throw new ConfigurationException("Invalid execution mode: " + executionConfiguration.getExecutionMode());
            }

        } catch (ConfigurationException configurationException) {
            if (args.length > 1) {
                printError(configurationException);
                System.out.println();
            }
            printUsage(protocolRegistry, dataStoreRegistry);
        } catch (Exception e) {
            printError(e);
        }
    }

    private static void sendCommand(ProtocolRegistry protocolRegistry, ClientExecutionConfiguration configuration, String command) {
        final Client client = protocolRegistry.getClient(configuration.getProtocol(), configuration.getClientConfiguration());
        final String response = client.send(command);
        System.out.println(response);
    }

    private static void loadPropertyClasses(String property) throws ClassNotFoundException {
        final String stores = System.getProperty(property);
        if (stores != null) {
            for (String className : stores.split("\\s*,\\s*")) {
                Class.forName(className);
            }
        }
    }

    private static DataStoreRegistry setUpDataStoreRegistry() throws ClassNotFoundException {
        Class.forName("me.theyinspire.pandora.core.datastore.mem.Loader");
        Class.forName("me.theyinspire.pandora.dds.Loader");
        Class.forName("me.theyinspire.pandora.raft.Loader");
        loadPropertyClasses("pandora.stores");
        return DefaultDataStoreRegistry.getInstance();
    }

    private static ProtocolRegistry setUpProtocolRegistry() throws ClassNotFoundException {
        Class.forName("me.theyinspire.pandora.tcp.Loader");
        Class.forName("me.theyinspire.pandora.rmi.Loader");
        Class.forName("me.theyinspire.pandora.udp.Loader");
        Class.forName("me.theyinspire.pandora.rest.Loader");
        loadPropertyClasses("pandora.protocols");
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
        System.out.println();
        System.out.println("Server mode:");
        System.out.println("To start the application in server mode, you must specify mode as `server`:");
        System.out.println("\t/path/to/launcher server [options]");
        System.out.println("Options in client mode include:");
        System.out.println("\t[--protocols=" + defaultProtocol + "]");
        System.out.println("\t\tA comma-separated list of protocols to use. Can be a subset of " + protocolRegistry.getKnownProtocols().stream().map(Protocol::getName).sorted().collect(Collectors.toList()));
        System.out.println("\t[--data-store=" + defaultDataStore + "]");
        System.out.println("\t\tThe data store instance to use. Can be one of " + dataStoreRegistry.getKnownDataStores());
        printProtocolOptions(protocolRegistry);
        printDataStoreOptions(dataStoreRegistry);
        System.out.println();
        System.out.println("Note: to add support for additional data store types and protocol types, you");
        System.out.println("can pass in JVM options `pandora.stores` and `pandora.protocols` respectively.");
        System.out.println("These properties must be comma-separated fully qualified names of loader");
        System.out.println("classes which will statically bind and register the given data store/protocol.");
        System.out.println("For example:");
        System.out.println("-Dpandora.protocols=my.protocol.package.Loader /path/to/launcher");
        System.out.println("These classes will be loaded in addition to the already supported classes.");
        System.out.println();
        System.out.println("Interactive mode:");
        System.out.println("In interactive mode an initial set of arguments is read from the command line the same");
        System.out.println("as with the client mode, however, no command is read or executed. Instead, you will be");
        System.out.println("presented with a REPL which will execute commands against a designated server.");
        System.out.println("This client can be reconfigured at any point (see /config) to point to a different");
        System.out.println("server instance.");
        System.out.flush();
    }

    private static void printDataStoreOptions(DataStoreRegistry dataStoreRegistry) {
        final Integer totalOptions = dataStoreRegistry.getKnownDataStores().stream()
                .map(DefaultOptionRegistry.getInstance()::getDataStoreOptions)
                .map(List::size)
                .reduce(Integer::sum)
                .orElse(0);
        if (totalOptions == 0) {
            return;
        }
        System.out.println();
        System.out.println("Data store specific options are:");
        System.out.println();
        for (String dataStore : dataStoreRegistry.getKnownDataStores()) {
            System.out.println(dataStore);
            System.out.println(new String(new char[dataStore.length()]).replace('\0', '='));
            final List<DataStoreOption> options = DefaultOptionRegistry.getInstance().getDataStoreOptions(dataStore);
            printOptions("ds-", dataStore, options);
        }
    }

    private static void printProtocolOptions(ProtocolRegistry protocolRegistry) {
        System.out.println();
        System.out.println("Protocol specific options are:");
        for (Protocol protocol : protocolRegistry.getKnownProtocols()) {
            final String prefix = protocol.getName();
            final List<ProtocolOption> options = DefaultOptionRegistry.getInstance().getProtocolOptions(protocol);
            printOptions("", prefix, options);
        }
    }

    private static void printOptions(String prefix, String name, List<? extends Option> options) {
        for (Option option : options) {
            System.out.print("\t");
            if (option.isOptional()) {
                System.out.print("[");
            }
            System.out.print("--" + prefix + name + "-" + option.getName());
            if (option.isOptional()) {
                System.out.print("=" + option.getDefaultValue());
                System.out.print("]");
            }
            System.out.println();
            System.out.println("\t\t" + option.getDescription());
        }
    }

    private static void printError(Throwable exception) {
        System.out.println(exception.getMessage());
        System.out.flush();
        if (exception.getCause() != null) {
            System.out.print("Caused by: ");
            printError(exception.getCause());
        }
    }

}
