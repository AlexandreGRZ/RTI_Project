package com.hepl.serverhttp.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class HandlerHtml implements HttpHandler {

    private final String RESOURCES_PATH = System.getProperty("user.dir")+"\\ServerHttp\\src\\main\\resources\\html";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        if(requestPath.endsWith(".html")){
            String fichier = RESOURCES_PATH + requestPath.replace("/", "\\");
            System.out.println(fichier);
            File file = new File(fichier);
            if (file.exists()) {
                exchange.sendResponseHeaders(200, file.length());
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                OutputStream os = exchange.getResponseBody();
                Files.copy(file.toPath(), os);
                os.close();
                System.out.println("OK");

            }else Erreur404(exchange);
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
