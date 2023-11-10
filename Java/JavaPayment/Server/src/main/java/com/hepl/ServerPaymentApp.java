package com.hepl;

import com.hepl.bridge.DbConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerPaymentApp {
    public static void main(String[] args) throws Exception{
        DbConnection connection = new DbConnection();

        ResultSet resultSet = connection.executeQuery("SELECT * FROM clients;");

        while(resultSet.next()){
            String pseudo = resultSet.getString("pseudo");
            System.out.println(pseudo);
        }

        connection.close();


        Properties config = new Properties();
        config.load(ServerPaymentApp.class.getClassLoader().getResourceAsStream("config.properties"));
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(Integer.getInteger(config.getProperty("server.port")));
        }
        catch (IOException e){
            System.out.println("Error in the threadPool creation :"+e.getMessage());
            return;
        }

    }
}