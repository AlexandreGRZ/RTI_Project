package com.hepl.bridge;

import com.hepl.model.DateCustom;
import com.hepl.model.Facture;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DbConnection {
    private Connection connection;
    private static final String CONFIG_FILENAME = "config.properties";

    public DbConnection() throws SQLException, IOException {
        Properties config = new Properties();
        config.load(getClass().getClassLoader().getResourceAsStream(CONFIG_FILENAME));
        connection = DriverManager.getConnection(config.getProperty("db.url"), config.getProperty("db.user"), config.getProperty("db.password"));
    }

    public synchronized boolean login(String login, String password) throws SQLException {
        String query = "SELECT * FROM employees WHERE pseudo=? AND password=?;";
        PreparedStatement statement = connection.prepareStatement(query);
        // oui, les indices commencent à 1, pourquoi faire comme tout le monde
        // quand on peut mettre un peu de piquant dans notre vie et risquer des seg faults
        statement.setString(1, login);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();
        // If any result, then login successful
        return resultSet.next();
    }

    public synchronized ArrayList<Facture> getFactures(int idClient) throws SQLException {
        String query = "SELECT * FROM factures WHERE idClient=?;";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, idClient);
        ResultSet resultSet = statement.executeQuery();

        ArrayList<Facture> factures = new ArrayList<>();

        while (resultSet.next()) {
            int idFacture = resultSet.getInt("id");
            Date date = resultSet.getDate("date");
            float amount = resultSet.getFloat("montant");
            boolean payed = resultSet.getBoolean("paye");
            factures.add(new Facture(idFacture, date, amount, payed));
        }
        System.out.println(factures);

        return factures;
    }

    public synchronized boolean payFacture(int idFacture) throws SQLException {
        String query = "UPDATE factures SET paye=True WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, idFacture);

        return statement.executeUpdate() > 0;
    }


    public synchronized void close() throws SQLException {
        if (connection != null)
            connection.close();
    }
}
