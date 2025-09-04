package com.distributed.service;

import java.sql.SQLException;
import java.util.List;
import org.sqlite.SQLiteErrorCode;

public class EMPDAOWrapper {
    private EMPDAO dao;

    public EMPDAOWrapper() { dao = new EMPDAO(); }

    public int addNewEmployeeWithCallback(String eno, String ename, String title,
                                          RetryListener listener) throws Exception {
        return executeWithRetry(() -> dao.addNewEmployee(eno, ename, title), listener);
    }

    public int updateEmployeeWithCallback(String eno, String ename, String title,
                                           RetryListener listener) throws Exception {
        return executeWithRetry(() -> dao.updateEmployee(eno, ename, title), listener);
    }

    public int deleteEmployeeWithCallback(String eno, RetryListener listener) throws Exception {
        return executeWithRetry(() -> dao.deleteEmployee(eno), listener);
    }

    public EMP findEmployeeByIdWithCallback(String eno, RetryListener listener) throws Exception {
        return executeWithRetry(() -> dao.findEmployeeById(eno), listener);
    }

    public List<EMP> getAllEmployeesWithCallback(RetryListener listener) throws Exception {
        return executeWithRetry(() -> dao.getAllEmployees(), listener);
    }

    public void setAutoCommit(boolean autoCommit) throws Exception {
        try {
            dao.setAutoCommit(autoCommit);
        } catch (Exception e) {
            throw new Exception("Error setting auto-commit: " + e.getMessage());
        }
    }
    
    public void commit() throws Exception {
        try {
            dao.commit();
        } catch (Exception e) {
            throw new Exception("Error committing transaction: " + e.getMessage());
        }
    }
    
    public void rollback() throws Exception {
        try {
            dao.rollback();
        } catch (Exception e) {
            throw new Exception("Error rolling back transaction: " + e.getMessage());
        }
    }
    
    public void close() throws Exception {
        try {
            dao.close();
        } catch (Exception e) {
            throw new Exception("Error closing connection: " + e.getMessage());
        }
    }

    // Generic retry helper for any DAO call:
    private <T> T executeWithRetry(CallableWithSQLException<T> action,
                                   RetryListener listener) throws Exception {
        final int MAX = 50, SLEEP_MS = 300;
        int tries = 0;
        while (true) {
            try {
                return action.call();
            } catch (SQLException e) {
                if (e.getErrorCode() == SQLiteErrorCode.SQLITE_BUSY.code && tries < MAX) {
                    tries++;
                    // call custom retry message print
                    if (listener != null) listener.onRetry(tries);
                    Thread.sleep(SLEEP_MS);
                } else {
                    throw e;
                }
            }
        }
    }

    // Functional interface to wrap SQLException-throwing calls
    @FunctionalInterface
    private interface CallableWithSQLException<V> {
        V call() throws SQLException;
    }
}