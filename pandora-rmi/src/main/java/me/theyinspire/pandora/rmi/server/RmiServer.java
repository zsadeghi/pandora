package me.theyinspire.pandora.rmi.server;

import me.theyinspire.pandora.core.datastore.DataStore;
import me.theyinspire.pandora.core.server.Server;
import me.theyinspire.pandora.core.server.error.ServerException;
import me.theyinspire.pandora.rmi.export.DelegatingRmiDataStore;
import me.theyinspire.pandora.rmi.export.RmiDataStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 2:36 PM)
 */
public class RmiServer implements Server {

    private static final Log LOG = LogFactory.getLog("pandora.server.rmi");
    private final int port;
    private String name;
    private Remote exportedObject;
    private final DataStore dataStore;

    public RmiServer(DataStore dataStore, int port) {
        this(dataStore, "dataStore", port);
    }

    public RmiServer(DataStore dataStore, String name, int port) {
        this.port = port;
        this.name = name;
        this.dataStore = dataStore;
    }

    @Override
    public void start() throws ServerException {
        LOG.info("Starting up the server");
        final RmiDataStore dataStore = new DelegatingRmiDataStore(this.dataStore, this);
        try {
            exportedObject = UnicastRemoteObject.exportObject(dataStore, port);
        } catch (RemoteException e) {
            throw new ServerException("Failed to export RMI object", e);
        }
        final RmiDataStore exportedDataStore = (RmiDataStore) exportedObject;
        final Registry registry;
        try {
            LOG.info("Locating RMI registry");
            registry = LocateRegistry.getRegistry();
        } catch (RemoteException e) {
            throw new ServerException("Failed to locate registry", e);
        }
        try {
            LOG.debug("Binding the exported object to the RMI registry");
            registry.bind(name, exportedDataStore);
        } catch (RemoteException e) {
            throw new ServerException("Failed to bind data store", e);
        } catch (AlreadyBoundException e) {
            throw new ServerException("Data store is already bound", e);
        }
    }

    @Override
    public void stop() throws ServerException {
        LOG.info("Initiating shutdown sequence");
        final Registry registry;
        try {
            registry = LocateRegistry.getRegistry();
        } catch (RemoteException e) {
            throw new ServerException("Failed to locate registry", e);
        }
        try {
            registry.unbind(name);
        } catch (RemoteException e) {
            throw new ServerException("Failed to unbind data store", e);
        } catch (NotBoundException e) {
            throw new ServerException("Data store is not bound", e);
        }
        try {
            UnicastRemoteObject.unexportObject(exportedObject, true);
        } catch (NoSuchObjectException e) {
            throw new ServerException("No exported object was found for data store", e);
        }
    }

}
