package com.distributed.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EMPServiceFactory extends Remote {
    EMPService newEMPService() throws RemoteException;
}
