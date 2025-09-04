package com.distributed.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// run this class first before implementing clients !
// This class makes sure every client has their own server (connection)

public class EMPServiceFactoryServer extends UnicastRemoteObject implements EMPServiceFactory {

    protected EMPServiceFactoryServer() throws RemoteException {
        super();
    }

    @Override
    public EMPService newEMPService() throws RemoteException {
        // create EMPServer here
        return new EMPServer();
    }

    public static void main(String[] args) {
        try {
            EMPServiceFactory factory = new EMPServiceFactoryServer();
                    Registry registry = LocateRegistry.createRegistry(1099);
        registry.bind("DistributedServiceFactory", factory);
        System.out.println("DistributedServiceFactory is ready.");
        } catch (Exception e) {
            System.err.println("EMPServiceFactory exception: " + e);
            e.printStackTrace();
        }
    }
}
