package com.hepl.serverhttp.db;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DbConnection {
    protected Connection connection;
    private static final String CONFIG_FILENAME = "config.properties";

    public DbConnection() throws SQLException, IOException {
        Properties config = new Properties();
        config.load(getClass().getClassLoader().getResourceAsStream(CONFIG_FILENAME));
        connection = DriverManager.getConnection(config.getProperty("db.url"), config.getProperty("db.user"), config.getProperty("db.password"));
    }

    public String getArticles() throws SQLException {
        ArrayList<Article> articles = new ArrayList<>();

        String query = "SELECT * FROM articles;";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            articles.add(new Article(resultSet.getInt("id"),
                    resultSet.getString("intitule"),
                    resultSet.getFloat("prix"),
                    resultSet.getInt("stock"),
                    resultSet.getString("image")));
        }
        String articlesJSON;
        try {
            articlesJSON = new ObjectMapper().writeValueAsString(articles);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return articlesJSON;
    }

    public boolean update(int id, float price, int quantity) throws SQLException{
        String query = "UPDATE articles SET prix=?, stock=? WHERE id=?;";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setFloat(1, price);
        statement.setInt(2, quantity);
        statement.setInt(3, id);

        return statement.executeUpdate() > 0;
    }

    public synchronized void close() throws SQLException {
        if (connection != null)
            connection.close();
    }
}
