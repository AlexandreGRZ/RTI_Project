package com.hepl;

import java.util.Properties;

// Import pour fonction test DB
import java.sql.ResultSet;
import com.hepl.bridge.DbConnection;

public class ServerPaymentApp {
    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        config.load(ServerPaymentApp.class.getClassLoader().getResourceAsStream("config.properties"));

        int port = Integer.parseInt(config.getProperty("server.port"));
        int poolSize = Integer.parseInt(config.getProperty("server.nbrThreads"));

        // Start thread server
        ThreadServer threadServer = new ThreadServer(port, null, poolSize);// Protocole needs to be added
        threadServer.start();

        // Wainting system (je suis pas convaincu mais bon)
        try{
            threadServer.join();
        }catch (InterruptedException e){
            threadServer.interrupt();
        }
    }



    private static void testDBConnection()throws Exception{
        DbConnection connection = new DbConnection();

        ResultSet resultSet = connection.executeQuery("SELECT * FROM clients;");

        while (resultSet.next()) {
            String pseudo = resultSet.getString("pseudo");
            System.out.println(pseudo);
        }

        connection.close();
    }
}