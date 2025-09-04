package com.distributed.service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class EMPServer extends UnicastRemoteObject implements EMPService {
    private EMPDAOWrapper empDAO;
    private RetryListener retryListener;

    protected EMPServer() throws RemoteException {
        super();
        empDAO = new EMPDAOWrapper();
    }

    @Override
    public void setRetryListener(RetryListener listener) throws RemoteException {
        this.retryListener = listener;
    }

    @Override
    public int addNewEmployee(String eno, String ename, String title) throws RemoteException {
        try {
            return empDAO.addNewEmployeeWithCallback(eno, ename, title, retryListener);
        } catch (Exception e) {
            throw new RemoteException("Insert Error: " + e.getMessage());
        }
    }

    @Override
    public int updateEmployee(String eno, String ename, String title) throws RemoteException {
        try {
            return empDAO.updateEmployeeWithCallback(eno, ename, title, retryListener);
        } catch (Exception e) {
            throw new RemoteException("Update Error: " + e.getMessage(), e);
        }
    }

    @Override
    public int deleteEmployee(String eno) throws RemoteException {
        try {
            return empDAO.deleteEmployeeWithCallback(eno, retryListener);
        } catch (Exception e) {
            throw new RemoteException("Delete Error: " + e.getMessage(), e);
        }
    }

    @Override
    public EMP findEmployeeById(String eno) throws RemoteException {
        try {
            return empDAO.findEmployeeByIdWithCallback(eno, retryListener);
        } catch (Exception e) {
            throw new RemoteException("Select Error: " + e.getMessage(), e);
        }
    }

    @Override
    public List<EMP> getAllEmployees() throws RemoteException {
        try {
            return empDAO.getAllEmployeesWithCallback(retryListener);
        } catch (Exception e) {
            throw new RemoteException("Select All Error: " + e.getMessage(), e);
        }
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws RemoteException {
        try { empDAO.setAutoCommit(autoCommit); } 
        catch (Exception e) { throw new RemoteException("AutoCommit Error: " + e.getMessage(), e); }
    }

    @Override
    public void commit() throws RemoteException {
        try { empDAO.commit(); } 
        catch (Exception e) { throw new RemoteException("Commit Error: " + e.getMessage()); }
    }

    @Override
    public void rollback() throws RemoteException {
        try { empDAO.rollback(); } 
        catch (Exception e) { throw new RemoteException("Rollback Error: " + e.getMessage(), e); }
    }

    @Override
    public void close() throws RemoteException {
        try { empDAO.close(); } 
        catch (Exception e) { throw new RemoteException("Close Error: " + e.getMessage(), e); }
    }
}