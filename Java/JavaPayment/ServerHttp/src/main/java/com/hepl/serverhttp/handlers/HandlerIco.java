package com.hepl.serverhttp.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import static com.hepl.serverhttp.handlers.Util.Erreur404;

public class HandlerIco implements HttpHandler {
    private final String RESOURCES_PATH = System.getProperty("user.dir") + "\\ServerHttp\\src\\main\\resources";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uriPath = exchange.getRequestURI().getPath();

        if (uriPath.endsWith(".ico")) {
            System.out.println("requête ico");
            File file = new File(RESOURCES_PATH + uriPath.replace("/", "\\"));
            exchange.sendResponseHeaders(200, file.length());
            exchange.getResponseHeaders().set("Content-Type", "image/x-icon");
            OutputStream os = exchange.getResponseBody();
            Files.copy(file.toPath(), os);
            os.close();
            System.out.println("Envoi favicon");
        } else Erreur404(exchange);

    }
}
