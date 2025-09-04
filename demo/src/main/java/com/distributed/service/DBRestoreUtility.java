package com.distributed.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class DBRestoreUtility {

    // restore the current db to stored db, make sure the project could be repeated tested

    public static void restoreDatabase(String storedDbPath, String workingDbPath) {
        try {
            Path sourcePath = Paths.get(storedDbPath);
            Path targetPath = Paths.get(workingDbPath);
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Database restored successfully");
        } catch (Exception e) {
            System.err.println("Error restoring the database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
