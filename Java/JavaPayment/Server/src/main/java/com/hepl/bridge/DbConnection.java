package com.hepl.bridge;

import java.sql.*;

public class DbConnection {
    private Connection connection;
    private static final String SERVER = "192.168.1.63";// VM ip
    private static final String DB_NAME = "PourStudent";
    private static final String URL = "jdbc:mysql://" + SERVER + "/" + DB_NAME;
    private static final String USER = "Student";
    private static final String PASSWORD = "PassStudent1_";

    public DbConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public synchronized ResultSet executeQuery(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public synchronized int executeUpdate(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(sql);
    }


    public synchronized void close() throws SQLException {
        if (connection != null)
            connection.close();
    }
}
