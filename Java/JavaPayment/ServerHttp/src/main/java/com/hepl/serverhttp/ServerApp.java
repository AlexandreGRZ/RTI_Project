package com.hepl.serverhttp;

import com.hepl.serverhttp.handlers.API;
import com.hepl.serverhttp.handlers.HandlerHtml;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        HttpServer server;

        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", new HandlerHtml());
            server.createContext("/index", new HandlerHtml());
            server.createContext("/index.html", new HandlerHtml());

            server.createContext("/getArticle", new API());
            server.createContext("/update", new API());

            System.out.println("Démarrage serveur...");
            server.start();
        } catch (IOException e) {
            System.out.println("erreur: " + e.getMessage());
        }
    }
}
