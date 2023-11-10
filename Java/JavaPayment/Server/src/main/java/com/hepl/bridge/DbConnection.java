package com.hepl.bridge;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DbConnection {
    private Connection connection;
    private static final String CONFIG_FILENAME = "config.properties";

    public DbConnection() throws SQLException, IOException {
        Properties config = new Properties();
        config.load(getClass().getClassLoader().getResourceAsStream(CONFIG_FILENAME));
        connection = DriverManager.getConnection(config.getProperty("db.url"), config.getProperty("db.user"), config.getProperty("db.password"));
    }

    public synchronized ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(query);
    }

    public synchronized int executeUpdate(String query) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(query);
    }


    public synchronized void close() throws SQLException {
        if (connection != null)
            connection.close();
    }
}
