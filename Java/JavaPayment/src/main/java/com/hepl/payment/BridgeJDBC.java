package com.hepl.payment;

import java.sql.*;

public class BridgeJDBC {
    private Connection connection;
    private static final String SERVER = "";// à ajouter
    private static final String DB_NAME = "";
    private static String url = "jdbc:mysql://" + SERVER + "/" + DB_NAME;
    private static String user = "";
    private static String password = "";

    public BridgeJDBC() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
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
