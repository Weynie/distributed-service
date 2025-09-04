package com.distributed.service;

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.List;

public interface EMPService extends Remote {
    List<EMP> getAllEmployees() throws Exception;
    EMP findEmployeeById(String eno) throws Exception;
    int addNewEmployee(String eno, String ename, String title) throws Exception;
    int updateEmployee(String eno, String ename, String title) throws Exception;
    int deleteEmployee(String eno) throws Exception;
    // transaction methods
    void setAutoCommit(boolean autoCommit) throws Exception;
    void commit() throws Exception;
    void rollback() throws Exception;
    void close() throws Exception;
    /** Register a client-side callback for retry messages */
    void setRetryListener(RetryListener listener) throws RemoteException;
}