package com.hepl.serverhttp.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class HandlerHtml implements HttpHandler {

    private final String RESOURCES_PATH = System.getProperty("user.dir")+"\\ServerHttp\\src\\main\\resources";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();

        if(uriPath.equals("/"))
            uriPath = "index.html";

        if(uriPath.endsWith(".html")){
            File file = new File(RESOURCES_PATH+"\\html\\index.html");
            exchange.sendResponseHeaders(200, file.length());
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            OutputStream os = exchange.getResponseBody();
            Files.copy(file.toPath(), os);
            os.close();
            System.out.println("Envoi page html");
        }
        else if(uriPath.endsWith(".css")){
            File file = new File(RESOURCES_PATH+"\\css\\style.css");
            exchange.sendResponseHeaders(200, file.length());
            exchange.getResponseHeaders().set("Content-Type", "text/css");
            OutputStream os = exchange.getResponseBody();
            Files.copy(file.toPath(), os);
            os.close();
            System.out.println("Envoi page css");
        }
        else if(uriPath.endsWith(".js")){
            File file = new File(RESOURCES_PATH+"\\js\\app.js");
            exchange.sendResponseHeaders(200, file.length());
            exchange.getResponseHeaders().set("Content-Type", "text/jss");
            OutputStream os = exchange.getResponseBody();
            Files.copy(file.toPath(), os);
            os.close();
            System.out.println("Envoi page js");
        }else Erreur404(exchange);
    }

    private void Erreur404(HttpExchange exchange)throws IOException {
        String reponse = "fichier introuvable";
        exchange.sendResponseHeaders(404, reponse.length());
        exchange.getResponseHeaders().set("Content-Type", "text/plain");
        OutputStream os = exchange.getResponseBody();
        os.write(reponse.getBytes());
        os.close();
        System.out.println("KO");
    }
}
