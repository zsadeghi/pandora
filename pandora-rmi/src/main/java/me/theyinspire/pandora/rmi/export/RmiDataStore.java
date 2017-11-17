package me.theyinspire.pandora.rmi.export;

import me.theyinspire.pandora.core.server.ServerConfiguration;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

/**
 * @author Zohreh Sadeghi (zsadeghi@uw.edu)
 * @since 1.0 (10/29/17, 2:52 PM)
 */
public interface RmiDataStore extends Remote {

    long size() throws RemoteException;

    boolean isEmpty() throws RemoteException;

    boolean store(String key, Serializable value) throws RemoteException;

    Serializable get(String key) throws RemoteException;

    boolean delete(String key) throws RemoteException;

    Set<String> keys() throws RemoteException;

    long truncate() throws RemoteException;

    boolean has(String key) throws RemoteException;

    Map<String, Serializable> all() throws RemoteException;

    String exit() throws RemoteException;

    String getUri(ServerConfiguration configuration) throws RemoteException;

    void lock(String key) throws RemoteException;

    void restore(String key) throws RemoteException;

    void unlock(String key) throws RemoteException;

    boolean locked(String key) throws RemoteException;

    String getSignature() throws RemoteException;

}
