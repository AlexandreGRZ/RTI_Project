package com.hepl;

import com.hepl.protocol.VESPAP;
import com.hepl.protocol.VESPAPS;

import java.util.Properties;

public class serveurPayementAppSecure
{
    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        config.load(ServerPaymentApp.class.getClassLoader().getResourceAsStream("config.properties"));

        int port = Integer.parseInt(config.getProperty("server.port"));
        int poolSize = Integer.parseInt(config.getProperty("server.nbrThreads"));

        // Start thread server
        threadServerSecu threadServer = new threadServerSecu(port, new VESPAPS(), poolSize);// Protocole needs to be added
        threadServer.start();

        // Waiting system
        try {
            threadServer.join();
        } catch (InterruptedException e) {
            threadServer.interrupt();
        }
    }
}
