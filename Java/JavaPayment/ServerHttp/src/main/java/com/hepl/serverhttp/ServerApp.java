package com.hepl.serverhttp;

import com.hepl.serverhttp.handlers.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        HttpServer server;

        try {
            // Create server
            server = HttpServer.create(new InetSocketAddress(8080), 0);

            // HTML Handler
            server.createContext("/", new HandlerHtml());
            server.createContext("/index", new HandlerHtml());
            server.createContext("/index.html", new HandlerHtml());

            // CSS Handler
            server.createContext("/css", new HandlerCss());
            // JS Handler
            server.createContext("/js", new HandlerJs());
            // Images Handler
            server.createContext("/images", new HandlerImages());
            // Ico Handler
            server.createContext("/favicon.ico", new HandlerIco());

            // API Handlers
            server.createContext("/getArticle", new API());
            server.createContext("/update", new API());

            // Starts server
            System.out.println("Démarrage serveur...");
            server.start();
        } catch (IOException e) {
            System.out.println("erreur: " + e.getMessage());
        }
    }
}
