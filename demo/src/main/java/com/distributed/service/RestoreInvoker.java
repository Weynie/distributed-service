package com.distributed.service;

// Restore and reset the db once called, to make db always orginaized

public class RestoreInvoker {
    public static void main(String[] args) {
        try {
            String dbUrl = "../staff.db";
            String storedPath = "../backup.db";
            DBRestoreUtility.restoreDatabase(storedPath, dbUrl);
        } catch (Exception e) {
            System.err.println("Backup error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
