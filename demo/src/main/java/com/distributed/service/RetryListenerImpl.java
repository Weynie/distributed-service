package com.distributed.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// the class ensures that retry message is printed at client side
public class RetryListenerImpl extends UnicastRemoteObject implements RetryListener {
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET  = "\u001B[0m";

    public RetryListenerImpl() throws RemoteException { super(); }

    @Override
    public void onRetry(int attempt) {
        System.out.println(YELLOW + "[Client] retry #" + attempt + RESET);
    }
}
