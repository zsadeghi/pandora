package me.theyinspire.pandora.kisscli;

import me.theyinspire.pandora.cli.Launcher;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/11/17, 10:54 PM)
 */
public class KissCliLauncher {

    private static final Map<String, String> protocols = new HashMap<>();

    static {
        protocols.put("n", "registry");
        protocols.put("t", "tcp");
        protocols.put("u", "udp");
        protocols.put("r", "rest");
        protocols.put("rmi", "rmi");
        protocols.put("tu", "tcp/udp");
        protocols.put("all", "all");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        final String[] actualArgs;
        final char mode = args[0].charAt(args[0].length() - 1);
        final String protocol = protocols.get(args[0].substring(0, args[0].length() - 1));
        if (mode == 's') {
            // server mode
            actualArgs = getServerArgs(protocol, args);
        } else if (mode == 'r') {
            // server mode
            actualArgs = getRegistryArgs(protocol, args);
        } else if (mode == 'c') {
            // client mode
            actualArgs = getClientArgs(protocol, args);
        } else if (args[0].equals("@")) {
            actualArgs = new String[args.length - 1];
            System.arraycopy(args, 1, actualArgs, 0, actualArgs.length);
        } else {
            usageError();
            return;
        }
        Launcher.main(actualArgs);
    }

    private static String[] getClientArgs(final String protocol, String[] args) {
        final int offset;
        if (protocol.equals("rmi")) {
            offset = 0;
        } else {
            offset = 1;
        }
        String[] actualArgs;
        actualArgs = new String[args.length + 2];
        actualArgs[0] = "client";
        actualArgs[1] = "--protocol=" + protocol;
        actualArgs[2] = "--" + protocol + "-host=" + args[1];
        if (!protocol.equals("rmi")) {
            actualArgs[3] = "--" + protocol + "-port=" + args[2];
        }
        actualArgs[3 + offset] = "--";
        System.arraycopy(args, 2 + offset, actualArgs, 4 + offset, args.length - 2 - offset);
        return actualArgs;
    }

    private static String[] getServerArgs(final String protocol, String[] args) {
        if (protocol.equals("tcp/udp")) {
            return new String[]{
                    "server",
                    "--data-store=raft",
                    "--ds-raft-discovery=registry",
                    "--ds-raft-registry-uri=" + args[2],
                    "--protocols=tcp,udp",
                    "--tcp-port=" + args[1],
                    "--udp-port=" + args[2]
            };
        }
        if (protocol.equals("all")) {
            return new String[]{
                    "server",
                    "--data-store=raft",
                    "--ds-raft-discovery=registry",
                    "--ds-raft-registry-uri=" + args[2],
                    "--protocols=tcp,udp,rmi",
                    "--tcp-port=" + args[1],
                    "--udp-port=" + args[2]
            };
        }
        return new String[]{
                "server",
                "--data-store=raft",
                "--ds-raft-discovery=registry",
                "--ds-raft-registry-uri=" + (protocol.equals("rmi") ? args[1] : args[2]),
                "--protocols=" + protocol,
                "--" + protocol + "-port=" + (protocol.equals("rmi") ? "9090" : args[1]),
        };
    }

    private static String[] getRegistryArgs(final String protocol, String[] args) {
        if (protocol.equals("tcp/udp")) {
            return new String[]{
                    "server",
                    "--data-store=memory",
                    "--protocols=tcp,udp",
                    "--tcp-port=" + args[1],
                    "--udp-port=" + args[2]
            };
        }
        if (protocol.equals("all")) {
            return new String[]{
                    "server",
                    "--data-store=memory",
                    "--protocols=tcp,udp,rmi",
                    "--tcp-port=" + args[1],
                    "--udp-port=" + args[2]
            };
        }
        return new String[]{
                "server",
                "--data-store=memory",
                "--protocols=" + protocol,
                "--" + protocol + "-port=" + (protocol.equals("rmi") ? "9090" : args[1]),
        };
    }

    private static void usageError() {
        System.out.println("Invalid usage.");
        printUsage();
        System.exit(1);
    }

    private static void printUsage() {
        System.out.println("Usage: /path/to/launcher <options>");
        System.out.println("");
        System.out.println("Client mode:");
        System.out.println("");
        System.out.println("<client> <host> <port> <command>");
        System.out.println("");
        System.out.println("\t<client> can be one of:");
        System.out.println("\t  * `tc` for TCP client");
        System.out.println("\t  * `up` for UDP client");
        System.out.println("\t  * `rc` for REST client");
        System.out.println("\t  * `rmic` for RMI client");
        System.out.println("\t<host> must be a valid INET name pointing at the server");
        System.out.println("\t<port> must be a valid integer identifying the port on which the server is listening");
        System.out.println("\t<command> can be one of:");
        System.out.println("\t  * put <key> <value>");
        System.out.println("\t    which will store <value> under key <key>");
        System.out.println("\t  * get <key>");
        System.out.println("\t    which will look up the value for <key>");
        System.out.println("\t  * del <key>");
        System.out.println("\t    which will delete the item stored under <key>");
        System.out.println("\t  * has <key>");
        System.out.println("\t    which will return `true` if there is an item for key <key>");
        System.out.println("\t  * truncate");
        System.out.println("\t    which will delete all items");
        System.out.println("\t  * empty");
        System.out.println("\t    which will return `true` if the data store is empty");
        System.out.println("\t  * size");
        System.out.println("\t    which will return the size of the data store");
        System.out.println("\t  * store");
        System.out.println("\t    which will return all items stored in the data store as `key:<key>:value:<value>:`");
        System.out.println("");
        System.out.println("Raft Server mode:");
        System.out.println("");
        System.out.println("You can start the application in following server modes:");
        System.out.println("");
        System.out.println("* TCP server:");
        System.out.println("\t /path/to/launcher ts <tcp-port> <registry-uri>");
        System.out.println("* UDP server:");
        System.out.println("\t /path/to/launcher us <udp-port> <registry-uri>");
        System.out.println("* TCP+UDP server:");
        System.out.println("\t /path/to/launcher tus <tcp-port> <udp-port> <registry-uri>");
        System.out.println("* RMI server:");
        System.out.println("\t /path/to/launcher rmis <registry-uri>");
        System.out.println("* TCP+UDP+RMI server:");
        System.out.println("\t /path/to/launcher alls <tcp-port> <udp-port> <registry-uri>");
        System.out.println("* REST server:");
        System.out.println("\t /path/to/launcher rs <rest-port> <registry-uri>");
        System.out.println("* Registry server:");
        System.out.println("\t /path/to/launcher tr <tcp-port>");
        System.out.println("\t /path/to/launcher ur <udp-port>");
        System.out.println("\t /path/to/launcher tur <tcp-port> <udp-port>");
        System.out.println("\t /path/to/launcher rmir");
        System.out.println("\t /path/to/launcher allr <tcp-port> <udp-port>");
        System.out.println("\t /path/to/launcher rr <rest-port>");
    }

}
