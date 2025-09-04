package com.distributed.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**  
 * Client implements this to receive retry notifications  
 */
public interface RetryListener extends Remote {

    void onRetry(int attempt) throws RemoteException;
}
