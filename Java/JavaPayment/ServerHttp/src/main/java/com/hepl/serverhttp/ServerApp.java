package com.hepl.serverhttp;

import com.hepl.serverhttp.handler.HandlerHtml;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Handler;

public class ServerApp
{
    public static void main( String[] args )
    {
        HttpServer server;


        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", new HandlerHtml());

            System.out.println("Démarrage serveur...");
            server.start();
        } catch (IOException e) {
            System.out.println("erreur: "+e.getMessage());
        }
    }
}
