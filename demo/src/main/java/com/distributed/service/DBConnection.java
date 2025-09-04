// DBConnection.java
package com.distributed.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.sqlite.SQLiteConfig;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
    // turn off the driver‐level busy‐handler so we see every lock error immediately:
        config.setBusyTimeout(0);
        String url = "jdbc:sqlite:staff.db";
        return DriverManager.getConnection(url, config.toProperties());
    }
}
