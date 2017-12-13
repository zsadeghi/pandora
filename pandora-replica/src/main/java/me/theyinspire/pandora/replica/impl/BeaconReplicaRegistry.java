package me.theyinspire.pandora.replica.impl;

import me.theyinspire.pandora.core.client.Client;
import me.theyinspire.pandora.core.client.ClientConfiguration;
import me.theyinspire.pandora.core.client.UriClientConfigurationReader;
import me.theyinspire.pandora.core.client.impl.DefaultUriClientConfigurationReader;
import me.theyinspire.pandora.core.protocol.impl.DefaultProtocolRegistry;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.replica.Replica;
import me.theyinspire.pandora.replica.ReplicaRegistryInitializer;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (11/17/17, 1:04 AM)
 */
public class BeaconReplicaRegistry extends AbstractReplicaRegistry {

    private static final int BUFFER_SIZE = 1024;
    private final Map<String, Replica> replicaMap;
    private final BeaconListener listener;
    private final int beaconPort;
    private BeaconTransmitter transmitter;

    public BeaconReplicaRegistry(int beaconPort, final ReplicaRegistryInitializer initializer) {
        super(initializer);
        this.beaconPort = beaconPort;
        replicaMap = new ConcurrentHashMap<>();
        final UriClientConfigurationReader configurationReader = new DefaultUriClientConfigurationReader();
        listener = new BeaconListener(configurationReader, replicaMap, beaconPort);
        new Thread(listener).start();
    }

    @Override
    public Set<Replica> getReplicaSet() {
        return new HashSet<>(replicaMap.values());
    }

    @Override
    protected void onBeforeInit(String signature, String uri) {
        transmitter = new BeaconTransmitter(signature, uri, beaconPort);
        new Thread(transmitter).start();
    }

    @Override
    public void destroy(String signature) {
        listener.stop();
        transmitter.stop();
    }

    private static class BeaconListener implements Runnable {

        private final int beaconPort;
        private final AtomicBoolean running = new AtomicBoolean(true);
        private final UriClientConfigurationReader configurationReader;
        private final Map<String, Replica> replicaMap;

        private BeaconListener(UriClientConfigurationReader configurationReader, Map<String, Replica> replicaMap, int beaconPort) {
            this.beaconPort = beaconPort;
            this.configurationReader = configurationReader;
            this.replicaMap = replicaMap;
        }

        @Override
        public void run() {
            final DatagramSocket beaconSocket;
            try {
                beaconSocket = new DatagramSocket(null);
                beaconSocket.setReuseAddress(true);
                beaconSocket.bind(new InetSocketAddress(beaconPort));
            } catch (SocketException e) {
                throw new ServerException("Failed to start the beacon on port " + beaconPort, e);
            }
            try {
                while (running.get()) {
                    final byte[] buffer = new byte[BUFFER_SIZE];
                    final DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
                    try {
                        beaconSocket.receive(packet);
                    } catch (IOException e) {
                        throw new ServerException("Failed to receive data from the beacon", e);
                    }
                    final String data = new String(packet.getData(), 0, packet.getLength());
                    final String action = data.substring(0, 3);
                    final String signature = data.substring(4, data.indexOf('@'));
                    if (action.equals("add")) {
                        final String uri = data.substring(data.indexOf('@') + 1);
                        final ClientConfiguration configuration = configurationReader.read(uri);
                        final Client client = DefaultProtocolRegistry.getInstance().getClient(configuration.getProtocol(), configuration);
                        final Replica replica = new ImmutableReplica(signature, client);
                        replicaMap.put(signature, replica);
                    } else {
                        replicaMap.remove(signature);
                    }
                }
            } finally {
                beaconSocket.close();
            }
        }

        public void stop() {
            running.set(false);
        }

    }

    private static class BeaconTransmitter implements Runnable {

        public static final String BROADCAST_ADDRESS = "0.0.0.0";
        private final AtomicBoolean running = new AtomicBoolean(true);
        private final String signature;
        private final String uri;
        private final int beaconPort;

        private BeaconTransmitter(String signature, String uri, int beaconPort) {
            this.signature = signature;
            this.uri = uri;
            this.beaconPort = beaconPort;
        }

        @Override
        public void run() {
            final DatagramSocket beaconSocket;
            final String message = signature + "@" + uri;
            try {
                beaconSocket = new DatagramSocket();
                beaconSocket.setBroadcast(true);
                beaconSocket.setReuseAddress(true);
            } catch (SocketException e) {
                throw new ServerException("Failed to start beacon transmitter", e);
            }
            try {
                while (running.get()) {
                    final DatagramPacket packet = getPacket("add " + message);
                    try {
                        beaconSocket.send(packet);
                    } catch (IOException e) {
                        throw new ServerException("Failed to transmit beacon data for " + message, e);
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new ServerException("Interrupted while sleeping", e);
                    }
                }
                try {
                    beaconSocket.send(getPacket("del " + message));
                } catch (IOException e) {
                    throw new ServerException("Failed to unregister node " + message, e);
                }
            } finally {
                beaconSocket.close();
            }
        }

        private DatagramPacket getPacket(String message) {
            final DatagramPacket packet;
            final InetAddress address;
            try {
                address = InetAddress.getByName(BROADCAST_ADDRESS);
            } catch (UnknownHostException e) {
                throw new ServerException("Failed to send broadcast", e);
            }
            packet = new DatagramPacket(message.getBytes(), message.length(), address, beaconPort);
            return packet;
        }

        public void stop() {
            running.set(false);
        }

    }

}
