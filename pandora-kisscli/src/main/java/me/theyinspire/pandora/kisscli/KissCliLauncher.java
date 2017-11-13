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
        protocols.put("t", "tcp");
        protocols.put("u", "udp");
        protocols.put("r", "rest");
        protocols.put("rmi", "rmi");
        protocols.put("tu", "tcp/udp");
        protocols.put("all", "all");
    }

    public static void main(String[] args) throws Exception {
        args = "alls 8080 8081".split(" ");
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
        } else if (mode == 'c') {
            // client mode
            actualArgs = getClientArgs(protocol, args);
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
        actualArgs[2] = "--host=" + args[1];
        if (!protocol.equals("rmi")) {
            actualArgs[3] = "--port=" + args[2];
        }
        actualArgs[3 + offset] = "--";
        System.arraycopy(args, 2 + offset, actualArgs, 5, args.length - 2 - offset);
        return actualArgs;
    }

    private static String[] getServerArgs(final String protocol, String[] args) {
        if (protocol.equals("tcp/udp")) {
            return new String[]{
                    "server",
                    "--protocols=tcp,udp",
                    "--tcp-port=" + args[1],
                    "--udp-port=" + args[2]
            };
        }
        if (protocol.equals("all")) {
            return new String[]{
                    "server",
                    "--protocols=tcp,udp,rmi",
                    "--tcp-port=" + args[1],
                    "--udp-port=" + args[2]
            };
        }
        return new String[]{
                "server",
                "--protocols=" + protocol,
                "--" + protocol + "port=" + (protocol.equals("rmi") ? "9090" : args[1])
        };
    }

    private static void usageError() {
        System.out.println("Invalid usage.");
        printUsage();
        System.exit(1);
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("Client:");
        System.out.println("uc/tc");
        System.out.println("store");
        System.out.println("uc/tc");
        System.out.println("key");
        System.out.println("uc/tc");
        System.out.println("by key");
        System.out.println("uc/tc <address> <port> store  UDP/TCP CLIENT: Display object store");
        System.out.println("uc/tc <address> <port> exit  UDP/TCP CLIENT: Shutdown server");
        System.out.println("<address> <port> put <key> <msg> UDP/TCP CLIENT: Put an object into <address> <port> get <key> UDP/TCP CLIENT: Get an object from store by <address> <port> del <key> UDP/TCP CLIENT: Delete an object from store");
        System.out.println("rmic <address> put <key> <msg> RMI CLIENT: Put an object into store rmic <address> get <key> RMI CLIENT: Get an object from store by key rmic <address> del <key> RMI CLIENT: Delete an object from store by key rmic <address> store RMI CLIENT: Display object store");
        System.out.println("rmic <address> exit RMI CLIENT: Shutdown server");
        System.out.println("Server:");
        System.out.println("us/ts <port> UDP/TCP/TCP­and­UDP SERVER: run server on <port>.");
        System.out.println("tus <tcpport> <udpport> TCP­and­UDP SERVER: run servers on <tcpport> and <udpport> sharing same key­value store.");
        System.out.println("alls <tcpport> <udpport> TCP, UDP, and RMI SERVER: run servers on <tcpport> and <udpport> sharing same key­value store.");
        System.out.println("rmic RMI Server.");
    }

}
