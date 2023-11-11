package com.hepl;

import com.hepl.protocol.VESPAP;

import java.util.Properties;

public class ServerPaymentApp {
    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        config.load(ServerPaymentApp.class.getClassLoader().getResourceAsStream("config.properties"));

        int port = Integer.parseInt(config.getProperty("server.port"));
        int poolSize = Integer.parseInt(config.getProperty("server.nbrThreads"));

        // Start thread server
        ThreadServer threadServer = new ThreadServer(port, new VESPAP(), poolSize);// Protocole needs to be added
        threadServer.start();

        // Waiting system (je suis pas convaincu mais bon)
        try {
            threadServer.join();
        } catch (InterruptedException e) {
            threadServer.interrupt();
        }
    }
}