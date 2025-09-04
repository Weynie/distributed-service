package com.distributed.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class EMPDAO {
    private Connection conn;

    public EMPDAO() {
        try {
        conn = DBConnection.getConnection();
        } catch (SQLException e) {
        // wrap in unchecked so callers don't need to declare throws
            throw new IllegalStateException("Unable to open DB connection", e);
        }
    }

    public EMP findEmployeeById(String eno) throws SQLException {
        String query = "SELECT * FROM EMP WHERE ENO=?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, eno);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new EMP(
                        rs.getString("ENO"),
                        rs.getString("ENAME"),
                        rs.getString("TITLE")
                    );
                }
                return null;
            }
        }
    }

    public int addNewEmployee(String eno, String ename, String title) throws SQLException {
        String sql = "INSERT INTO EMP(ENO, ENAME, TITLE) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, eno);
            pstmt.setString(2, ename);
            pstmt.setString(3, title);
            return pstmt.executeUpdate();
        }
    }

    public int updateEmployee(String eno, String ename, String title) throws SQLException {
        String sql = "UPDATE EMP SET ENAME=?, TITLE=? WHERE ENO=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ename);
            pstmt.setString(2, title);
            pstmt.setString(3, eno);
            return pstmt.executeUpdate();
        }
    }

    public int deleteEmployee(String eno) throws SQLException {
        String sql = "DELETE FROM EMP WHERE ENO=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, eno);
            return pstmt.executeUpdate();
        }
    }

    public List<EMP> getAllEmployees() throws SQLException {
        List<EMP> list = new ArrayList<>();
        String sql = "SELECT * FROM EMP";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new EMP(
                    rs.getString("ENO"),
                    rs.getString("ENAME"),
                    rs.getString("TITLE")
                ));
            }
        }
        return list;
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        conn.setAutoCommit(autoCommit);
    }

    public void commit() throws SQLException {
        conn.commit();
    }

    public void rollback() throws SQLException {
        conn.rollback();
    }

    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
